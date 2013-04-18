package de.prob.eventb.disprover.core;

public interface ICounterExample {

	public boolean counterExampleFound();

	public boolean timeoutOccured();

	public boolean isProof();

}