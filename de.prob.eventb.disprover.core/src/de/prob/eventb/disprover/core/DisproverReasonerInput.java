package de.prob.eventb.disprover.core;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
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

	private final IProofTreeNode node;
	private final boolean useDisproverPrefs;

	private int maxInt;
	private int minInt;
	private int setSize;
	private int timeout;

	public DisproverReasonerInput(IProofTreeNode node) {
		this.node = node;
		this.useDisproverPrefs = false;
	}

	public DisproverReasonerInput(IProofTreeNode node, int maxInt, int minInt,
			int setSize, int timeout) {
		this.node = node;
		this.useDisproverPrefs = true;
		this.maxInt = maxInt;
		this.minInt = minInt;
		this.setSize = setSize;
		this.timeout = timeout;
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

	public IProofTreeNode getProofTreeNode() {
		return node;
	}

	/**
	 * @param sequent
	 *            , the sequent to prove
	 * @return the required Hypothesis, depending on {@link #mode}.
	 * @throws DisproverException
	 */
	public Iterable<Predicate> getHypotheses(IProverSequent sequent)
			throws DisproverException {
		return sequent.visibleHypIterable();
	}

	public int getMaxInt() {
		return maxInt;
	}

	public int getMinInt() {
		return minInt;
	}

	public int getSetSize() {
		return setSize;
	}

	public int getTimeout() {
		return timeout;
	}

	public boolean useDisproverPrefs() {
		return useDisproverPrefs;
	}

}
