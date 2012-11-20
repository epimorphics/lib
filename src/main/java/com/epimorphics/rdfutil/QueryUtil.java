/******************************************************************
 * File:        QueryUtil.java
 * Created by:  Dave Reynolds
 * Created on:  6 Aug 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.util.EpiException;
import com.epimorphics.util.PrefixUtils;
import com.epimorphics.webapi.PageInfo;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Random small utilities to help with SPARQl queries.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class QueryUtil {


    /**
     * Inject strings into a SPARQL query replacing each ${i} with the corresponding element from the arg list.
     * Purely syntactic.
     */
    public static String substituteInQuery(String query, Object... strings) {
        String result = query;
        for (int i = 0; i < strings.length; i++) {
            Object subs = strings[i];
            result = result.replaceAll("\\$\\{" + i + "\\}", subs == null ? "null" : subs.toString());
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
        return createQueryExecution( m, query, pm, (bindings == null) ? null : createBindings( bindings ) );
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

        List<RDFNode> resultList = new ArrayList<RDFNode>();
        while (rs.hasNext()) {
            resultList.add( rs.next().get( var ) );
        }
        return resultList;
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
        RDFNode r = null;

        try {
            if (rs.hasNext()) {
                r = rs.next().get( var );
            }
        }
        finally {
            qe.close();
        }

        return r;
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
}
