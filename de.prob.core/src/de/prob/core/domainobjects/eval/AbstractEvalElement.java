/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.domainobjects.eval;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.core.Animator;
import de.prob.core.command.EvaluateRawExpressionsCommand;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public abstract class AbstractEvalElement {

	private String value = null;
	private Animator animator;

	public abstract Start getPrologAst();

	public abstract boolean hasChildren();

	public boolean isAtomic() {
		return !hasChildren();
	};

	public abstract String getLabel();

	protected Start parse(final String prefix, final String code)
			throws BCompoundException {
		final BParser parser = new BParser();
		final Start modelAst = parser.parse(prefix + code, false);
		return modelAst;
	}

	public synchronized String getValue(final Animator animator,
			final String stateId) {
		this.animator = animator;
		if (value == null) {
			value = evaluate(stateId);
		}
		return value;
	}

	private synchronized String evaluate(final String stateId) {
		try {
			return EvaluateRawExpressionsCommand.evaluate(animator, this,
					stateId);
		} catch (ProBException e) {
			Logger.notifyUser(
					"Something went wrong while evaluating: " + getLabel(), e);
		}
		return "unknown value";
	}
}
