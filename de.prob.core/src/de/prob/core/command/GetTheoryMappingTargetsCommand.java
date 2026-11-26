/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Asks probcli what can be written in a theory mapping file (.ptm) for a
 * Rodin theory, using <code>get_theory_mapping_targets/2</code>: the tags
 * of lines <code>operator "Op" internal {Tag}</code>, and the tags ProB uses
 * by default for axiomatic operators without such a line.
 */
public final class GetTheoryMappingTargetsCommand implements
		IComposableCommand {

	/** a tag that can be written in a .ptm file */
	public static final class Target {
		/** what is written between { and } */
		public final String tag;
		/** the number of arguments the theory's operator must have */
		public final int arity;
		/** whether the operator is mapped to a predicate (else an expression) */
		public final boolean predicate;
		/**
		 * b_operator, external_function, external_predicate, constant,
		 * aggregation or inductive_natural
		 */
		public final String category;
		/** the construct ProB uses, e.g. an AST functor or external function */
		public final String construct;

		Target(final String tag, final int arity, final boolean predicate,
				final String category, final String construct) {
			this.tag = tag;
			this.arity = arity;
			this.predicate = predicate;
			this.category = category;
			this.construct = construct;
		}

		/** @return e.g. "external function RADD" or "B operator closure" */
		public String description() {
			switch (category) {
			case "external_function":
				return "external function " + construct;
			case "external_predicate":
				return "external predicate " + construct;
			case "constant":
				return "value " + (construct.isEmpty() ? "\"\"" : construct);
			case "aggregation":
				return ("general_product".equals(construct) ? "product"
						: "sum")
						+ " of a set of integers or of a relation to integers";
			case "inductive_natural":
				return "inductive natural numbers";
			default:
				return (predicate ? "B predicate " : "B operator ")
						+ construct;
			}
		}
	}

	/** the tag ProB uses for an axiomatic operator without a .ptm line */
	public static final class Default {
		public final String operator;
		public final int arity;
		public final String tag;
		/** any, integer or real: the type the arguments need */
		public final String argType;

		Default(final String operator, final int arity, final String tag,
				final String argType) {
			this.operator = operator;
			this.arity = arity;
			this.tag = tag;
			this.argType = argType;
		}
	}

	private static final String TARGETS = "Targets";
	private static final String DEFAULTS = "Defaults";

	private List<Target> targets = Collections.emptyList();
	private List<Default> defaults = Collections.emptyList();

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_theory_mapping_targets").printVariable(TARGETS)
				.printVariable(DEFAULTS).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<Target> t = new ArrayList<Target>();
		for (PrologTerm term : (ListPrologTerm) bindings.get(TARGETS))
			if (term.hasFunctor("target", 5))
				t.add(new Target(string(term, 1), integer(term, 2),
						"predicate".equals(string(term, 3)), string(term, 4),
						string(term, 5)));
		final List<Default> d = new ArrayList<Default>();
		for (PrologTerm term : (ListPrologTerm) bindings.get(DEFAULTS))
			if (term.hasFunctor("default", 4))
				d.add(new Default(string(term, 1), integer(term, 2), string(
						term, 3), string(term, 4)));
		targets = Collections.unmodifiableList(t);
		defaults = Collections.unmodifiableList(d);
	}

	private static String string(final PrologTerm term, final int arg) {
		return term.getArgument(arg).atomicToString();
	}

	private static int integer(final PrologTerm term, final int arg)
			throws CommandException {
		try {
			return Integer.parseInt(string(term, arg));
		} catch (NumberFormatException e) {
			throw new CommandException("Unexpected arity in " + term, e);
		}
	}

	public List<Target> getTargets() {
		return targets;
	}

	public List<Default> getDefaults() {
		return defaults;
	}
}
