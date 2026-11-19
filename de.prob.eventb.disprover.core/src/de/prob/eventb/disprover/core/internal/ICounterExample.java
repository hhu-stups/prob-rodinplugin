package de.prob.eventb.disprover.core.internal;

import java.util.Map;

public interface ICounterExample {

	public boolean counterExampleFound();

	public boolean timeoutOccured();

	public boolean isProof();

	public boolean onlySelectedHypotheses();

	public String getReason();

	public boolean doubleCheckFailed();

	/** @return the values of the counter-example, by identifier */
	public Map<String, String> getValues();

	/**
	 * @return ProB's bindings of the counter-example, a list of
	 *         binding(Id,Value,PrettyPrintedValue), e.g. to visualise it; an
	 *         empty list if there is none
	 */
	public de.prob.prolog.term.PrologTerm getBindings();

}