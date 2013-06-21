package de.prob.eventb.disprover.core;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Input for the Disprover. The Input keeps track of the {@link DisproverMode}
 * that the Disprover runs in. To prevent conditional statements in the
 * Disprover code, this method provides some helper mthod that return the
 * appropriate values according to the mode.
 * 
 * @author jastram
 */
public class DisproverReasonerInput implements IReasonerInput {
	@Override
	public void applyHints(ReplayHints renaming) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

}
