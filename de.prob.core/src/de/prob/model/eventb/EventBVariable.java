package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Variable;

public class EventBVariable extends Variable {

	private final String name;

	public EventBVariable(final String name) {
		super(new EventB(name));
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
