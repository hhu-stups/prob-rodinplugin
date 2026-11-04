package de.prob.core.command;

import java.io.File;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parserbase.ProBParseException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Lets ProB write a graph of the state space as a GraphViz dot file.
 */
public final class WriteDotCommand implements IComposableCommand {

	public enum Graph {
		/** all states and transitions explored so far */
		STATE_SPACE("State Space"),
		/** states merged by the events enabled in them */
		SIGNATURE_MERGE("Signature Merge"),
		/** states merged by the value of an expression */
		TRANSITION_DIAGRAM("Transition Diagram");

		private final String label;

		private Graph(final String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	private final Graph graph;
	private final PrologTerm expression;
	private final File file;

	/**
	 * @param expression
	 *            the parsed expression (raw AST) for
	 *            {@link Graph#TRANSITION_DIAGRAM}, otherwise <code>null</code>
	 */
	public WriteDotCommand(final Graph graph, final PrologTerm expression,
			final File file) {
		if ((graph == Graph.TRANSITION_DIAGRAM) != (expression != null))
			throw new IllegalArgumentException(
					"only the transition diagram needs an expression");
		this.graph = graph;
		this.expression = expression;
		this.file = file;
	}

	public static void writeDot(final Animator animator, final Graph graph,
			final PrologTerm expression, final File file)
			throws ProBException {
		animator.execute(new WriteDotCommand(graph, expression, file));
	}

	/**
	 * Parses an expression of the model for
	 * {@link Graph#TRANSITION_DIAGRAM}.
	 * 
	 * @throws ProBParseException
	 *             if the expression has syntax or type errors
	 */
	public static PrologTerm parseExpression(final Animator animator,
			final String expression) throws ProBParseException {
		final StructuredPrologOutput out = new StructuredPrologOutput();
		animator.getLanguageDependendPart().parseExpression(out, expression,
				false);
		out.fullstop();
		return out.getSentences().get(0);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		switch (graph) {
		case SIGNATURE_MERGE:
			// no events are ignored
			pto.openTerm("write_dotty_signature_merge");
			pto.openList().closeList();
			break;
		case TRANSITION_DIAGRAM:
			// ProB also accepts a raw AST instead of the text
			pto.openTerm("write_dotty_transition_diagram");
			pto.printTerm(expression);
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
