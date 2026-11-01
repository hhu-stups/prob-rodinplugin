package de.prob.eventb.disprover.core;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Input for the Disprover. It records the factor by which the timeout from
 * the preferences is multiplied. It is stored with the proof, so that a proof
 * found with an extended timeout is replayed with the same timeout.
 * 
 * @author jastram
 */
public class DisproverReasonerInput implements IReasonerInput {

	private final int timeoutFactor;

	public DisproverReasonerInput() {
		this(1);
	}

	public DisproverReasonerInput(final int timeoutFactor) {
		this.timeoutFactor = timeoutFactor;
	}

	public int getTimeoutFactor() {
		return timeoutFactor;
	}

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
