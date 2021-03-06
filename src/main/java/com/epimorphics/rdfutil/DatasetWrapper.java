/******************************************************************
 * File:        DatasetWrapper.java
 * Created by:  Dave Reynolds
 * Created on:  3 Dec 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.epimorphics.util.PrefixUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.Lock;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.tdb.TDB;

/**
 * Wrap up a dataset to support script-friendly access. See ModelWrapper and
 * RDFNodeWrapper for the more interesting functionality.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class DatasetWrapper {
    public static final String TDB_UNION_GRAPH_NAME = "urn:x-arq:UnionGraph";

    protected Dataset dataset;
    protected boolean inWrite = false;
    protected boolean unionDefault = false;
    protected PrefixMapping prefixes = null;

    public DatasetWrapper(Dataset dataset, boolean unionDefault, PrefixMapping prefixes) {
        this.dataset = dataset;
        this.unionDefault = unionDefault;
        this.prefixes = prefixes;
    }

    public DatasetWrapper(Dataset dataset, boolean unionDefault) {
        this(dataset, unionDefault, null);
    }

    public DatasetWrapper(Dataset dataset) {
        this(dataset, false, null);
    }

    /**
     * If the unionDefault flag is set to try then getDefaultModel will attempt
     * to use the union model from the dataset and SPARQL queries
     * will be issued with unionDefaultGraph set to true. Only meaningful for TDB-backed datasets.
     */
    public void setUnionDefault(boolean unionDefault) {
        this.unionDefault = unionDefault;
    }

    public boolean getUnionDefault() {
        return unionDefault;
    }

    /**
     * Provide a prefix mapping for short names that will augment any per-model prefixes when
     * used from the wrapper APIs. Useful for simplify sparql queries and node link following.
     */
    public void setPrefixes(PrefixMapping prefixes) {
        this.prefixes = prefixes;
    }

    public PrefixMapping getPrefixes() {
        return prefixes;
    }

    /** Get the default graph as a Jena Model */
    public ModelWrapper getDefaultModelW() {
        lock();
        try {
            if (unionDefault) {
                return new ModelWrapper(this, dataset.getNamedModel(TDB_UNION_GRAPH_NAME));
            } else {
                return new ModelWrapper(this, dataset.getDefaultModel());
            }
        } finally {
            unlock();
        }
    }

    /** Get a graph by name as a Jena Model */
    public ModelWrapper getNamedModelW(String uri) {
        return new ModelWrapper(this, dataset.getNamedModel(uri));
    }

    public Dataset getDataset() {
        return dataset;
    }

    /** Lock the dataset for reading */
    public synchronized void lock() {
        if (dataset.supportsTransactions()) {
            dataset.begin(ReadWrite.READ);
        } else {
            dataset.asDatasetGraph().getLock().enterCriticalSection(Lock.READ);
        }
    }

    /** Lock the dataset for write */
    public synchronized void lockWrite() {
        if (dataset.supportsTransactions()) {
            dataset.begin(ReadWrite.WRITE);
            inWrite = true;
        } else {
            dataset.asDatasetGraph().getLock().enterCriticalSection(Lock.WRITE);
        }
    }

    /** Unlock the dataset */
    public synchronized void unlock() {
        if (dataset.supportsTransactions()) {
            if (inWrite) {
                dataset.commit();
                inWrite = false;
            }
            dataset.end();
        } else {
            dataset.asDatasetGraph().getLock().leaveCriticalSection();
        }
    }

    /** Unlock the dataset, aborting the transaction. Only useful if the dataset is transactional */
    public synchronized void abort() {
        if (dataset.supportsTransactions()) {
            if (inWrite) {
                dataset.abort();
                inWrite = false;
            }
            dataset.end();
        } else {
            dataset.asDatasetGraph().getLock().leaveCriticalSection();
        }
    }

    // Query support
    // -------------

    /**
     * Thread/transaction safe select query.
     * @param query query string to which query prefixes will be added if available
     * @return memory copy of the result set
     */
    public ResultSetRewindable querySelect(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery);
        if (unionDefault) qexec.getContext().set(TDB.symUnionDefaultGraph, true) ;
        lock();
        try {
            return ResultSetFactory.copyResults( qexec.execSelect() );
        } finally {
            qexec.close();
            unlock();
        }
    }

    /**
     * Thread/transaction safe ask query.
     * @param query query string to which query prefixes will be added if available
     * @return result of aks
     */
    public boolean queryAsk(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery);
        if (unionDefault) qexec.getContext().set(TDB.symUnionDefaultGraph, true) ;
        lock();
        try {
            return ( qexec.execAsk() );
        } finally {
            qexec.close();
            unlock();
        }
    }


    /**
     * Thread/transaction safe construct query.
     * @param query query string to which query prefixes will be added if available
     * @return constructed in-memory model
     */
    public ModelWrapper queryConstruct(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery);
        if (unionDefault) qexec.getContext().set(TDB.symUnionDefaultGraph, true) ;
        lock();
        try {
            return new ModelWrapper( this, qexec.execConstruct() );
        } finally {
            qexec.close();
            unlock();
        }
    }

    /**
     * Thread/transaction safe select query returning wrapped bindings.
     * @param query query string to which query prefixes will be added if available
     */
    public List<Map<String, RDFNodeWrapper>> querySelectW(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery);
        lock();
        try {
            return wrapResultSet( qexec.execSelect() );
        } finally {
            qexec.close();
            unlock();
        }
    }

    protected List<Map<String, RDFNodeWrapper>> wrapResultSet(ResultSet rs){
        ModelWrapper mw = getDefaultModelW();
        List<Map<String, RDFNodeWrapper>> result = new ArrayList<Map<String,RDFNodeWrapper>>();
        while (rs.hasNext()) {
            QuerySolution soln = rs.next();
            Map<String, RDFNodeWrapper> map = new HashMap<String, RDFNodeWrapper>();
            for (Iterator<String> ni = soln.varNames(); ni.hasNext(); ) {
                String name = ni.next();
                RDFNode node = soln.get(name);
                map.put(name, node == null ? null :new RDFNodeWrapper(mw, node));
            }
            result.add( map );
        }
        return result;
    }

    protected String expandQuery(String query) {
        if (prefixes != null) {
            return PrefixUtils.expandQuery(query, prefixes);
        } else {
            return query;
        }
    }


}
