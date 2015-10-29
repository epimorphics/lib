/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import java.util.ArrayList;
import java.util.List;

public class Template {

	final List<Element> elements = new ArrayList<Element>();
	
	public Template(String content) {
		
		while (true) {
			int dollar = content.indexOf('$');
			if (dollar < 0) break;
			content = parseParameter(content, dollar);
		}
		if (content.length() > 0) elements.add(new PlainText(content));		
	}
	
	protected String parseParameter(String content, int dollar) {
		
		String lit = content.substring(0, dollar);
		if (lit.length() > 0) elements.add(new PlainText(lit));
		
		int scan = dollar + 1;
		while (scan < content.length() && isLettery(content.charAt(scan))) scan += 1;
		
		int colon = scan;
		
		if (scan < content.length() && content.charAt(scan) == ':') {
			scan += 1;
			while (scan < content.length() && isLettery(content.charAt(scan))) scan += 1;
		}
		
		String spelling = content.substring(dollar+1, colon);
		String type = content.substring(colon, scan);
		elements.add(new Parameter(spelling, type));
		return content.substring(scan);
	}
	
	private boolean isLettery(char ch) {
		return ch == '_' || Character.isLetter(ch);
	}

	public List<Element> getElements() {
		return elements;
	}

	public String substWith(Settings s) {
		StringBuilder sb = new StringBuilder();
		for (Element e: elements) e.subst(sb, s);
		return sb.toString();
	}

	public boolean startsWith(String queryType) {
		return elements.size() > 0 && startsWith(elements.get(0), queryType);
	}

	private boolean startsWith(Element element, String queryType) {
		return element instanceof PlainText && ((PlainText) element).text.startsWith(queryType);
	}
	
}