/******************************************************************
 * File:        ModelWrapper.java
 * Created by:  Dave Reynolds
 * Created on:  18 Apr 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.epimorphics.util.EpiException;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.util.Closure;

/**
 * Provides convenient access to an RDF Model (a graph) for use
 * from scripting languages. Provides access to the underlying
 * Model object along with conveniece functions to make it
 * easier to traverse the model from scripts and templates.
 * The name is a slight misnomer because we in fact allow the underlying
 * structure to be a DataSet (where we point to the default graph) or
 * a standalone model.
 *
 * <ul>
 *  <li>access to nodes via curies (using shared prefix manager to simplify),</li>
 *  <li>succinct sparql query returning easy to script result lists,</li>
 * </ul>
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class ModelWrapper {

    protected Model model;
    protected Dataset dataset;
    protected PrefixManager pm;
    boolean   singleModel = true;

    /**
     * Constructor
     * @param model  the model to be wrapped
     * @param dataset the dataset (e.g. DataRepository) from which the model was taken, if any, used for locking
     * @param pm     prefix manager to use
     */
    public ModelWrapper(Model model, Dataset dataset, PrefixManager pm) {
        this.model = model;
        this.dataset = dataset;
        this.pm = pm;
        singleModel = true;
    }

    /**
     * Constructor
     * @param dataset the dataset to be wrapped
     * @param pm     prefix manager to use
     */
    public ModelWrapper(Dataset dataset, PrefixManager pm) {
        this.dataset = dataset;
        this.model = dataset.getDefaultModel();
        this.pm = pm;
        singleModel = false;
    }

    /** Return the Jena model object, may be null */
    public Model getModel() {
        return model;
    }

    /** Return the Jena dataset object, may be null */
    public Dataset getDataset() {
        return dataset;
    }

    /** Return true if this wrapper has an associated dataset */
    public boolean hasDataset() {
        return dataset != null;
    }

    /** Lock the model/dataset for reading */
    public void lock() {
        if (hasDataset()) {
            dataset.asDatasetGraph().getLock().enterCriticalSection(Lock.READ);
        } else {
            model.enterCriticalSection(Lock.READ);
        }
    }

    /** Lock the model/dataset for write */
    public void lockWrite() {
        if (hasDataset()) {
            dataset.asDatasetGraph().getLock().enterCriticalSection(Lock.WRITE);
        } else {
            model.enterCriticalSection(Lock.READ);
        }
    }

    /** Unlock the model/dataset */
    public void unlock() {
        if (hasDataset()) {
            dataset.asDatasetGraph().getLock().leaveCriticalSection();
        } else {
            model.leaveCriticalSection();
        }
    }

    /** Return the node corresponding to the given URI, curie or script-curie or pseudo-bnode */
    public RDFNodeWrapper getNode(String curie) {
        Resource r = null;
        if (curie.startsWith("_:")) {
            // An attempt at bNode round tripping for browsing purposes only
            r =  new ResourceImpl( AnonId.create(curie.substring(2)) ).inModel(model);
        } else {
            String uri = expand(curie);
            r = model.createResource(uri);
        }
        return new RDFNodeWrapper( r, this);
    }

    /** Shorten a URI to curie form using this model or fall-back globals */
    public String shorten(String uri) {
        String s = model.shortForm(uri);
        if (pm != null && s.length() == uri.length()) {
            s = pm.shorten(uri);
        }
        return s;
    }

    /** Expand a curi to a URI using this maodel or fall-back globals */
    public String expand(String curie) {
        String uri = model.expandPrefix(curie);
        if (pm != null && uri.length() == curie.length()) {
            uri = pm.expand(curie);
        }
        return uri;
    }

    /** Return a list of at most N subjects in this model */
    public List<RDFNodeWrapper> listSubjects(int N) {
        List<RDFNodeWrapper> results = new ArrayList<RDFNodeWrapper>( Math.min(N, 100) );
        int count = 0;
        lock();
        ResIterator subjs = model.listSubjects();
        while (subjs.hasNext() && count < N) {
            results.add( new RDFNodeWrapper( subjs.next(), this) );
            count++;
        }
        subjs.close();
        unlock();
        return results;
    }

    /** Return a list of at most N subjects in this model */
    public List<RDFNodeWrapper> listSubjects() {
        return listSubjects(Integer.MAX_VALUE);
    }

    protected static Object asValue(RDFNode n, ModelWrapper mw) {
        if (n.isLiteral()) {
            return n.asLiteral().getValue();
        } else {
            return new RDFNodeWrapper(n, mw);
        }
    }

    public static final String ITEM_VAR = "item";
    public static final String THIS_VAR = "?this";

    /**
     * Run a SPARQL query on the model, returning a list of maps of all result values.
     * Literal values will be return as wrapped literal nodes.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query.
     * @param queryS the query to be run
     * @param binding  an initial value for the ?this variable to allow queries to be
     * related to an existing node
     */
    public List<Map<String, RDFNodeWrapper>> query(String queryS, Object thisBinding) {
        List<Map<String, RDFNodeWrapper>> results = new ArrayList<Map<String,RDFNodeWrapper>>();
        lock();
        QueryExecution qexec = runQuery(queryS, thisBinding);
        try {
            ResultSet resultset = qexec.execSelect();
            while (resultset.hasNext()) {
                Map<String, RDFNodeWrapper> result = new HashMap<String, RDFNodeWrapper>();
                QuerySolution soln = resultset.nextSolution();
                Iterator<String> i = soln.varNames();
                while (i.hasNext()) {
                    String var = i.next();
                    RDFNode n = soln.get(var);
                    if (n != null)  result.put(var,  new RDFNodeWrapper(n, this) );
                }
                results.add(result);
            }
        } finally {
            qexec.close();
            unlock();
        }
        return results;
    }


    /**
     * Run a SPARQL query on the model, returning a list of maps of all result values.
     * Literal values will be return as plain java objects.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query.
     * @param queryS the query to be run
     * @param binding  an initial value for the ?this variable to allow queries to be
     * related to an existing node
     */
    public List<Map<String, Object>> queryValues(String queryS, Object thisBinding) {
        lock();
        QueryExecution qexec = runQuery(queryS, thisBinding);
        try {
            return wrapResultSet( qexec.execSelect(), this);
        } finally {
            qexec.close();
            unlock();
        }
    }

    /**
     * Run a SPARQL query on the model, returning a list of maps of all result values.
     * Literal values will be return as wrapped literal nodes.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager.
     */
    public List<Map<String, RDFNodeWrapper>>  query(String queryS) {
        return query(queryS, null);
    }

    /**
     * Run a SPARQL query on the model, returning a list of maps of all result values.
     * Literal values will be return as plain java objects.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager.
     */
    public List<Map<String, Object>>  queryValues(String queryS) {
        return queryValues(queryS, null);
    }

    /**
     * Run a SPARQL query on the model, returning a list of all
     * matches of the ?item variable.
     * Literal values in the result will be wrapped.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query.
     * @param queryS the query to be run
     * @param binding  an initial value for the ?this variable to allow queries to be
     * related to an existing node
     */
    public List<RDFNodeWrapper> queryList(String queryS, Object thisBinding) {
        List<RDFNodeWrapper> results = new ArrayList<RDFNodeWrapper>();
        lock();
        QueryExecution qexec = runQuery(queryS, thisBinding);
        try {
            ResultSet resultset = qexec.execSelect();
            while (resultset.hasNext()) {
                QuerySolution soln = resultset.nextSolution();
                RDFNode node = soln.get(ITEM_VAR);
                if (node != null) {
                    results.add( new RDFNodeWrapper(node, this) );
                }
            }
        } finally {
            qexec.close();
            unlock();
        }
        return results;
    }

    /**
     * Run a SPARQL query on the model, returning a list of all
     * matches of the ?item variable.
     * Literal values in the result will be returned as plain java objects.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query.
     * @param queryS the query to be run
     * @param binding  an initial value for the ?this variable to allow queries to be
     * related to an existing node
     */
    public List<Object> queryListValues(String queryS, Object thisBinding) {
        List<Object> results = new ArrayList<Object>();
        lock();
        QueryExecution qexec = runQuery(queryS, thisBinding);
        try {
            ResultSet resultset = qexec.execSelect();
            while (resultset.hasNext()) {
                QuerySolution soln = resultset.nextSolution();
                RDFNode node = soln.get(ITEM_VAR);
                if (node != null) {
                    results.add( asValue(node, this) );
                }
            }
        } finally {
            qexec.close();
            unlock();
        }
        return results;
    }

    /**
     * Run a SPARQL query on the model, returning a list of all
     * matches of the ?item variable.
     * Literal values in the result will be wrapped.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix
     * declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query.
     */
    public List<RDFNodeWrapper> queryList(String queryS) {
        return queryList(queryS, null);
    }

    /**
     * Run a SPARQL query on the model, returning a list of all
     * matches of the ?item variable.
     * Literal values in the result will be returned as plain java objects.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix
     * declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query.
     */
    public List<Object> queryListValues(String queryS) {
        return queryListValues(queryS, null);
    }

    protected String normalizeQuery(String query) {
        String result = query.trim();
        if ( ! regex.matcher(result.toLowerCase()).find()) {
            result = "SELECT * WHERE " + result;
        }
        if (pm != null) {
            return pm.expandQuery(result, model); 
        } else {
            return result;
        }
    }
    static Pattern regex = Pattern.compile( "(select|describe|ask|construct)", Pattern.MULTILINE);


    protected RDFNode normalizeValue(Object thisBinding) {
        if (thisBinding == null) return null;
        if (thisBinding instanceof RDFNodeWrapper) {
            return ((RDFNodeWrapper)thisBinding).node;
        } else if (thisBinding instanceof RDFNode) {
            return (RDFNode) thisBinding;
        } else {
            return ResourceFactory.createTypedLiteral(thisBinding);
        }
    }

    /**
     * Execute a SPARQL query (which will be prefix-normalized before use) with an optional
     * binding for the "this" variable. Caller is responsible for locking.
     * @return
     */
    public QueryExecution runQuery(String queryS, Object thisBinding) {
        RDFNode value = normalizeValue(thisBinding);
        String select = normalizeQuery(queryS);
        Query query = QueryFactory.create(select) ;
        QueryExecution qexec = null;
        if (value == null) {
            if (!singleModel) {
                qexec = QueryExecutionFactory.create(query, dataset) ;
            } else {
                qexec =  QueryExecutionFactory.create(query, model) ;
            }
        } else {
            QuerySolutionMap binding = new QuerySolutionMap();
            binding.add(THIS_VAR, value);
            if (!singleModel) {
                qexec = QueryExecutionFactory.create(query, dataset, binding) ;
            } else {
                qexec =  QueryExecutionFactory.create(query, model, binding) ;
            }
        }
        return qexec;
    }

    public static List<Map<String, Object>> wrapResultSet(ResultSet resultset, ModelWrapper mw) {
        List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
        while (resultset.hasNext()) {
            Map<String, Object> result = new HashMap<String, Object>();
            QuerySolution soln = resultset.nextSolution();
            Iterator<String> i = soln.varNames();
            while (i.hasNext()) {
                String var = i.next();
                RDFNode n = soln.get(var);
                if (n != null)  result.put(var, asValue(n, mw) );
            }
            results.add(result);
        }
        return results;
    }

    // Support for model updating - caller should ensure a write lock surrounding whole set of operations

    /**
     * Add a prefix declaration, the value is taken from the prefix manager
     * No write locking - caller should ensure lock is taken.
     */
    public String addPrefix(String prefix) {
        String probe = prefix + ":";
        String uri = expand(probe);
        if (uri.equals(probe)) {
            throw new EpiException("No expansion found for prefix - " + prefix);
        }
        model.setNsPrefix(prefix, uri);
        return uri;
    }

    /**
     * Add a prefix declaration, also register with the prefix manager
     * No write locking - caller should ensure lock is taken.
     */
    public String addPrefix(String prefix, String uri) {
        model.setNsPrefix(prefix, uri);
        pm.registerPrefix(prefix, uri);
        return uri;
    }

    /**
     * Construct a wrapped resource object.
     * @param name  can be an existing wrapped, or unwrapped resource, a uri string or a curi
     */
    public RDFNodeWrapper getResource(Object name) {
        return new RDFNodeWrapper( asResource(name), this);
    }

    /**
     * Takes a uri string, curi, wrapped or unwrapped resource and returns the corresponding Resource object.
     */
    Resource asResource(Object arg) {
        if (arg instanceof String) {
            return model.createResource( expand((String)arg) );
        } else if (arg instanceof Resource) {
            return ((Resource)arg).inModel(model);
        } else if (arg instanceof RDFNodeWrapper) {
            return ((RDFNodeWrapper)arg).asResource().inModel(model);
        } else {
            return asResource(arg.toString());
        }
    }

    /**
     * Takes a uri string, curi, wrapped or unwrapped resource and returns the corresponding Resource object.
     */
    Property asProperty(Object arg) {
        if (arg instanceof Property) {
            return (Property) arg;
        } else {
            Resource r = asResource(arg);
            if (r != null) {
                return ResourceFactory.createProperty( r.getURI() );
            } else {
                return null;
            }
        }
    }

    /**
     * Add new statements to the model from a Turtle source.
     * No write locking - caller should ensure lock is taken.
     */
    public void insertTurtle(String src) {
        StringReader r = new StringReader( pm.prefixHeader(src, model) + src );
        model.add(  ModelFactory.createDefaultModel().read(r, null, "Turtle") );
    }

    /**
     * Return a serialization of the whole model in turtle.
     * Use only for debugging over small models
     */
    public String asTurtle() {
        StringWriter writer = new StringWriter();
        model.write(writer, "Turtle");
        return writer.getBuffer().toString();
    }

    /**
     * Return a new wrapped model contained a (closed, bounded) description of the given resource
     */
    public ModelWrapper describeResource(Object resource) {
        Resource r = asResource(resource);
        Model m = Closure.closure(r, false);
        m.setNsPrefixes( model );
        return new ModelWrapper(m, dataset, pm);
    }

    /**
     * Wrap a new model, using the same DataSet and PrefixManager as this model
     * @param m The new model to wrap
     * @return A new ModelWrapper instance
     */
    public ModelWrapper wrapModel( Model m ) {
        return new ModelWrapper( m, dataset, pm );
    }
}
