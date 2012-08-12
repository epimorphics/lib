/******************************************************************
 * File:        QueryUtil.java
 * Created by:  Dave Reynolds
 * Created on:  6 Aug 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.webapi.PageInfo;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

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
    public static String substituteInQuery(String query, Object...strings) {
        String result = query;
        for (int i = 0; i < strings.length; i++) {
            result = result.replaceAll("\\$\\{" + i + "\\}", strings[i].toString());
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

}
