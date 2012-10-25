package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.List;

import de.prob.model.representation.Label;

public class Model {

	public List<Relationship> relationships = new ArrayList<Relationship>();

	public void addRelationship(final Label from, final Label to) {
		relationships.add(new Relationship(from, to));
	}
}
