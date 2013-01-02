package de.prob.eventb.translator.internal;

public enum EProofStatus {
	UNPROVEN("false"), PROVEN("true"), REVIEWED("reviewed");
	private final String text;

	private EProofStatus(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
