/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.disprover.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.ui.prover.IProofCommand;

import de.prob.ui.ProbUiPlugin;

/**
 * "Show Disprover Statistics" in the ProB menu of the Proof Control view: not
 * a tactic, it opens the ProB Table view at the {@link DisproverStatisticsTable}
 * and leaves the proof unchanged.
 */
public class ShowDisproverStatisticsCommand implements IProofCommand {

	@Override
	public boolean isApplicable(final IUserSupport us, final Predicate hyp,
			final String input) {
		return true;
	}

	@Override
	public void apply(final IUserSupport us, final Predicate hyp,
			final String[] inputs, final IProgressMonitor monitor) {
		// Rodin may call it outside the UI thread
		final Display display = Display.getDefault();
		if (!display.isDisposed())
			display.asyncExec(() -> ProbUiPlugin
					.showContributedTable(DisproverStatisticsTable.ID));
	}
}
