/******************************************************************
 * File:        QueryUtil.java
 * Created by:  Dave Reynolds
 * Created on:  6 Aug 2012
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

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.util.EpiException;
import com.epimorphics.util.PrefixUtils;
import com.epimorphics.webapi.PageInfo;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.XSD;

import static com.epimorphics.util.NameUtils.escape;

/**
 * Random small utilities to help with SPARQl queries.
 */
public class QueryUtil {

    private static final Logger log = LoggerFactory.getLogger( QueryUtil.class );

    /**
     * Inject strings into a SPARQL query replacing each ${i} with the corresponding element from the arg list.
     * Purely syntactic. Up to the caller to protected any sensitive characters.
     */
    public static String substituteInQuery(String query, Object... strings) {
        String result = query;
        for (int i = 0; i < strings.length; i++) {
            Object subs = strings[i];
//            result = result.replaceAll("\\$\\{" + i + "\\}", subs == null ? "null" : subs.toString());
            result = result.replace("${" + i + "}", subs == null ? "null" : subs.toString());
        }
        return result;
    }

    /**
     * Add limit/offset to query based on paging specification.
     */
    public static String addPageLimits(String query, PageInfo pageInfo) {
        String expandedQuery = query;
        if (pageInfo.getOffset() != 0) {
            expandedQuery += " OFFSET " + pageInfo.getOffset();
        }
        expandedQuery += " LIMIT " + pageInfo.getPageSize();
        return expandedQuery;

    }

    /**
     * Take a column from result set and extract it as a list of resources.
     * Skips any non-resource results.
     */
    // TODO clean this up, have generalized verion later in this class
    public static List<Resource> resultsFor(ResultSet results, String varname) {
        List<Resource> resultList = new ArrayList<Resource>();
        while (results.hasNext()) {
            RDFNode result = results.nextSolution().get(varname);
            if (result.isResource()) {
                resultList.add(result.asResource());
            }
        }
        return resultList;
    }

    /**
     * Utility to declare bindings in code. Called with an array of objects,
     * each pairs of sequential objecst will be interpreted as string denoting
     * a variable name, and a value denoting an RDFNode via {@link RDFUtil#asRDFNode(Object)}.
     *
     * @param bindings An array of Objects, which will be taken in pairs to be a string
     * var name and an object to encode as an RDF node.
     * @return A {@link QuerySolutionMap} in which the keys are bound to their given values
     */
    public static QuerySolutionMap createBindings( Object... bindings ) {
        QuerySolutionMap qsm = new QuerySolutionMap();

        try {
            for (int i = 0; i < bindings.length; i += 2) {
                qsm.add( (String) bindings[i], RDFUtil.asRDFNode( bindings[i+1] ) );
            }
        }
        catch (Exception e) {
            throw new EpiException( "Bindings must be declared as a string key followed by the binding value.", e );
        }

        return qsm;
    }

    /**
     * Create a {@link QueryExecution} for executing the given query against the
     * given model. Other variants allow prefix mappings and initial variable bindings to be specified.
     * Uses the default common prefixes.
     *
     * @param m The model to run queries against
     * @param query The query to run
     * @return A query execution object
     */
    public static QueryExecution createQueryExecution( Model m, String query ) {
        return createQueryExecution( m, query, null, (QuerySolutionMap) null );
    }

    /**
     * Create a {@link QueryExecution} for executing the given query against the
     * given model. This variant allows additional prefixes for the query to be passed in,
     * together with bindings for variables.
     *
     * @param m The model to run queries against
     * @param query The query to run
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null. If null is given, the default
     * prefixes from {@link PrefixUtils#commonPrefixes()} will be used
     * @param bindings Optional array of name/value pairs to use as initial variable bindings
     * @return A query execution object
     */
    public static QueryExecution createQueryExecution( Model m, String query, PrefixMapping pm, Object... bindings ) {
        return createQueryExecution( m, query, pm, (QuerySolutionMap)((bindings == null) ? null : createBindings( bindings )) );
    }

