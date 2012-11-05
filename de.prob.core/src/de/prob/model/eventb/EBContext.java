package de.prob.model.eventb;

import java.util.Arrays;

import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class EBContext extends Label {

	public Label sets = new Label("Sets");
	public Label constants = new Label("Constants");
	public Label axioms = new Label("Axioms");

	public EBContext(final String name) {
		super(name);

		children.addAll(Arrays
				.asList(new IEntity[] { sets, constants, axioms }));
	}

	public void addSet(final String set) {
		sets.addChild(new EventB(set));
	}

	public void addConstant(final String constant) {
		constants.addChild(new EventB(constant));
	}

	public void addAxiom(final String axiom, String aname) {
		axioms.addChild(new EventB(axiom,aname));
	}

}
