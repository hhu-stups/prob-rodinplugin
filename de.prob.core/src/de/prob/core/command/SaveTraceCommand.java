/**
 * (c) 2009-2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryItem;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Saves the animator's trace as a ProB2 JSON trace file (.prob2trace), using
 * <code>prob2_save_trace_file/4</code>. The trace is passed as transition ids,
 * as ProB's own history does not follow the jumps in the history view.
 */
public final class SaveTraceCommand implements IComposableCommand {

	private static final String DESCRIPTION = "File created by ProB for Rodin";

	private final List<Long> transitionIds;
	private final String modelFile;
	private final File file;

	/**
	 * @param transitionIds
	 *            the transitions of the trace, starting from the root
	 * @param modelFile
	 *            the model's file, stored in the trace file's metadata (ProB
	 *            does not know it: the plug-in sends the model itself)
	 */
	public SaveTraceCommand(final List<Long> transitionIds,
			final String modelFile, final File file) {
		this.transitionIds = transitionIds;
		this.modelFile = modelFile;
		this.file = file;
	}

	/**
	 * Saves the trace from the root to the current state of the history.
	 * Does not notify the user if ProB fails: the exception's message has
	 * ProB's error messages, see {@link Animator#executeQuietly}.
	 */
	public static void saveCurrentTrace(final Animator animator,
			final String modelFile, final File file) throws ProBException {
		animator.executeQuietly(new SaveTraceCommand(
				currentTransitionIds(animator.getHistory()), modelFile, file));
	}

	/** @return the transitions from the root to the current state */
	private static List<Long> currentTransitionIds(final History history) {
		final List<Long> ids = new ArrayList<Long>();
		// the operation of an item leads to the next item
		synchronized (history) {
			final HistoryItem[] items = history.getAllItems();
			final int current = history.getCurrentPosition();
			for (int i = 0; i < current && i < items.length; i++) {
				final Operation operation = items[i].getOperation();
				if (operation != null && operation.getId() >= 0)
					ids.add(operation.getId());
			}
		}
		return ids;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob2_save_trace_file");
		pto.openList();
		for (Long id : transitionIds)
			pto.printNumber(id);
		pto.closeList();
		pto.printAtom("json");
		pto.openList();
		pto.openTerm("=").printAtom("description").printAtom(DESCRIPTION)
				.closeTerm();
		pto.openTerm("=").printAtom("model_file").printAtom(modelFile)
				.closeTerm();
		pto.closeList();
		pto.printAtom(file.getAbsolutePath());
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// the result is the file
	}
}
