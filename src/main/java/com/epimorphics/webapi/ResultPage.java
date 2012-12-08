/******************************************************************
 * File:        ResultPage.java
 * Created by:  Dave Reynolds
 * Created on:  12 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.webapi;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.vocabs.API;
import com.epimorphics.vocabs.OpenSearch;
import com.epimorphics.vocabs.XHV;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Support for returning a paged view of a list of RDF resources.
 * The results resources are expected to all be in the same model.
 * The model will be side-effected to add the paging information
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class ResultPage {

    protected PageInfo pageInfo;
    protected List<Resource> results;
    protected Resource root;
    protected Resource page;

    public ResultPage(PageInfo pageInfo, List<Resource> results) {
        this.pageInfo = pageInfo;
        this.results = results;
        initModel();
    }

    public ResultPage(PageInfo pageInfo, Resource[] resultsArray) {
        this.pageInfo = pageInfo;
        this.results = new ArrayList<Resource>(resultsArray.length);
        for (Resource r : resultsArray) {
            results.add(r);
        }
        initModel();
    }

    private void initModel() {
        Model model;
        if (results.size() > 0) {
            model = results.get(0).getModel();
        } else {
            model = ModelFactory.createDefaultModel();
        }
        root = model.createResource( pageInfo.getRequestRootURI() );
        page = model.createResource( pageInfo.getPageURI() );

        root.addProperty(DCTerms.hasPart, page);

        page
          .addProperty(RDF.type, API.Page)
          .addLiteral(OpenSearch.itemsPerPage, pageInfo.getPageSize())
          .addLiteral(OpenSearch.startIndex, pageInfo.getOffset() + 1);
        int pn = pageInfo.getPageNumber();
        if (pn > 0) {
            page.addProperty(XHV.prev, pageInfo.getPageURI(pn - 1));
            page.addProperty(XHV.first, pageInfo.getPageURI(0));
        }
        if (results.size() >= pageInfo.getPageSize()) {
            page.addProperty(XHV.next, pageInfo.getPageURI(pn + 1));
        }
        page.addProperty( API.items, model.createList( results.iterator() ) );
    }

    /**
     * Return the resource representing the overall list.
     */
    public Resource getRoot() {
        return root;
    }

    /**
     * Return the resource representing this page of the list.
     */
    public Resource getPage() {
        return page;
    }

    /**
     * Return all the content resources in this list
     */
    public Iterable<Resource> list() {
        return results;
    }

    /**
     * Return the model containing all the result data
     */
    public Model getModel() {
        Model rootModel = root.getModel();
        for (Resource r : results) {
            if (r.getModel() != rootModel) {
                rootModel.add( r.getModel() );
            }
        }
        return rootModel;
    }
}
