/******************************************************************
 * File:        MockUriInfo.java
 * Created by:  Dave Reynolds
 * Created on:  15 Aug 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.webapi.marshalling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.header.InBoundHeaders;

/**
 * Partial mock up of UriInfo, just sufficient to create PageInfo instances
 * for testing ResultPage processing.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class MockUriInfo implements UriInfo {

    protected String path;
    protected String absolutePath;
    protected MultivaluedMap<String, String> queryParameters = new InBoundHeaders();

    public MockUriInfo(String path) {
        this.path = path;
        if (path.contains("?")) {
            String[] parts = path.split("\\?");
            absolutePath = parts[0];
            String[] queries = parts[1].split("&");
            for (String query : queries) {
                String[] comps = query.split("=");
                queryParameters.add(comps[0], comps[1]);
            }
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getPath(boolean decode) {
        return path;
    }

    @Override
    public List<PathSegment> getPathSegments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PathSegment> getPathSegments(boolean decode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URI getRequestUri() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        return UriBuilder.fromUri(path);
    }

    @Override
    public URI getAbsolutePath() {
        try {
            return new URI(absolutePath);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return UriBuilder.fromUri(absolutePath);
    }

    @Override
    public URI getBaseUri() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean decode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        return queryParameters;
    }

    @Override
    public List<String> getMatchedURIs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMatchedURIs(boolean decode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMatchedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
