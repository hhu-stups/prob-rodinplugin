package de.prob.core.domainobjects;

import java.util.Collection;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.command.EvaluationExpandCommand;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.exceptions.ProBException;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

public class EvaluationElement {
	private final PrologTerm id;
	private final Animator animator;
	private final EvaluationElement parent;

	private EvLazyInformation lazy;

	public EvaluationElement(final Animator animator, final PrologTerm id,
			final EvaluationElement parent) {
		this.id = id;
		this.animator = animator;
		this.parent = parent;
	}

	public EvaluationElement(final Animator animator, final PrologTerm id,
			final String label, final List<PrologTerm> childrenIds) {
		this(animator, id, null);
		EvaluationElement[] children = new EvaluationElement[childrenIds.size()];
		int i = 0;
		for (final PrologTerm childId : childrenIds) {
			children[i] = new EvaluationElement(animator, childId, this);
			i++;
		}
		this.lazy = new EvLazyInformation(label, children);
	}

	public PrologTerm getId() {
		return id;
	}

	public EvaluationElement getParent() {
		return parent;
	}

	public EvaluationElement[] getChildren() throws ProBException {
		checkForLazyInformation();
		return lazy.children;
	}

	public String getLabel() throws ProBException {
		checkForLazyInformation();
		return lazy.label;
	}

	public EvaluationStateElement evaluateForState(final State state)
			throws ProBException {
		return EvaluationGetValuesCommand.getSingleValueCached(state, this);
	}

	private void checkForLazyInformation() throws ProBException {
		if (lazy == null) {
			final EvaluationExpandCommand cmd = new EvaluationExpandCommand(id);
			animator.execute(cmd);
			final Collection<PrologTerm> childIds = cmd.getChildrenIds();
			final EvaluationElement[] children = new EvaluationElement[childIds
					.size()];
			int i = 0;
			for (final PrologTerm childId : childIds) {
				children[i] = new EvaluationElement(animator, childId, this);
				i++;
			}
			final String label = cmd.getLabel();
			lazy = new EvLazyInformation(label, children);
		}
	}

	private static final class EvLazyInformation {
		private final String label;
		private final EvaluationElement[] children;

		public EvLazyInformation(final String label,
				final EvaluationElement[] children) {
			this.label = UnicodeTranslator.toUnicode(label);
			this.children = children;
		}
	}

	@Override
	public int hashCode() {
		return 31 + id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		return id.equals(((EvaluationElement) obj).id);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EvaluationElement[id=").append(id);
		if (lazy == null) {
			sb.append(",label not yet loaded]");
		} else {
			sb.append(",label='").append(lazy.label).append("']");
		}
		return sb.toString();
	}

}
