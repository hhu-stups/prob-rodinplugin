package de.prob.eventb.disprover.ui.export;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Performs the same export as {@link ExportPOsHandler} without adding type predicates.
 * To export a ProB sequent prover trace to a BPR file, we need the original predicate from Rodin.
 * See commandId="de.prob.eventb.disprover.ui.exportpos_seqprover" in plugin.xml
 */
public class ExportSequentProverPOsHandler extends ExportPOsHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		return execute(event, false);
	}
}
