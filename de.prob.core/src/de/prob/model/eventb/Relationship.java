package de.prob.model.eventb;

import de.prob.model.representation.Label;

public class Relationship {
	public final Label from;
	public final Label to;
	
	public Relationship(Label from,Label to) {
		this.from = from;
		this.to = to;
	}
}
