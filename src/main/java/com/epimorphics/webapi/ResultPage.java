/******************************************************************
 * File:        ResultPage.java
 * Created by:  Dave Reynolds
 * Created on:  12 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
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

package com.epimorphics.webapi;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.vocabs.API;
import com.epimorphics.vocabs.OpenSearch;
import com.epimorphics.vocabs.XHV;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

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
