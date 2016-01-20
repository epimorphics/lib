/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.text;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.text.EntityDefinition;
import org.apache.jena.query.text.TextDatasetFactory;
import org.apache.jena.query.text.TextIndexConfig;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDFS;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class TestTextIndex {

	static final Node G = NodeFactory.createURI("eh:/G");
	
	static final String [] triples = new String[] {
			"A :: leaf branch",
			"B :: leaf paint",
			"C :: branch line"
	};
	
	@Test public void testTextIndex() throws IOException {
		Dataset ds = prepareForTest();		
		testIndexSelectsSubjects(ds, set("eh:/subjectA", "eh:/subjectC"), "branch");
		testIndexSelectsSubjects(ds, set("eh:/subjectA", "eh:/subjectB"), "leaf");
		testIndexSelectsSubjects(ds, set("eh:/subjectB"), "paint");
		testIndexSelectsSubjects(ds, set("eh:/subjectC"), "line");
	}

	private void testIndexSelectsSubjects(Dataset ds, Set<String> expected, String target) throws IOException {
		
		QueryShape qs = new QueryShape();
		qs.getTransforms().add(new TextTransformerByJenaText());		
		qs.setTextQuery(new TextQuery(new Var("S"), new URI(RDFS.label.getURI()), target));
		
		QueryExecution qx = createQuery(ds, qs);

		Set<String> results = new HashSet<String>();
		
		try {
			ds.begin(ReadWrite.READ);
			ResultSet rs = qx.execSelect();
			while (rs.hasNext()) results.add(rs.next().get("S").asNode().getURI());
		} finally {
			ds.commit();
			ds.end();
		}
		
		assertEquals(expected, results);
	}

	private QueryExecution createQuery(Dataset ds, QueryShape qs) {
		String queryString = qs.toSparqlSelect(new Settings());
		Query q = QueryFactory.create(queryString);		
		
		QueryExecution qx = QueryExecutionFactory.create(q, ds);
		return qx;
	}

	private Dataset prepareForTest() throws IOException {
		String root = "./DATASET", tdb_dir = root + "/TDB";
				
		FileUtils.deleteDirectory(new File(root));
				
		new File(tdb_dir).mkdirs();
		
		Directory text_dir = FSDirectory.open(new File(root + "/textIndex"));
		EntityDefinition entDef = new EntityDefinition("uri", "text", RDFS.label);
		
		Dataset base = TDBFactory.createDataset(tdb_dir); 
		TextIndexConfig config = new TextIndexConfig(entDef);
	
		Dataset ds = TextDatasetFactory.createLucene(base, text_dir, config);
		
		loadTestData(ds);
		return ds;
	}
	
	@SafeVarargs public static <T> Set<T> set(T... args) {
		return new HashSet<T>(Arrays.asList(args));
	}

	private void loadTestData(Dataset ds) {
		ds.begin(ReadWrite.WRITE);
		DatasetGraph g = ds.asDatasetGraph();
		for (String t: triples) {
			String [] nameAndText = t.split(" *:: *");
			Node S = NodeFactory.createURI("eh:/subject" + nameAndText[0]);
			Node P = RDFS.label.asNode();
			Node O = NodeFactory.createLiteral(nameAndText[1]);
			g.add(G, S, P, O);
		}
		ds.commit();
		ds.end();
	}

		
}
