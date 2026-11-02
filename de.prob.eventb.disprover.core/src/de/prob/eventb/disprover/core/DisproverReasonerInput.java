package de.prob.eventb.disprover.core;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Input for the Disprover. It records the factor by which the timeout from
 * the preferences is multiplied and whether ProB's WD prover was used. It is
 * stored with the proof, so that a proof is replayed with the same timeout and
 * options.
 * 
 * @author jastram
 */
public class DisproverReasonerInput implements IReasonerInput {

	/** value of {@link #getWdProverTimeout()} for the normal disprover */
	public static final int NO_WD_PROVER = -1;

	private final int timeoutFactor;
	private final int wdProverTimeout;

	public DisproverReasonerInput() {
		this(1);
	}

	public DisproverReasonerInput(final int timeoutFactor) {
		this(timeoutFactor, NO_WD_PROVER);
	}

	/**
	 * @param wdProverTimeout
	 *            the timeout passed to ProB's WD prover (option
	 *            <code>wd_prover_timeout</code>), or {@link #NO_WD_PROVER}
	 */
	public DisproverReasonerInput(final int timeoutFactor,
			final int wdProverTimeout) {
		this.timeoutFactor = timeoutFactor;
		this.wdProverTimeout = wdProverTimeout;
	}

	/**
	 * Creates the input for the WD prover, which does not use constraint
	 * solving
	 */
	public static DisproverReasonerInput createWdProverInput(
			final int wdProverTimeout) {
		return new DisproverReasonerInput(1, wdProverTimeout);
	}

	public int getTimeoutFactor() {
		return timeoutFactor;
	}

	public boolean isWdProver() {
		return wdProverTimeout != NO_WD_PROVER;
	}

	public int getWdProverTimeout() {
		return wdProverTimeout;
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
