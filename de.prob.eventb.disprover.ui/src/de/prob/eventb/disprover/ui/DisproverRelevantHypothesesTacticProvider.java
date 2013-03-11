package de.prob.eventb.disprover.ui;

import de.prob.eventb.disprover.core.DisproverReasonerInput.HypothesesSource;

/**
 * TacticProvider for the Disprover.
 * 
 * Disprover that uses {@link DisproverMode#ALL_HYPOTHESES}.
 * 
 * @author jastram
 * 
 */
public class DisproverRelevantHypothesesTacticProvider extends
		AbstractDisproverTacticProvider {

	@Override
	protected HypothesesSource getHypotheses() {
		return HypothesesSource.RELEVANT;
	}

	@Override
	protected boolean useContexts() {
		return false;
	}

}
