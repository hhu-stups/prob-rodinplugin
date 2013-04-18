package de.prob.eventb.disprover.ui;

import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;

import de.prob.eventb.disprover.core.Disprover;
import de.prob.eventb.disprover.core.DisproverReasonerInput;

public class DisproverTacticProvider extends DefaultTacticProvider {

	protected boolean useContexts() {
		return false;
	}

	protected static class MyPredicateApplication implements ITacticApplication {

		private final IProofTreeNode node;

		public MyPredicateApplication(IProofTreeNode node) {
			this.node = node;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return getTactic(node, globalInput, inputs);
		}

		public ITactic getTactic(IProofTreeNode node, String globalInput,
				String[] inputs) {

			IReasonerInput reasonerInput = new DisproverReasonerInput(node);
			return BasicTactics.reasonerTac(
					Disprover.createDisproverReasoner(), reasonerInput);
		}

		@Override
		public String getTacticID() {
			return "de.prob.eventb.disprover.ui.disproverTactic";
		}

	}

	public DisproverTacticProvider() {
		super();
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {

		if (node != null && node.isOpen()) {
			ITacticApplication application = new MyPredicateApplication(node);
			return Collections.singletonList(application);
		}
		return Collections.<ITacticApplication> emptyList();
	}
}