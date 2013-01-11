package de.prob.units.problems;

import org.rodinp.core.IRodinProblem;

import de.prob.units.Activator;

public class MultipleUnitsInferredMarker implements IRodinProblem {

	private String message;
	private int severity;
	public static final String ERROR_CODE = Activator.PLUGIN_ID + "."
			+ "multipleUnitsInferred";

	public MultipleUnitsInferredMarker(int severity, String message) {
		this.severity = severity;
		this.message = message;
	}

	@Override
	public String getErrorCode() {
		return ERROR_CODE;
	}

	@Override
	public String getLocalizedMessage(Object[] arg0) {
		return message;
	}

	@Override
	public int getSeverity() {
		return severity;
	}

}
