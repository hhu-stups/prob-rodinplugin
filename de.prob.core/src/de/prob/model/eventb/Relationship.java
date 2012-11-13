package de.prob.model.eventb;

import de.prob.model.representation.Label;
import de.prob.model.representation.RefType.ERefType;

public class Relationship {

	public final Label from;
	public final Label to;
	public final ERefType type;

	public Relationship(Label from, Label to, ERefType type) {
		this.from = from;
		this.to = to;
		this.type = type;
	}
}
