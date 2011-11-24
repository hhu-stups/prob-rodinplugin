/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.sap.commands;

/**
 * Instances of this class summarizes the generation of local test cases. (test
 * cases in the partner model)
 * 
 * @author plagge
 */
public class LocalTestcasesResult {
	private final int sat;
	private final int unsat;

	public LocalTestcasesResult(final int sat, final int unsat) {
		this.sat = sat;
		this.unsat = unsat;
	}

	/**
	 * @return the number of global test cases for which local test cases have
	 *         been successfully generated.
	 */
	public int getSat() {
		return sat;
	}

	/**
	 * @return the number of global test cases for which no local test case have
	 *         been found.
	 */
	public int getUnsat() {
		return unsat;
	}

}