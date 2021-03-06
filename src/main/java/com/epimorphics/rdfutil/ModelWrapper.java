/******************************************************************
 * File:        ModelWrapper.java
 * Created by:  Dave Reynolds
 * Created on:  19 Mar 2012
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

import com.epimorphics.util.EpiException;
import com.epimorphics.util.PrefixUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;

/**
 * Wrapped version of a model from a dataset.
 * The wrapper provides uniform use of transaction/locking for safe access
 * plus automatic prefix expansion to simplify use from scripting languages.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class ModelWrapper {

    protected DatasetWrapper dsw;
    protected Model model;
    protected String language;          // Optional, used for choosing locale-appropriate lexical forms
    protected static final String DEFAULT_LANGUAGE = "en";
    
    public ModelWrapper(DatasetWrapper dataset, Model model) {
        this.dsw = dataset;
        this.model = model;
    }

    /**
     * Create a model wrapper around the default model of the dataset
     */
    public ModelWrapper(DatasetWrapper dataset) {
        this(dataset, dataset.getDataset().getDefaultModel());
    }


    /**
     * Create a model wrapper around a named model of the dataset
     */
    public ModelWrapper(DatasetWrapper dataset, String modelname) {
        this(dataset, dataset.getDataset().getNamedModel(modelname));
    }

    /**
     * Create a model wrapper round a simple model, creating a wrapped
     * dataset to hold it. Uses the global default modal instance.
     */
    public ModelWrapper(Model model) {
        this(new DatasetWrapper(DatasetFactory.create(model)), model);
    }

    /**
     * Set a language code (2-letter ISO639 code) which will be used to
     * guide choice of lexical form in e.g. node.getName()
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Return a language code (2-letter ISO639 code) which can be used to
     * guide choice of lexical form in e.g. node.getName()
     */
    public String getLanguage() {
        return language == null ? DEFAULT_LANGUAGE : language;
    }
    
    public Model getModel() {
        return model;
    }

    public DatasetWrapper getDatasetWrapper() {
        return dsw;
    }

    public Dataset getDataset() {
        return dsw.getDataset();
    }

    public String shortForm(String uri) {
        return PrefixUtils.merge(model, dsw.getPrefixes()).shortForm(uri);
    }

    public String expandPrefix(String prefix) {
        return PrefixUtils.merge(model, dsw.getPrefixes()).expandPrefix(prefix);
    }

    /**
     * Return a wrapped node from this Model.
     * @param spec can be a curie or URI for a resource, or an existing RDFNode or RDFNodeWrapper - possibly from some other model
     */
    public RDFNodeWrapper getNode(Object spec) {
        if (spec instanceof RDFNodeWrapper) {
            return new RDFNodeWrapper(this, ((RDFNodeWrapper)spec).asRDFNode());
        } else if (spec instanceof RDFNode) {
            return new RDFNodeWrapper(this, (RDFNode)spec);
        } else {
            Resource r = getResource(spec);
            if (r == null) {
                return null;
            } else {
                return new RDFNodeWrapper(this, r);
            }
        }
    }


    /**
     * Return a resource (not wrapped) from this Model.
     * @param spec can be a curie or URI for a resource, or an existing RDFNode or RDFNodeWrapper - possibly from some other model
     */
    public Resource getResource(Object spec) {
        if (spec instanceof String) {
            return model.getResource( expandPrefix((String)spec) );
        } else if (spec instanceof RDFNode) {
            return ((RDFNode)spec).inModel(model).asResource();
        } else if (spec instanceof RDFNodeWrapper) {
            return ((RDFNodeWrapper)spec).asRDFNode().inModel(model).asResource();
        } else if (spec == null) {
            return null;
        } else {
            throw new EpiException("getNode only handles strings, RDFNodes or RDFNodeWrappers - found " + spec);
        }
    }


    /** Lock the dataset for reading */
    public void lock() {
        dsw.lock();
    }

    /** Lock the dataset for write */
    public void lockWrite() {
        dsw.lockWrite();
    }

    /** Unlock the dataset */
    public synchronized void unlock() {
        dsw.unlock();
    }


    /** Return a list of at most N subjects in this model */
    public List<RDFNodeWrapper> listSubjects(int N) {
        List<RDFNodeWrapper> results = new ArrayList<RDFNodeWrapper>( Math.min(N, 100) );
        int count = 0;
        try {
            lock();
            ResIterator subjs = model.listSubjects();
            while (subjs.hasNext() && count < N) {
                results.add( new RDFNodeWrapper(this, subjs.next()) );
                count++;
            }
            subjs.close();
        } finally {
            unlock();
        }
        return results;
    }

    /** Return a list of at most N subjects in this model */
    public List<RDFNodeWrapper> listSubjects() {
        return listSubjects(Integer.MAX_VALUE);
    }

    /**
     * Return a merged prefix mapping that includes and prefixes defined for
     * this model plus any global ones which have been registered with
     * the dataset wapper
     */
    public PrefixMapping getPrefixes() {
        return PrefixUtils.merge(model, dsw.getPrefixes());
    }

    // Query support
    // -------------

    /**
     * Thread/transaction safe select query.
     * @param query query string to which query prefixes will be added from model and the wrapped dataset
     * @return memory copy of the result set
     */
    public ResultSetRewindable querySelect(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery, model);
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
     * @param query query string to which query prefixes will be added from model and the wrapped dataset
     * @return result of aks
     */
    public boolean queryAsk(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery, model);
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
     * @param query query string to which query prefixes will be added from model and the wrapped dataset
     * @return constructed in-memory model
     */
    public ModelWrapper queryConstruct(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery, model);
        lock();
        try {
            return new ModelWrapper( dsw, qexec.execConstruct() );
        } finally {
            qexec.close();
            unlock();
        }
    }

    /**
     * Thread/transaction safe select query returning wrapped bindings.
     * @param query query string to which query prefixes will be added from model and the wrapped dataset
     */
    public List<Map<String, RDFNodeWrapper>> querySelectW(String query) {
        String expandedQuery = expandQuery(query);
        QueryExecution qexec = QueryExecutionFactory.create(expandedQuery, model);
        lock();
        try {
            return wrapResultSet( qexec.execSelect() );
        } finally {
            qexec.close();
            unlock();
        }
    }

    public List<Map<String, RDFNodeWrapper>> wrapResultSet(ResultSet rs){
        List<Map<String, RDFNodeWrapper>> result = new ArrayList<Map<String,RDFNodeWrapper>>();
        while (rs.hasNext()) {
            QuerySolution soln = rs.next();
            Map<String, RDFNodeWrapper> map = new HashMap<String, RDFNodeWrapper>();
            for (Iterator<String> ni = soln.varNames(); ni.hasNext(); ) {
                String name = ni.next();
                RDFNode node = soln.get(name);
                map.put(name, node == null ? null :new RDFNodeWrapper(this, node));
            }
            result.add( map );
        }
        return result;
    }

    public String expandQuery(String query) {
        return PrefixUtils.expandQuery(query, getPrefixes());
    }

}
