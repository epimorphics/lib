/******************************************************************
 * File:        FileModelUtil.java
 * Created by:  Dave Reynolds
 * Created on:  10 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.io.File;

import com.epimorphics.util.FileUtil;
import com.hp.hpl.jena.graph.impl.FileGraph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Utilities for handling file-backed persistent models.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class FileModelUtil {

	/**
	 * Open a file-backed model. If the file already exists then 
	 * the data is read in from it. If it does not exist it will be
	 * created when data is added. This constructor ensures the directory exists.
	 */
	public static Model openModel(String dir, String fname) {
		File file = new File( FileUtil.ensureDir(dir), fname);
		return ModelFactory.createModelForGraph( new FileGraph(file, false, false) );
	}

}
