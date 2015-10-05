/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

public class Op {

   public static enum OpType {FUNC, INFIX, PREFIX}

	public static final Op fnSTR = new Op(OpType.FUNC, "STR");
	public static final Op fnLANG = new Op(OpType.FUNC, "LANG");
	public static final Op fnLANGMATCHES = new Op(OpType.FUNC, "LANGMATCHES");
	public static final Op fnDATATYPE = new Op(OpType.FUNC, "DATATYPE");
	public static final Op fnBOUND = new Op(OpType.FUNC, "BOUND");
	public static final Op fnSameTerm = new Op(OpType.FUNC, "sameTerm");
	public static final Op fnIsIRI= new Op(OpType.FUNC, "isIRI");
	public static final Op fnIsURI = new Op(OpType.FUNC, "isURI");
	public static final Op fnIsBlank = new Op(OpType.FUNC, "isBlank");
	public static final Op fnIsLiteral = new Op(OpType.FUNC, "isLiteral");
	
	public static final Op opDiv = new Op(OpType.INFIX, 1, "/");
	public static final Op opMul = new Op(OpType.INFIX, 1, "*");
	
	public static final Op opPlus = new Op(OpType.INFIX, 2, "+");
	public static final Op opMinus = new Op(OpType.INFIX, 2, "-");
	
	public static final Op opLessEq = new Op(OpType.INFIX, 3, "<=");
	public static final Op opGreaterEq= new Op(OpType.INFIX, 3, ">=");
	public static final Op opLess = new Op(OpType.INFIX, 3, "<");
	public static final Op opGreater = new Op(OpType.INFIX, 3, ">");
	
	public static final Op opEq = new Op(OpType.INFIX, 4, "=");
	public static final Op opNe = new Op(OpType.INFIX, 4, "!=");
	
	public static final Op opAnd = new Op(OpType.INFIX, 5, "&&");
	public static final Op opOr = new Op(OpType.INFIX, 5, "||");
	
	public static final Op opUnaryPlus = new Op(OpType.PREFIX, "+");
	public static final Op opUnaryMinus = new Op(OpType.PREFIX, "-");
	public static final Op opNot = new Op(OpType.PREFIX, "!");
	
	final String name;
	final OpType type;
	final int precedence;
	
	public Op(String name) {
		this(OpType.FUNC, name);
	}
	
	public Op(OpType type, String name) {
		this(type, 0, name);
	}
	
	public Op(OpType type, int precedence, String name) {
		this.name = name;
		this.type = type;
		this.precedence = precedence;
	}

	public String getName() {
		return name;
	}
}
