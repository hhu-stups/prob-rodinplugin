/**
 * 
 */
package de.prob.sap.exceptions;

import java.util.Collection;
import java.util.Collections;

import org.eventb.core.ast.ASTProblem;

/**
 * This exception is thrown if an expression or predicate had parser problems
 * (a.k.a. errors).
 * 
 * @author plagge
 */
public class ParseProblemException extends ProbSapException {
	private static final long serialVersionUID = 1315549744488826055L;

	private final Collection<ASTProblem> problems;

	public ParseProblemException(Collection<ASTProblem> problems) {
		super("Parse problems");
		this.problems = Collections.unmodifiableCollection(problems);
	}

	public Collection<ASTProblem> getProblems() {
		return problems;
	}
}
