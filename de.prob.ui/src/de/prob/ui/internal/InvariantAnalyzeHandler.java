package de.prob.ui.internal;

import de.prob.core.command.AnalyseInvariantCommand;

public class InvariantAnalyzeHandler extends GenericAnalyzeHandler {

	public InvariantAnalyzeHandler() {
		super(new AnalyseInvariantCommand(), "Analyze Invariant");
	}

}
