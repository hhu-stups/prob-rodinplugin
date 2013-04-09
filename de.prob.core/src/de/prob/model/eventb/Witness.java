package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.IEval;

public class Witness extends AbstractElement implements IEval {

	private final String name;
	private final EventB predicate;

	public Witness(final String name, final String code) {
		this.name = name;
		predicate = new EventB(code);
	}

	public String getName() {
		return name;
	}

	public EventB getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getEvaluate() {
		return predicate;
	}

}
