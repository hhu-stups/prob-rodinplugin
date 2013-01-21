package de.prob.units.problems;

import org.eclipse.core.resources.IMarker;
import org.rodinp.core.IRodinProblem;

import de.prob.units.Activator;

public class IncorrectUnitDefinitionMarker implements IRodinProblem {

	private final String message;
	private final int severity = IMarker.SEVERITY_ERROR;
	public static final String ERROR_CODE = Activator.PLUGIN_ID + "."
			+ "incorrectUnitDefinition";

	public IncorrectUnitDefinitionMarker(String cstOrVar) {
		this.message = "Incorrect Unit Definition on Constant/Variable "
				+ cstOrVar;
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
