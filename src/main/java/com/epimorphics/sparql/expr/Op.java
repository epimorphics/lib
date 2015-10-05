/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

public class Op {

	static final Op fnSTR = new Op("STR");
	static final Op fnLANG = new Op("LANG");
	static final Op fnLANGMATCHES = new Op("LANGMATCHES");
	static final Op fnDATATYPE = new Op("DATATYPE");
	static final Op fnBOUND = new Op("BOUND");
	static final Op fnSameTerm = new Op("sameTerm");
	static final Op fnIsIRI= new Op("isIRI");
	static final Op fnIsURI = new Op("isURI");
	static final Op fnIsBlank = new Op("isBlank");
	static final Op fnIsLiteral = new Op("isLiteral");
	
	static final Op opPlus = new Op("+");
	static final Op opMinus = new Op("-");
	static final Op opDiv = new Op("/");
	static final Op opMul = new Op("*");
	static final Op opLessEq = new Op("<=");
	static final Op opGreaterEq= new Op(">=");
	static final Op opLess = new Op("<");
	static final Op opGreater = new Op(">");
	static final Op opEq = new Op("=");
	static final Op opNe = new Op("!=");
	static final Op opAnd = new Op("&&");
	static final Op opOr = new Op("||");
	
	static final Op opUnaryPlus = new Op("+");
	static final Op opUnaryMinus = new Op("-");
	static final Op opNot = new Op("!");
	
	final String name;
	
	public Op(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}