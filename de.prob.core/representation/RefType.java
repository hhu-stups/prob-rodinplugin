package de.prob.model.representation;

public class RefType {
	private final ERefType relationship;

	/*
	 * RefType is used for both ClassicalBModels and EventBModels
	 * 
	 * ClassicalB: SEES, USES, REFINES, INCLUDES, IMPORTS EventB: SEES, REFINES,
	 * EXTENDS
	 */
	public enum ERefType {
		SEES, USES, REFINES, INCLUDES, IMPORTS, EXTENDS
	}

	public RefType(final ERefType relationship) {
		this.relationship = relationship;
	}

	@Override
	public String toString() {
		return relationship.toString();
	}

	public ERefType getRelationship() {
		return relationship;
	}
}
