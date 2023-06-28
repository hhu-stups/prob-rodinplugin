package de.prob.ui.ltl;

import de.prob.ui.services.AbstractBoolProvider;

public class CounterExampleLoadedProvider extends AbstractBoolProvider {
	public static final String SERVICE = "de.prob.ui.ltl.counterexample_loaded";

	public CounterExampleLoadedProvider() {
		super(SERVICE);
	}
}
