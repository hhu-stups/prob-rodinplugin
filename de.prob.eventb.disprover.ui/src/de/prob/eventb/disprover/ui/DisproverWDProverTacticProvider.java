package de.prob.eventb.disprover.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.IPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;

import de.prob.eventb.disprover.core.Disprover;
import de.prob.eventb.disprover.core.DisproverReasonerInput;

/**
 * "Use ProB WD Prover": calls the disprover with the options
 * <code>[wd_prover_timeout(T),no_constraint_solving]</code>, where T is taken
 * from the preferences.
 */
public class DisproverWDProverTacticProvider extends DefaultTacticProvider {
	protected static class MyPredicateApplication implements
			IPredicateApplication {
		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			// the time-out is stored in the input, for replaying the proof
			IReasonerInput reasonerInput = DisproverReasonerInput
					.createWdProverInput(DisproverPreferences
							.getWdProverTimeout());
			return BasicTactics.reasonerTac(
					Disprover.createDisproverReasoner(), reasonerInput);
		}

		@Override
		public String getTacticID() {
			return "de.prob.eventb.disprover.ui.disproverWDProverTactic";
		}

		@Override
		public Image getIcon() {
			return null;
		}

		@Override
		public String getTooltip() {
			return null;
		}

	}

	public DisproverWDProverTacticProvider() {
		super();
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {

		if (node != null && node.isOpen()) {
			ITacticApplication application = new MyPredicateApplication();
			return Collections.singletonList(application);
		}
		return Collections.<ITacticApplication> emptyList();
	}
}