    /**
     * Create a {@link QueryExecution} for executing the given query against the
     * given model. This variant allows additional prefixes for the query to be passed in,
     * together with bindings for variables.
     *
     * @param m The model to run queries against
     * @param query The query to run
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null. If null is given, the default
     * prefixes from {@link PrefixUtils#commonPrefixes()} will be used
     * @param bindings Optional {@link QuerySolutionMap} containing initial bindings for query variables
     * @return A query execution object
     */
    public static QueryExecution createQueryExecution( Model m, String query, PrefixMapping pm, QuerySolutionMap bindings ) {
        Query q = new Query();

        // preference order: given prefixes, m's prefixes, common prefixes
        PrefixMapping pm_ = (pm == null) ? ((m == null) ? PrefixUtils.commonPrefixes() : m) : pm;
        q.setPrefixMapping( pm_ );

        // default to null base URI and SPARQL 1.1.
        QueryFactory.parse( q, query, null, Syntax.syntaxSPARQL_11 );
        log.debug( "pepared query = " + q.serialize() );

        QueryExecution qe = null;

        if (bindings != null) {
            qe = QueryExecutionFactory.create( q, m, bindings );
        }
        else {
            qe = QueryExecutionFactory.create( q, m );
        }

        return qe;
    }

    /**
     * Return all results from executing the given select query. Uses the default commont prefixes.
     *
     * @param m The model to run the query against
     * @param query The sparql query
     * @return ResultSet of all values
     */
    public static ResultSet selectAll( Model m, String query ) {
        return selectAll( m, query, null, (QuerySolutionMap) null );
    }

    /**
     * Return all results from executing the given select query.
     * @param m The model to run the query against
     * @param query The sparql query
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null. If null is given, the default
     * prefixes from {@link PrefixUtils#commonPrefixes()} will be used
     * @param bindings Optional array of name/value pairs to use as initial variable bindings
     * @return ResultSet of all values
     */
    public static ResultSet selectAll( Model m, String query, PrefixMapping pm, Object... bindings ) {
        return createQueryExecution( m, query, pm, bindings ).execSelect();
    }

    /**
     * Return all results from executing the given select query.
     * @param m The model to run the query against
     * @param query The sparql query
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null. If null is given, the default
     * prefixes from {@link PrefixUtils#commonPrefixes()} will be used
     * @param bindings Optional {@link QuerySolutionMap} containing initial bindings for query variables
     * @return ResultSet of all values
     */
    public static ResultSet selectAll( Model m, String query, PrefixMapping pm, QuerySolutionMap bindings ) {
        return createQueryExecution( m, query, pm, bindings ).execSelect();
    }

    /**
     * Return all results from executing the given select query for a given variable.
     * @param var The variable name to project from the query results
     * @param m The model to run the query against
     * @param query The sparql query
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null. If null is given, the default
     * prefixes from {@link PrefixUtils#commonPrefixes()} will be used
     * @param bindings Optional {@link QuerySolutionMap} containing initial bindings for query variables
     * @return Non-null list of the values for <code>var</code>
     */
    public static List<RDFNode> selectAllVar( String var, Model m, String query, PrefixMapping pm, Object... bindings ) {
        ResultSet rs = createQueryExecution( m, query, pm, bindings ).execSelect();

        return getResultSetAll( var, rs );
    }

    /**
     * Return the first result from executing the given select query, for a given variable.
     * @param var The variable name to project from the query results
     * @param m The model to run the query against
     * @param query The sparql query
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null. If null is given, the default
     * prefixes from {@link PrefixUtils#commonPrefixes()} will be used
     * @param bindings Optional {@link QuerySolutionMap} containing initial bindings for query variables
     * @return The first value for <code>var</code>, or null
     */
    public static RDFNode selectFirstVar( String var, Model m, String query, PrefixMapping pm, Object... bindings ) {
        QueryExecution qe = createQueryExecution( m, query, pm, bindings );
        ResultSet rs = qe.execSelect();
        return getResultSetFirst( var, qe, rs );
    }

    /**
     * Return the model that is the SPARQL description of resource <code>r</code>
     * @param m
     * @param r
     * @return
     */
    public static Model describeResource( Model m, Resource r ) {
        return describeResource( m, r.getURI() );
    }

    /**
     * Return the model that is the SPARQL description of resource with the given URI
     * @param m
     * @param uri
     * @return
     */
    public static Model describeResource( Model m, String uri ) {
        return describe( m, String.format( "describe <%s>", uri ) );
    }

    /**
     * Return the model that results from executing the given describe query. Uses the default
     * common prefixes.
     *
     * @param m The model to run the query against
     * @param query The sparql query
     * @return ResultSet of all values
     */
    public static Model describe( Model m, String query ) {
        return describe( m, query, null, (QuerySolutionMap) null );
    }

