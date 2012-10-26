package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.List;

import de.prob.model.representation.Label;
import de.prob.model.representation.RefType.ERefType;

public class Model {

	public final String serialization_version = "1";
	public final String name;
	public final List<Relationship> relationships = new ArrayList<Relationship>();

	public Model(String name) {
		this.name = name;
	}

	public void addRelationship(final Label from, final Label to, ERefType type) {
		relationships.add(new Relationship(from, to,type));
	}
}
