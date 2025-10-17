package com.epimorphics.webapi.dispatch;
/*
    See lda-top/LICENCE (or https://raw.github.com/epimorphics/elda/master/LICENCE)
    for the licence for this software.
    
    (c) Copyright 2011 Epimorphics Limited
    $Id$
*/


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.ws.rs.core.MultivaluedMap;

/**
    <p>A CompiledTemplate is a compiled URI template that
    can match against a path and bind variables.</p>
    
    <p>
    	Complications arise because CompiledTemplates are used in two
    	ways. One is to match a URI path template against the path
    	of a request URI, binding variables as necessary; this is
    	part of the endpoint selection login. Then these is the
    	additional possibility of the template also having query
    	parameter which may also bind variables. The matching of
    	the actual query parameters against the template query parameters
    	is done separately, with variable values matching anything
    	including / -- as opposed to anything <i>except</i> /, used
    	to match path elements.
    </p>
    
    <p>
    	The query template matchers are rebuilt on each call rather
    	than being compiled with the main matcher, but only because
    	a first attempt at doing this produced a recursive loop and
    	stack overflow.
    </p>
    
    <p>
    	The uri template cannot (easily) be treated as a single pattern
    	because the order of the query parameters is irrelevant; so
    	if A and B are submatches we'd have something like AB|BA
    	and that's not dealing with any other -- unclaimed -- query
    	parameters.
    </p>
    
    <p>
    	An ill-formed variable binding, ie an { or } not part of {NAME},
    	is reported by prepare as a SyntaxError exception.
    </p>
*/
public class CompiledTemplate<T> {
	
	private final String template;
	private final Pattern compiled;
	private final List<VarGroupIndex> where;
	private final int literals;
	private final int patterns;
	private final T value;
	private final Map<String, String> uriParamBindings;
	
	protected CompiledTemplate
		( int literals
		, int patterns
		, String template
		, Pattern compiled
		, Map<String, String> uriParamBindings
		, List<VarGroupIndex> where
		, T value 
		) {
		this.where = where;
		this.value = value;
		this.patterns = patterns + patternCount(uriParamBindings);
		this.literals = literals;
		this.compiled = compiled;
		this.template = template;
		this.uriParamBindings = uriParamBindings;
	}
	
	public static class SyntaxError extends RuntimeException {
		
		private static final long serialVersionUID = 1L;

		public final String fragment;
		
		public SyntaxError(String fragment) {
			super("Bad template, contains { or } not matching {NAME}: '" + fragment + "'");
			this.fragment = fragment;
		}
	}
	
	@Override public String toString() {
		return "<MatchTemplate for '" + template + "' => " + value + ">";
	}

	/**
	    Compare this MatchTemplate with another. The one with the most
	    literal characters is the lesser; if they have the same number
	    of literals, the one with the fewer patterns is the lesser.
	    (Hence a sort will put "more specific" templates earlier.)
	*/
	public int compareTo( CompiledTemplate<?> other ) {
		int result = other.literals - literals;
		if (result == 0) result = other.patterns - patterns;
		return result;
	}
	
	/**
	    A MatchTemplate comparator for use in sorting.
	*/
	public static Comparator<CompiledTemplate<?>> compare = new Comparator<CompiledTemplate<?>>() {
		@Override public int compare( CompiledTemplate<?> a, CompiledTemplate<?> b ) {
			return a.compareTo( b );
		}
	};
	
	
	/**
	    Answer the URI template string from which this MatchTemplate was
	    constructed.
	*/
	public String template() {
		return template;
	}
	
	/**
	    Answer the associated value for this template.
	*/
	public T value() {
		return value;
	}
	
	/**
	    Answer a MatchTemplate corresponding to the template string.
	 */
	public static <T> CompiledTemplate<T> prepare( String template, T value ) {
		Map<String, String> params = new HashMap<String, String>();
		template = extractParams(template, params);
		return prepareCore(template, "([^/]+)", params, value);
	}

