/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.structure;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.expr.LeafExprs;
import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.patterns.GraphPatternBasic;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermFilter;
import static com.epimorphics.test.utils.MakeCollection.*;

public class TestStructure {

	
	public static class Query {
		
		protected GraphPattern where = new GraphPattern() {
			
			@Override public void toSparql(Settings s, StringBuilder sb) {
				sb.append("{}");
			}
		};
		
		protected int limit = -1;
		protected int offset = -1;

		public String toSparql(Settings s) {
			StringBuilder sb = new StringBuilder();
			toSparql(s, sb);
			return sb.toString();
		}

		private void toSparql(Settings s, StringBuilder sb) {
			sb.append("SELECT * WHERE ");
			where.toSparql(s, sb);
			if (limit > -1) sb.append(" LIMIT ").append(limit);
			if (offset > -1) sb.append(" OFFSET ").append(offset);
			sb.append("");
			
		}

		public void setPattern(GraphPattern where) {
			this.where = where;
		}

		public void setLimit(int limit) {
			this.limit = limit;
		}

		public void setOffset(int offset) {
			this.offset = offset;
			
		}
		
	}
	
	@Test public void testEmptyQuery() {
		Query q = new Query();
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {}", result);
	}
	
	@Test public void testQueryWithTriplePattern() {
		Query q = new Query();
		
		TermFilter filter = new TermFilter(LeafExprs.bool(true));
		GraphPattern where = new GraphPatternBasic(list(filter));
		
		q.setPattern(where);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {FILTER(true)}", result);
	}
	
	@Test public void testQueryRespectsLimit() {
		Query q = new Query();
		q.setLimit(21);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {} LIMIT 21", result);
	}
	
	@Test public void testQueryRespectsOffset() {
		Query q = new Query();
		q.setOffset(1066);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {} OFFSET 1066", result);
	}
	
	@Test public void testQueryRespectsLimitAndOffset() {
		Query q = new Query();
		q.setLimit(21);
		q.setOffset(1829);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {} LIMIT 21 OFFSET 1829", result);
	}
	
}
