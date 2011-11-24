/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.core.types;

/**
 * The ProB data type seq(...)
 * 
 * @author plagge
 */
public class SequenceDataType extends SetLikeProbType {
	private static final String SEQ = "seq";

	public SequenceDataType(ProbDataType innerType) {
		super(SEQ, innerType);
	}
}
