package de.prob.eventb.disprover.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

import de.prob.eventb.disprover.core.Disprover;
import de.prob.eventb.disprover.core.DisproverReasonerInput;
import de.prob.eventb.disprover.core.DisproverReasonerInput.HypothesesSource;

/**
 * TacticProvider for the Disprover.
 * 
 * Disprover that uses {@link DisproverMode#ALL_HYPOTHESES}.
 * 
 * @author jastram
 * 
 */
public class DisproverConfigTacticProvider implements ITacticProvider {

	public class MyPredicateApplication implements ITacticApplication {

		private IProofTreeNode node;

		public MyPredicateApplication(IProofTreeNode node) {
			this.node = node;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return getTactic(node, inputs, globalInput);
		}

		@Override
		public String getTacticID() {
			return "19";
		}

		public ITactic getTactic(IProofTreeNode node, String[] inputs,
				String globalInput) {

			IReasonerInput reasonerInput = configureDisproverInteractively(node);
			if (reasonerInput == null) {
				// Returning null would be nicer, but doesn't do any good.
				throw new OperationCanceledException("Disproving Cancelled");
			}
			return BasicTactics.reasonerTac(
					Disprover.createDisproverReasoner(), reasonerInput);
		}

		/**
		 * Pops up a dialog to interactively configure the Disprover.
		 * 
		 * @return the properly configured {@link DisproverReasonerInput} or
		 *         null upon cancellation.
		 */
		private DisproverReasonerInput configureDisproverInteractively(
				IProofTreeNode proofTreeNode) {
			// Show the Disprover Preference Page...
			IPreferencePage page = new DisproverPreferencePage();
			PreferenceManager mgr = new PreferenceManager();
			IPreferenceNode node = new PreferenceNode("1", page);
			mgr.addToRoot(node);
			PreferenceDialog dialog = new PreferenceDialog(null, mgr);
			dialog.create();
			dialog.setMessage(page.getTitle());
			if (dialog.open() == Window.CANCEL)
				return null;

			// ...and build a DisproverReasonerInput with the Preference
			// Information.

			IPreferenceStore store = DisproverActivator.getDefault()
					.getPreferenceStore();
			boolean useContext = store
					.getBoolean(PreferenceConstants.P_INCLUDE_CONTEXT);
			HypothesesSource hypothesesSource = HypothesesSource.valueOf(store
					.getString(PreferenceConstants.P_HYPOTHESES));
			int minInt = store.getInt(PreferenceConstants.P_MIN_INT);
			int maxInt = store.getInt(PreferenceConstants.P_MAX_INT);
			int setSize = store.getInt(PreferenceConstants.P_SET_SIZE);
			int timeout = store.getInt(PreferenceConstants.P_TIMEOUT);

			return new DisproverReasonerInput(proofTreeNode, useContext,
					hypothesesSource, maxInt, minInt, setSize, timeout);
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {

		if (node != null && node.isOpen()) {
			ITacticApplication application = new MyPredicateApplication(node);
			return Collections.singletonList(application);
		}
		return null;
	}

}
