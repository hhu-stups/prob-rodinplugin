package de.prob.model.representation;

public abstract class Action extends AbstractElement {

	private final String code;

	public Action(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code;
	}
}
