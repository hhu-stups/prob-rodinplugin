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
				guards }));
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

	public void addGuard(final String guard, String gname) {
		guards.addChild(new EventB(guard,gname));
	}

	public void addWitness(final String witness,String wname) {
		witnesses.addChild(new EventB(witness,wname));
	}

	public void addAction(final String action, String aname) {
		actions.addChild(new Action(action, aname));
	}
}
