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

}