	/**
	    Match the given uri string. If it matches, add entries to the
	    bindings map so that a template variable X maps to the corresponding
	    piece Y of the uri.
	 * @param queryParams 
	*/
	public boolean match( Map<String, String> bindings, String uri, MultivaluedMap<String, String> queryParams ) {		
		Matcher mu = compiled.matcher( uri );
		if (mu.matches()) {
			if (paramsMatch( bindings, queryParams )) {
				for (VarGroupIndex c: where) {
					bindings.put(c.varName, mu.group(c.groupIndex) );
				}
				return true;
			} 
		}
		return false;
	}
	
	private boolean paramsMatch( Map<String, String> bindings, MultivaluedMap<String, String> queryParams ) {				
		Map<String, String> perhaps = new HashMap<String, String>();
		List<String> toRemove = new ArrayList<String>();
		for (String key: uriParamBindings.keySet()) {
			if (queryParams.containsKey( key )) {
				toRemove.add( key );
				String v = queryParams.getFirst( key );				
				String p = uriParamBindings.get( key );
				Map<String, String> MT = new HashMap<String, String>();
				CompiledTemplate<T> prepared = prepareCore(p, "(.+)", MT, value);			
				boolean matched = prepared.match(bindings, v, queryParams);
				if (!matched) return false;
			} else {
				return false;
			}
		}
		
		for (String key: toRemove) queryParams.remove( key );
		bindings.putAll( perhaps );
		return true;
	}

	private static final Pattern varPattern = Pattern.compile( "\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}" );

	private static String extractParams(String template, Map<String, String> uriParamBindings) {
		int q = template.indexOf( '?' );
		if (q > -1) {
			fillParams( uriParamBindings, template.substring(q + 1) );
			return template.substring(0, q);
		}
		return template;
	}

	private static <T> CompiledTemplate<T> prepareCore(String template, String argPattern, Map<String, String> uriParamBindings, T value) {
		Matcher m = varPattern.matcher( template );
		int start = 0;
		int index = 0;
		int literals = 0;
		int patterns = 0;
		List<VarGroupIndex> where = new ArrayList<VarGroupIndex>();
		StringBuilder sb = new StringBuilder();
		while (m.find(start)) {
			index += 1;
			String name = m.group(1);
			where.add( new VarGroupIndex( name, index ) );
			String literal = template.substring( start, m.start() );
			checkLiteralIsBraceless(literal);
			literals += literal.length();
			patterns += 1;
			sb.append( literal );
			sb.append( argPattern );
			start = m.end();
		}
		String literal = template.substring( start );
		checkLiteralIsBraceless(literal);
		sb.append( literal );
		literals += literal.length();
		Pattern compiled = Pattern.compile( sb.toString() );
		return new CompiledTemplate<T>( literals, patterns, template, compiled, uriParamBindings, where, value );
	}

	private static void checkLiteralIsBraceless(String literal) {
		if (literal.contains("{") || literal.contains("}"))
			throw new SyntaxError(literal);
	}

	private static void fillParams( Map<String, String> uriParamBindings, String template ) {
		String [] fields = template.split( "&" );
		for (String f: fields) {
			String [] kv = f.split( "=", 2 );
			uriParamBindings.put( kv[0], kv[1] );
		}
	}

	private int patternCount(Map<String, String> params ) {
		int result = 0;
		for (Map.Entry<String, String> e: params.entrySet())
			if (e.getValue().startsWith("{")) result += 1;
		return result;
	}
	
	/**
		A VarGroupIndex associates a {} variable from a URI template
		with its group index in the regular expression that matches the
		template.
	*/
	protected static class VarGroupIndex {
		final String varName;
		final int groupIndex;
		
		public VarGroupIndex(String varName, int groupIndex) {
			this.varName = varName;
			this.groupIndex = groupIndex;
		}		
	}
}