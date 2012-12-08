/******************************************************************
 * File:        PageInfo.java
 * Created by:  Dave Reynolds
 * Created on:  12 Dec 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.webapi;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Support for interpreting paging requests on paged results lists.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class PageInfo {

    public static int DEFAULT_PAGESIZE = 100;
    
    public static final String PAGESIZE_PARAM = "_pageSize";
    public static final String PAGE_PARAM     = "_page";
    
    protected UriInfo requestURI;
    protected int pageSize;
    protected int pageNumber;
    MultivaluedMap<String, String> params;
    
    public PageInfo(UriInfo requestURI) {
        this.requestURI = requestURI;
        params = requestURI.getQueryParameters();
        
        pageSize = intParam(params, PAGESIZE_PARAM, DEFAULT_PAGESIZE);
        pageNumber = intParam(params, PAGE_PARAM, 0);
        params.remove(PAGE_PARAM);
        params.remove(PAGESIZE_PARAM);
    }
    
    /**
     * Return the index of the first result that is requested.
     */
    public int getOffset() {
        return pageNumber * pageSize;
    }

    /**
     * Return the page number requested, default is 0.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Return the page size for this request.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Return the request URI with query parameters stripped off.
     */
    public String getAbsoluteRequestURI() {
        return requestURI.getAbsolutePath().toString();
    }

    /**
     * Return the request URI incluiding query parameters but not page control parameters
     */
    public String getRequestRootURI() {
        UriBuilder builder = requestURI.getAbsolutePathBuilder();
        for (String param : params.keySet()) {
            for (String value : params.get(param)) {
                builder = builder.queryParam(param, value);
            }
        }
        return builder.build().toString();
    }

    /**
     * Return the page URI, this is the request URI with appropriate page and pagesize query
     * parameters added. 
     */
    public String getPageURI() {
        return getPageURI(pageNumber);
    }

    /**
     * Return the page URI for a specific numbered page.
     */
    public String getPageURI(int pn) {
        UriBuilder builder = requestURI.getAbsolutePathBuilder();
        for (String param : params.keySet()) {
            for (String value : params.get(param)) {
                builder = builder.queryParam(param, value);
            }
        }
        builder = builder.queryParam(PAGE_PARAM, pn);
        if (pageSize != DEFAULT_PAGESIZE) {
            builder = builder.queryParam(PAGESIZE_PARAM, pageSize);
        }
        return builder.build().toString();
    }
    
    
    private int intParam(MultivaluedMap<String, String> params, String param, int def) {
        String val = params.getFirst(param);
        if (val == null) return def;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
