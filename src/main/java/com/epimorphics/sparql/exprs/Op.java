/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.exprs;

public class Op {

	public static enum OpType {FUNC, INFIX, PREFIX}

	public static final Op Tuple = new Op(OpType.FUNC, "");
	
	public static final Op fnIRI = new Op(OpType.FUNC, "IRI");
	public static final Op fnURI = new Op(OpType.FUNC, "URI");
	public static final Op fnBNODE = new Op(OpType.FUNC, "BNODE");
	public static final Op fnRAND = new Op(OpType.FUNC, "RAND");
	public static final Op fnABS = new Op(OpType.FUNC, "ABS");
	public static final Op fnCEIL = new Op(OpType.FUNC, "CEIL");
	public static final Op fnFLOOR = new Op(OpType.FUNC, "FLOOR");
	public static final Op fnROUND = new Op(OpType.FUNC, "ROUND");
	public static final Op fnCONCAT = new Op(OpType.FUNC, "CONCAT");
	public static final Op fnSTRLEN = new Op(OpType.FUNC, "STRLEN");
	public static final Op fnUCASE = new Op(OpType.FUNC, "UCASE");
	public static final Op fnLCASE = new Op(OpType.FUNC, "LCASE");
	public static final Op fnENCODE_FOR_URI = new Op(OpType.FUNC, "ENCODE_FOR_URI");
	public static final Op fnCONTAINS = new Op(OpType.FUNC, "CONTAINS");
	public static final Op fnSTRSTARTS = new Op(OpType.FUNC, "STRSTARTS");
	public static final Op fnSTRENDS = new Op(OpType.FUNC, "STRENDS");
	public static final Op fnSTRBEFORE = new Op(OpType.FUNC, "STRBEFORE");
	public static final Op fnSTRAFTER = new Op(OpType.FUNC, "STRAFTER");
	public static final Op fnYEAR = new Op(OpType.FUNC, "YEAR");
	public static final Op fnMONTH = new Op(OpType.FUNC, "MONTH");
	public static final Op fnDAY = new Op(OpType.FUNC, "DAY");
	public static final Op fnHOURS = new Op(OpType.FUNC, "HOURS");
	public static final Op fnMINUTES = new Op(OpType.FUNC, "MINUTES");
	public static final Op fnSECONDS = new Op(OpType.FUNC, "SECONDS");
	public static final Op fnTIMEZONE = new Op(OpType.FUNC, "TIMEZONE");
	public static final Op fnTZ = new Op(OpType.FUNC, "TZ");
	public static final Op fnNOW = new Op(OpType.FUNC, "NOW");
	public static final Op fnUUID = new Op(OpType.FUNC, "UUID");
	public static final Op fnSTRUUID = new Op(OpType.FUNC, "STRUUID");
	public static final Op fnMD5 = new Op(OpType.FUNC, "MD5");
	public static final Op fnSHA1 = new Op(OpType.FUNC, "SHA1");
	public static final Op fnSHA256 = new Op(OpType.FUNC, "SHA256");
	public static final Op fnSHA384 = new Op(OpType.FUNC, "SHA384");
	public static final Op fnSHA512 = new Op(OpType.FUNC, "SHA512");
	public static final Op fnCOALESCE = new Op(OpType.FUNC, "COALESCE");
	public static final Op fnIF = new Op(OpType.FUNC, "IF");
	public static final Op fnSTRLANG = new Op(OpType.FUNC, "STRLANG");
	public static final Op fnSTRDT = new Op(OpType.FUNC, "STRDT");
	public static final Op fnIsBLANK = new Op(OpType.FUNC, "isBLANK");
	public static final Op fnIsLITERAL = new Op(OpType.FUNC, "isLITERAL");
	public static final Op fnIsNUMERIC = new Op(OpType.FUNC, "isNUMERIC");

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
	
	public static final Op opIn = new Op(OpType.INFIX, 3, "IN");
	public static final Op opNotIn = new Op(OpType.INFIX, 3, "NOT IN");
	
	public static final Op opEq = new Op(OpType.INFIX, 3, "=");
	public static final Op opNe = new Op(OpType.INFIX, 3, "!=");
	
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