    /**
     * Return the model that results from executing the given describe query.
     * @param m The model to run the query against
     * @param query The sparql query
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null. If null is given, the default
     * prefixes from {@link PrefixUtils#commonPrefixes()} will be used
     * @param bindings Optional array of name/value pairs to use as initial variable bindings
     * @return ResultSet of all values
     */
    public static Model describe( Model m, String query, PrefixMapping pm, Object... bindings ) {
        return createQueryExecution( m, query, pm, bindings ).execDescribe();
    }

    /**
     * Return the model that results from executing the given describe query.
     * @param m The model to run the query against
     * @param query The sparql query
     * @param pm Optional {@link PrefixMapping} to use when parsing the query, or null.
     * @param bindings Optional {@link QuerySolutionMap} containing initial bindings for query variables
     * @return ResultSet of all values
     */
    public static Model describe( Model m, String query, PrefixMapping pm, QuerySolutionMap bindings ) {
        return createQueryExecution( m, query, pm, bindings ).execDescribe();
    }

    /**
     * Format a value in a way that we can include it in a SPARQL query string
     * @param val
     * @return The value formatted for SPARQL
     */
    public static String asSPARQLValue( Object val ) {
        String s = null;

        if (val instanceof RDFNode) {
            RDFNode n = (RDFNode) val;
            if (n.isLiteral()) {
                Literal l = n.asLiteral();
                if (l.getLanguage() != null && l.getLanguage() != "") {
                    s = String.format( "'%s'@%s", escape(l.getLexicalForm(), '\''), l.getLanguage() );
                } 
                else if (l.getDatatypeURI() != null && ! l.getDatatypeURI().equals(XSD.xstring.getURI())) {
                    s = String.format( "'%s'^^<%s>", escape(l.getLexicalForm(), '\''), l.getDatatypeURI() );
                }
                else {
                    s = String.format( "'%s'", escape(l.getLexicalForm(), '\'') );
                }
            }
            else {
                Resource r = n.asResource();
                if (r.isAnon()) {
                    s = String.format( "_:%s", r.getId().toString().replaceAll( "[^A-Za-z0-9]", "_" ) );
                }
                else {
                    s = String.format( "<%s>", r.getURI() );
                }
            }
        }
        else if (val instanceof String) {
            String str = (String) val;
            if (str.matches( "^(file:|http:|https:).*" )) {
                s = String.format( "<%s>", str.replace(">", "%3E") );
            }
            else if (str.matches( "^[A-Za-z0-9]*:.*" )) {
                // looks like a qname
                s = str;
            }
            else if (str.matches("^[0-9]+(\\.[0-9]*)?")) {
                // Looks like a number
                s = str;
            }
        }

        if (s == null) {
            s = String.format( "'%s'", escape(val.toString(), '\'') );
        }

        return s;
    }

    /**
     * Substitute the variables in a query string for the values from the given binding. This produces
     * a new query string, suitable, for example, for sending to a remote service endpoint.
     * @param query The query string to act on
     * @param bindings A set of value bindings for variables that may occur in <code>query</code>
     */
    public static String substituteVars( String query, QuerySolutionMap bindings ) {
        String q = query;

        for (Iterator<String> vars = bindings.varNames(); vars.hasNext(); ) {
            String var = vars.next();
            RDFNode n  = bindings.get( var );
            q = q.replaceAll( "\\?" + var + "\\b", asSPARQLValue( n ));
        }

        return q;
    }

    /**
     * Execute a select query against a remote SPARQL endpoint.
     * @param serviceURL The address of the SPARQL endpoint, as a string
     * @param query The query string to send
     * @param pm Optional prefix map. If null, the default common prefixes will be used
     * @param bindings Optional bindings for variables in the query string, in pairs of variable name and value
     * @return The resultset of all results
     */
    public static ResultSet serviceSelectAll( String serviceURL, String query, PrefixMapping pm, Object... bindings ) {
        return serviceSelectAll( serviceURL, query, pm, createBindings( bindings ));
    }

