/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.disprover.core.internal;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;

import de.prob.eventb.disprover.core.DisproverReasoner;
import de.prob.eventb.disprover.core.DisproverReasonerInput;

/**
 * Auto tactic "ProB WD Prover" (e.g. for Rodin's tactic profiles): ProB's
 * well-definedness prover without constraint solving, like "Use ProB WD
 * Prover" in the Proof Control view. The time-out is read from the disprover
 * preferences each time, and stored in the proof for replaying it.
 */
public class DisproverWDProverAutoTactic implements ITactic {

	@Override
	public Object apply(final IProofTreeNode ptNode, final IProofMonitor pm) {
		return BasicTactics.reasonerTac(
				new DisproverReasoner(1),
				DisproverReasonerInput
						.createWdProverInput(DisproverReasonerInput
								.getWdProverTimeoutPreference())).apply(
				ptNode, pm);
	}
}
