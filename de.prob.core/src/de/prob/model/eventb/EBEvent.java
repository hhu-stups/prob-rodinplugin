package de.prob.model.eventb;

import java.util.Arrays;

import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class EBEvent extends Label {

	final public Label refines = new Label("REFINES");
	final public Label parameters = new Label("ANY");
	final public Label guards = new Label("WHERE");
	final public Label witnesses = new Label("WITH");
	final public Label actions = new Label("THEN");

	public EBEvent(final String name) {
		super(name);
		children.addAll(Arrays.asList(new IEntity[] { refines, parameters,
				guards, witnesses, actions }));
	}

	@Override
	public String toString() {
		return name;
	}

	public void addRefinement(final String refinementName) {
		refines.addChild(new Label(refinementName));
	}

	public void addParameter(final String parameter) {
		parameters.addChild(new EventB(parameter));
	}

	public void addGuard(final String guard) {
		guards.addChild(new EventB(guard));
	}

	public void addWitness(final String witness) {
		witnesses.addChild(new EventB(witness));
	}

	public void addAction(final String action) {
		actions.addChild(new Label(action));
	}
}
