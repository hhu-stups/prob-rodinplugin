/**
 * 
 */
package de.prob.sap.exceptions;

import de.prob.exceptions.ProBException;

/**
 * This class represents exceptions that are specific to the ProB-SAP plugin.
 * 
 * @author plagge
 */
public class ProbSapException extends ProBException {
	private static final long serialVersionUID = -3950873334778622083L;

	public ProbSapException(String exception) {
		super(exception);
	}

}
