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

public class DisproverExtendedTimeoutTacticProvider extends
		DefaultTacticProvider {
	protected static class MyPredicateApplication implements
			IPredicateApplication {
		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			IReasonerInput reasonerInput = new DisproverReasonerInput();
			return BasicTactics.reasonerTac(
					Disprover.createExtendedTimeoutDisproverReasoner(10),
					reasonerInput);
		}

		@Override
		public String getTacticID() {
			return "de.prob.eventb.disprover.ui.disproverTactic";
		}

		@Override
		public Image getIcon() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getTooltip() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public DisproverExtendedTimeoutTacticProvider() {
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