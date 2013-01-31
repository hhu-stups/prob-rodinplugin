package de.prob.units.problems;

import org.eclipse.core.resources.IMarker;
import org.rodinp.core.IRodinProblem;

import de.prob.units.Activator;

public class NoUnitInferredMarker implements IRodinProblem {

	private final String message;
	private final int severity = IMarker.SEVERITY_WARNING;
	public static final String ERROR_CODE = Activator.PLUGIN_ID + "."
			+ "multipleUnitsInferred";

	public NoUnitInferredMarker(String cstOrVar) {
		this.message = "No Units inferred for Constant/Variable " + cstOrVar;
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
