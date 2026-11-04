package de.prob.core.command;

import java.io.File;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Lets ProB write a graph of the state space as a GraphViz dot file.
 */
public final class WriteDotCommand implements IComposableCommand {

	public enum Graph {
		/** all states and transitions explored so far */
		STATE_SPACE("State Space"),
		/** states merged by the events enabled in them */
		SIGNATURE_MERGE("Signature Merge");

		private final String label;

		private Graph(final String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	private final Graph graph;
	private final File file;

	public WriteDotCommand(final Graph graph, final File file) {
		this.graph = graph;
		this.file = file;
	}

	public static void writeDot(final Animator animator, final Graph graph,
			final File file) throws ProBException {
		animator.execute(new WriteDotCommand(graph, file));
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		switch (graph) {
		case SIGNATURE_MERGE:
			// no events are ignored
			pto.openTerm("write_dotty_signature_merge");
			pto.openList().closeList();
			break;
		default:
			pto.openTerm("write_dotty_state_space");
			break;
		}
		pto.printAtom(file.getAbsolutePath());
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// the result is the file
	}
}
