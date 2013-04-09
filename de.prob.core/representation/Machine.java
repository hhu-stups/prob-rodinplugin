package de.prob.model.representation;

public abstract class Machine extends AbstractElement {

	private final String name;

	public Machine(final String name) {
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
