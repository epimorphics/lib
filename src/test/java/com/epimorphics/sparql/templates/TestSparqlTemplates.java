/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.test.utils.MakeCollection;

public class TestSparqlTemplates {

	public static class Template {
	
		final List<Element> elements = new ArrayList<Element>();
		
		public Template(String content) {
			
			while (true) {
				int dollar = content.indexOf('$');
				if (dollar < 0) {
					if (content.length() > 0) elements.add(new Literal(content));
					break;
				} else {
					String lit = content.substring(0, dollar);
					if (lit.length() > 0) elements.add(new Literal(lit));
					
					int scan = dollar + 1;
					while (scan < content.length() && Character.isLetter(content.charAt(scan))) scan += 1;
					
					int colon = scan;
					
					if (scan < content.length() && content.charAt(scan) == ':') {
						scan += 1;
						while (scan < content.length() && Character.isLetter(content.charAt(scan))) scan += 1;
					}
				
					String spelling = content.substring(dollar+1, colon);
					String type = content.substring(colon, scan);
					elements.add(new Param(spelling, type));
					content = content.substring(scan);
				}
					
			}
			
		}
		
		public List<Element> getElements() {
			return elements;
		}
		
		interface Element {
			
		}
		
		static class Literal implements Element {
			
			final String spelling;
			
			Literal(String spelling) {
				this.spelling = spelling;
			}
			
			public boolean equals(Object other) {
				return other instanceof Literal && same((Literal) other);
			}
			
			public String toString() {
				return "lit(" + spelling + ")";
			}

			private boolean same(Literal other) {
				return spelling.equals(other.spelling);
			}
		}
		
		static class Param implements Element {

			public static final String USUAL = null;
			
			final String spelling;
			final String type;
			
			Param(String spelling, String type) {
				this.spelling = spelling;
				this.type = type;
			}
			
			public boolean equals(Object other) {
				return other instanceof Param && same((Param) other);
			}
			
			public String toString() {
				return "par(" + spelling + ")";
			}

			private boolean same(Param other) {
				return spelling.equals(other.spelling);
			}
		}
		
	}
	
	protected Template.Element literal(String spelling) {
		return new Template.Literal(spelling);
	}
	
	protected Template.Element param(String spelling) {
		return param(spelling, Template.Param.USUAL);
	}
	
	protected Template.Element param(String spelling, String type) {
		return new Template.Param(spelling, type);
	}
	
	@Test public void testSingleLiteral() {
		Template t = new Template("SELECT ?x WHERE {}");
		assertEquals(MakeCollection.list(literal("SELECT ?x WHERE {}")), t.getElements());
	}
	
	@Test public void testSingleSimpleInsertion() {
		Template t = new Template("$Alpha");
		assertEquals(MakeCollection.list(param("Alpha")), t.getElements());
	}
	
	@Test public void testSingleTypedInsertion() {
		Template t = new Template("$Alpha:Fragment");
		assertEquals(MakeCollection.list(param("Alpha", "Fragment")), t.getElements());
	}
	
	@Test public void testMultipleParts() {
		Template t = new Template("before $Alpha then $beta end");
		assertEquals(MakeCollection.list(literal("before "), param("Alpha"), literal(" then "), param("beta"), literal(" end")), t.getElements());
	}
}
