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
import de.prob.eventb.disprover.core.DisproverReasonerInput.HypothesesSource;

public abstract class AbstractDisproverTacticProvider extends
		DefaultTacticProvider {

	protected abstract HypothesesSource getHypotheses();
	protected abstract boolean useContexts();

	protected static class MyPredicateApplication implements ITacticApplication {

		private IProofTreeNode node;
		private HypothesesSource hyps;

		public MyPredicateApplication(IProofTreeNode node, HypothesesSource hyps) {
			this.node = node;
			this.hyps = hyps;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return getTactic(node, globalInput, inputs);
		}

		public ITactic getTactic(IProofTreeNode node, String globalInput,
				String[] inputs) {

			IReasonerInput reasonerInput = new DisproverReasonerInput(node,
					false, hyps);
			return BasicTactics.reasonerTac(
					Disprover.createDisproverReasoner(), reasonerInput);
		}

		@Override
		public String getTacticID() {
			return "17";
		}

	}

	public AbstractDisproverTacticProvider() {
		super();
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {

		System.out.println(2);
		if (node != null && node.isOpen()) {
			ITacticApplication application = new MyPredicateApplication(node,
					getHypotheses());
			return Collections.singletonList(application);
		}
		return null;
	}

}