    /**
     * Execute a select query against a remote SPARQL endpoint.
     * @param serviceURL The address of the SPARQL endpoint, as a string
     * @param query The query string to send
     * @param pm Optional prefix map. If null, the default common prefixes will be used
     * @param bindings Optional bindings for variables in the query string, in pairs of variable name and value
     * @return The resultset of all results
     */
    public static ResultSet serviceSelectAll( String serviceURL, String query, PrefixMapping pm, QuerySolutionMap bindings ) {
        String qBody = substituteVars( query, bindings );
        String qHeader = PrefixUtils.asSparqlPrefixes( (pm == null) ? PrefixUtils.commonPrefixes() : pm );
        return QueryExecutionFactory.sparqlService( serviceURL, qHeader + qBody ).execSelect();
    }

    /**
     * Return all values for the given var from executing the given query against the given remote sparql endpoint.
     * @param var
     * @param serviceURL
     * @param query
     * @param pm
     * @param bindings
     * @return
     */
    public static List<RDFNode> serviceSelectAllVar( String var, String serviceURL, String query, PrefixMapping pm, Object... bindings ) {
        return getResultSetAll( var, serviceSelectAll( serviceURL, query, pm, bindings ) );
    }

    /**
     * Return the first value for the given var from executing the given query against the given remote sparql endpoint.
     * @param var
     * @param serviceURL
     * @param query
     * @param pm
     * @param bindings
     * @return
     */
    public static RDFNode serviceSelectFirstVar( String var, String serviceURL, String query, PrefixMapping pm, Object... bindings ) {
        return getResultSetFirst( var, null, serviceSelectAll( serviceURL, query, pm, bindings ) );
    }

    /**
     * Execute a describe query against a remote SPARQL endpoint.
     * @param serviceURL The address of the SPARQL endpoint, as a string
     * @param query The query string to send
     * @param pm Optional prefix map. If null, the default common prefixes will be used
     * @param bindings Optional bindings for variables in the query string, in pairs of variable name and value
     * @return The resultset of all results
     */
    public static Model serviceDescribe( String serviceURL, String query, PrefixMapping pm, Object... bindings ) {
        String qBody = substituteVars( query, createBindings( bindings ) );
        String qHeader = PrefixUtils.asSparqlPrefixes( (pm == null) ? PrefixUtils.commonPrefixes() : pm );
        return QueryExecutionFactory.sparqlService( serviceURL, qHeader + qBody ).execDescribe();
    }

    /**
     * Return all resources connected by a property path to the given resource.
     * Path can use prefixes defined in the resource's Model.
     */
    public static List<Resource> connectedResources(Resource root, String path) {
        Model m = root.getModel();
        String query = String.format("SELECT ?x WHERE { <%s> %s ?x }", root.getURI(), path);
        query = PrefixUtils.expandQuery(query, m);
        return resultsFor(selectAll(m, query), "x");
    }

    /**
     * Return all Literals connected by a property path to the given resource.
     * Path can use prefixes defined in the resource's Model.
     */
    public static List<Literal> connectedLiterals(Resource root, String path) {
        Model m = root.getModel();
        String query = String.format("SELECT ?x WHERE { <%s> %s ?x }", root.getURI(), path);
        query = PrefixUtils.expandQuery(query, m);
        return resultsFor(selectAll(m, query), "x", Literal.class);
    }

    /***************************
     * Internal support methods
     **************************/

    /**
     * @param var
     * @param qe
     * @param rs
     * @return
     */
    protected static RDFNode getResultSetFirst( String var, QueryExecution qe, ResultSet rs ) {
        RDFNode r = null;

        try {
            if (rs.hasNext()) {
                r = rs.next().get( var );
            }
        }
        finally {
            if (qe != null) {qe.close();}
        }

        return r;
    }

    /**
     * @param var
     * @param rs
     * @return
     */
    protected static List<RDFNode> getResultSetAll( String var, ResultSet rs ) {
        List<RDFNode> resultList = new ArrayList<RDFNode>();
        while (rs.hasNext()) {
            resultList.add( rs.next().get( var ) );
        }
        return resultList;
    }


    /**
     * Take a column from result set and extract it as a list of values of the 
     * given type (e.g. Resource, Literal or generic RDFNode.
     * Skips any non-matching results
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> resultsFor(ResultSet results, String varname, Class<T> cls) {
        List<T> resultList = new ArrayList<T>();
        while (results.hasNext()) {
            RDFNode result = results.nextSolution().get(varname);
            if (cls.isInstance(result)) {
                resultList.add( (T) result);
            }
        }
        return resultList;
    }

}
