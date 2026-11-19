/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Asks ProB for the formula of an entry of the formula tree (as shown in the
 * State view) as text, using <code>evaluation_formula_text/3</code>: unlike
 * its label, which is e.g. the name of an event for its guard, or has a proof
 * info icon, it can be parsed again.
 */
public final class EvaluationFormulaTextCommand implements IComposableCommand {

	/** the kind of formula of an entry */
	public enum Kind {
		EXPRESSION, PREDICATE,
		/** no formula, e.g. a section such as the invariants */
		NONE
	}

	private static final String KIND = "Kind";
	private static final String TEXT = "Text";

	private final PrologTerm id;
	private Kind kind = Kind.NONE;
	private String text = "";

	public EvaluationFormulaTextCommand(final PrologTerm id) {
		this.id = id;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_formula_text").printTerm(id)
				.printVariable(KIND).printVariable(TEXT).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final String k = GetCommandsCommand.text(bindings.get(KIND));
		kind = "expression".equals(k) ? Kind.EXPRESSION : "predicate"
				.equals(k) ? Kind.PREDICATE : Kind.NONE;
		text = GetCommandsCommand.text(bindings.get(TEXT));
	}

	public Kind getKind() {
		return kind;
	}

	/** @return the formula, empty if there is none */
	public String getText() {
		return text;
	}
}
