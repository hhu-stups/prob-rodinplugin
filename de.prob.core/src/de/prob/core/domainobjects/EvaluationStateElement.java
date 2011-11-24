/**
 * 
 */
package de.prob.core.domainobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.prob.core.Animator;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationGetValuesCommand.EvaluationResult;
import de.prob.exceptions.ProBException;

/**
 * @author plagge
 * 
 */
public class EvaluationStateElement {
	public final static EvaluationStateElement[] EMPTY_EVALUATION_STATE_ELEMENT_ARRAY = new EvaluationStateElement[0];

	private final EvaluationElement element;
	private final State state;
	private final EvaluationResult result;

	private List<EvaluationStateElement> children;

	public EvaluationStateElement(final EvaluationElement element,
			final State state, final EvaluationResult result) {
		this.element = element;
		this.state = state;
		this.result = result;
	}

	public EvaluationElement getElement() {
		return element;
	}

	public State getState() {
		return state;
	}

	public List<EvaluationStateElement> getChildren() throws ProBException {
		checkChildren();
		return children;
	}

	public EvaluationResult getResult() {
		return result;
	}

	private void checkChildren() throws ProBException {
		if (children == null) {
			EvaluationElement[] staticChildren = element.getChildren();
			EvaluationGetValuesCommand cmd = new EvaluationGetValuesCommand(
					state.getId(), Arrays.asList(staticChildren));
			Animator.getAnimator().execute(cmd);
			Map<EvaluationElement, EvaluationResult> values = cmd.getResult();
			children = new ArrayList<EvaluationStateElement>(
					staticChildren.length);
			for (final EvaluationElement staticChild : staticChildren) {
				final EvaluationResult value = values.get(staticChild);
				children.add(new EvaluationStateElement(staticChild, state,
						value));
			}
			children = Collections.unmodifiableList(children);
		}

	}

	@Override
	public int hashCode() {
		return 19 * (13 + element.hashCode()) + state.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		EvaluationStateElement other = (EvaluationStateElement) obj;
		return element.equals(other.element) && state.equals(other.state);
	}

	public String getText() {
		return result == null ? null : result.getText();
	}

}
