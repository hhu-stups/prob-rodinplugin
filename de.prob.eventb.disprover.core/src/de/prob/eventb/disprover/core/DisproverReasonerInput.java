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

	/** the disprover's preferences, shared with its preference page */
	public static final String PREFERENCE_NODE = "prob_disprover_preferences";
	/** time-out for the WD prover, option wd_prover_timeout(T) */
	public static final String WD_PROVER_TIMEOUT_KEY = "wdtimeout";
	public static final int DEFAULT_WD_PROVER_TIMEOUT = 50;

	/**
	 * @return the time-out for ProB's WD prover from the disprover's
	 *         preferences
	 */
	public static int getWdProverTimeoutPreference() {
		final String value = org.eclipse.core.runtime.Platform
				.getPreferencesService()
				.getRootNode()
				.node(org.eclipse.core.runtime.preferences.InstanceScope.SCOPE)
				.node(PREFERENCE_NODE).get(WD_PROVER_TIMEOUT_KEY, null);
		if (value != null) {
			try {
				final int timeout = Integer.parseInt(value.trim());
				if (timeout >= 0)
					return timeout;
			} catch (NumberFormatException e) {
				// use the default
			}
		}
		return DEFAULT_WD_PROVER_TIMEOUT;
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
