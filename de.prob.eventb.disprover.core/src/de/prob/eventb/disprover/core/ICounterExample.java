package de.prob.eventb.disprover.core;

public interface ICounterExample {

	public boolean counterExampleFound();

	public String getMessage();

}