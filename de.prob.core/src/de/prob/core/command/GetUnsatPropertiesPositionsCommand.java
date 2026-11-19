/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Asks ProB for the axioms (properties) which prevented finding valid
 * constants, as far as their Rodin labels are known.
 */
public final class GetUnsatPropertiesPositionsCommand implements
		IComposableCommand {

	private static final String RESULT_VARIABLE = "Positions";

	/** an axiom, given by the name of its context and its label */
	public static final class Position {
		public final String component;
		public final String label;
		/** if the axiom is false, i.e., not only of unknown value */
		public final boolean isFalse;
		/**
		 * if there is no solution only for the axiom together with other
		 * axioms (of the same component), not for the axiom alone
		 */
		public final boolean together;

		Position(final String component, final String label,
				final boolean isFalse, final boolean together) {
			this.component = component;
			this.label = label;
			this.isFalse = isFalse;
			this.together = together;
		}
	}

	private List<Position> positions = Collections.emptyList();

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("unsat_properties_positions")
				.printVariable(RESULT_VARIABLE).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<Position> result = new ArrayList<Position>();
		try {
			for (final PrologTerm term : BindingGenerator.getList(bindings,
					RESULT_VARIABLE)) {
				if (term.hasFunctor("pos", 4)) {
					result.add(new Position(
							term.getArgument(1).atomicToString(),
							term.getArgument(2).atomicToString(),
							"false".equals(term.getArgument(3).atomicToString()),
							"component".equals(term.getArgument(4)
									.atomicToString())));
				}
			}
		} catch (ResultParserException e) {
			throw new CommandException(e.getLocalizedMessage(), e);
		}
		positions = result;
	}

	public List<Position> getPositions() {
		return positions;
	}
}
