package de.prob.model.representation;

public class BSet extends AbstractElement {

	private final String name;

	public BSet(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
