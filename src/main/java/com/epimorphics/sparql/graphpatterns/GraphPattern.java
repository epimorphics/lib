/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsSparqler;

public abstract class GraphPattern implements IsSparqler {

	public enum Rank
		{ NoBraces
		, Optional
		, Values
		, Select
		, Bind
		, Text
		, Named
		, Zero
		, Empty
		, Basic
		, Minus
		, Union
		, And
		, Exists
		, Max
		}
	
	public final void toSparql(Settings s, StringBuilder sb) {
		toPatternString(Rank.Zero, s, sb);
	}

	protected void toPatternString(Rank r, Settings s, StringBuilder sb) {
		if (needsBraces(r)) sb.append("{");
		toPatternString(s, sb);
		if (needsBraces(r)) sb.append("}");
	}

	protected boolean needsBraces(Rank context) {
		return context.ordinal() > ordinal();
	}
	
	protected abstract int ordinal();

	public abstract void toPatternString(Settings s, StringBuilder sb);

	
}
