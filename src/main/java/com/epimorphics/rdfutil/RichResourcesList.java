/*****************************************************************************
 * File:    RichResource.java
 * Project: epimorphics-lib
 * Created: 20 Nov 2012
 * By:      ian
 *
 * Copyright (c) 2012 Epimorphics Ltd. All rights reserved.
 *****************************************************************************/

// Package
///////////////

package com.epimorphics.rdfutil;


// Imports
///////////////

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.rdf.model.*;

/**
 * <p>A rich resource is an RDF resource with an attached model. A {@link RichResourcesList}
 * contains an ordered list of resources, with a shared model containing their combined
 * description.</p>
 *
 * @author Ian Dickinson, Epimorphics (mailto:ian@epimorphics.com)
 */
@SuppressWarnings( "serial" )
public class RichResourcesList
extends ArrayList<Resource>
{
    /***********************************/
    /* Constants                       */
    /***********************************/

    /***********************************/
    /* Static variables                */
    /***********************************/

    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( RichResourcesList.class );

    /***********************************/
    /* Instance variables              */
    /***********************************/

    private Model description;

    /***********************************/
    /* Constructors                    */
    /***********************************/

    public RichResourcesList() {
        this( null );
    }

    public RichResourcesList( Model m ) {
        this.description = m;
    }

    /***********************************/
    /* External signature methods      */
    /***********************************/

    /**
     * Add a resource to the list. Add the model that r is attached to to this
     * list's description model, and ensure that the resource that is added to the
     * list is attached to the combined model.
     * @param r The resource to add to the model
     */
    @Override
    public boolean add( Resource r ) {
        return add( r, r.getModel() );
    }

    /**
     * Add a resource to the list, and add the given description model to the combined
     * description.
     *
     * @param r A resource to add
     * @param rDescription A description model to add
     * @return
     */
    public boolean add( Resource r, Model rDescription ) {
        addDescription( rDescription );
        return super.add( r.inModel( description() ) );
    }

    /**
     * Add a description model to the combined model for this list.
     * @param m
     */
    public void addDescription( Model m ) {
        if (m != null) {
            description().add( m );
        }
    }

    /**
     * Return the combined description model for this list.
     * @return
     */
    public Model description() {
        if (description == null) {
            description = ModelFactory.createDefaultModel();
        }
        return description;
    }

    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

}

