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
			// quietly: ProB's errors are shown in the notice below, not in
			// a dialog of their own
			return Animator.callQuietly(() -> EvaluateRawExpressionsCommand
					.evaluate(animator, this, stateId));
		} catch (ProBException e) {
			// e.g. while the State view is refreshed, possibly for several
			// entries: a notice that does not block the user
			Logger.notifyUserNonModal("ProB could not evaluate " + getLabel()
					+ "; it is shown as \"unknown value\".", e.getMessage());
		}
		return "unknown value";
	}
}
