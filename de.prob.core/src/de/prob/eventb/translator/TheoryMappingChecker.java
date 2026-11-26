/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.translator;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.prob.core.command.GetTheoryMappingTargetsCommand.Target;
import de.prob.eventb.translator.TheoryMappingFiles.AxiomaticOperator;
import de.prob.eventb.translator.TheoryMappingFiles.Theory;
import de.prob.tmparser.OperatorMapping;
import de.prob.tmparser.TheoryMappingException;
import de.prob.tmparser.TheoryMappingParser;

/**
 * Checks a theory mapping file (.ptm), for error markers in the editor and
 * the Problems view:
 * <ul>
 * <li>its syntax, with the parser ProB for Rodin uses when it loads a model
 * (it stops at the first error), including the B formulas between dollar
 * signs and mappings declared twice;</li>
 * <li>if the theory is known: operators the theory does not have (ProB
 * ignores their lines), and the theory's axiomatic operators without a
 * mapping and without a default of ProB (they raise an error when they are
 * evaluated);</li>
 * <li>if probcli told its tags (see {@link TheoryMappingTemplate}): unknown
 * tags, and tags for another number of arguments than the operator has.</li>
 * </ul>
 * Does not need the Theory plug-in.
 */
public final class TheoryMappingChecker {

	public enum Severity {
		ERROR, WARNING, INFO
	}

	/** a problem in the file */
	public static final class Problem {
		public final Severity severity;
		public final String message;
		/** 1-based */
		public final int line;
		/** offsets in the text, or -1 to mark the whole line */
		public final int start;
		public final int end;

		Problem(final Severity severity, final String message, final int line,
				final int start, final int end) {
			this.severity = severity;
			this.message = message;
			this.line = line;
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			return severity + " line " + line
					+ (start >= 0 ? " [" + start + "," + end + ")" : "")
					+ ": " + message;
		}
	}

	private static final Pattern POSITION = Pattern
			.compile("\\[(\\d+),(\\d+)\\]\\s*(.*)");
	private static final Pattern FORMULA_ERROR = Pattern
			.compile("Parse error in B expression for operator (.+?): (.*)",
					Pattern.DOTALL);
	private static final Pattern DECLARED_TWICE = Pattern
			.compile("Mapping for operation '(.+)' declared twice");
	private static final Pattern NUMBER = Pattern
			.compile("-?\\d+(\\.\\d+)?");

	private final String text;
	private final String theoryName;
	private final Theory theory;
	private final TheoryMappingTemplate tags;
	private final List<Problem> problems = new ArrayList<Problem>();

	/**
	 * @param theory
	 *            the theory the file is for, or null if it is not known
	 *            (e.g. without the Theory plug-in)
	 * @param tags
	 *            probcli's tags and defaults; may not know them
	 */
	private TheoryMappingChecker(final String text, final String theoryName,
			final Theory theory, final TheoryMappingTemplate tags) {
		this.text = text;
		this.theoryName = theoryName;
		this.theory = theory;
		this.tags = tags;
	}

	/**
	 * @param theoryName
	 *            the file's name without .ptm
	 * @param theory
	 *            the theory deployed in the file's project with this name, or
	 *            null if there is none or it is not known
	 * @param theoryKnown
	 *            whether the deployed theories are known (the Theory plug-in
	 *            is installed): then a null theory means there is none
	 */
	public static List<Problem> check(final String text,
			final String theoryName, final Theory theory,
			final boolean theoryKnown, final TheoryMappingTemplate tags) {
		final TheoryMappingChecker checker = new TheoryMappingChecker(text,
				theoryName, theory, tags);
		checker.run(theoryKnown);
		return checker.problems;
	}

	private void run(final boolean theoryKnown) {
		final Collection<OperatorMapping> mappings;
		try {
			mappings = TheoryMappingParser.parseTheoryMapping(theoryName,
					new StringReader(text));
		} catch (TheoryMappingException | IOException e) {
			syntaxError(e);
			return;
		}
		if (theory == null && theoryKnown)
			add(Severity.WARNING, "No theory " + theoryName
					+ " is deployed in this project: ProB only reads "
					+ theoryName + ".ptm for the deployed theory " + theoryName
					+ " of the same project.", 1, -1, -1);
		final Set<String> mapped = new HashSet<String>();
		for (OperatorMapping mapping : mappings) {
			mapped.add(mapping.getOperatorName());
			checkMapping(mapping);
		}
		if (theory != null)
			for (AxiomaticOperator op : theory.operators)
				if (!mapped.contains(op.name) && tags.knowsTargets()
						&& tags.defaultFor(op) == null)
					add(Severity.INFO, "No mapping for the axiomatic "
							+ (op.type ? "type " : "operator ") + op.name
							+ ", and ProB has no default for it: ProB raises an error when it is evaluated.",
							1, -1, -1);
	}

	private void syntaxError(final Exception e) {
		// the parser's exception is wrapped
		final String message = e.getMessage() == null ? e.toString() : e
				.getMessage();
		final Matcher position = POSITION.matcher(message);
		if (position.find()) {
			int line = Integer.parseInt(position.group(1));
			final int column = Integer.parseInt(position.group(2));
			int offset = offset(line, column);
			// on an empty line (e.g. a missing } at the end of the file):
			// the end of the previous line
			if (offset >= lineEnd(line) && offset == offset(line, 1)
					&& line > 1 && lineEnd(line - 1) > offset(line - 1, 1)) {
				line--;
				offset = lineEnd(line);
			}
			// at least one character, e.g. the last one at the end of a line
			if (offset >= lineEnd(line) && offset > offset(line, 1))
				offset--;
			final int end = Math.min(offset + 1, Math.max(lineEnd(line), offset));
			add(Severity.ERROR, "Syntax error: " + position.group(3)
					+ " (ProB cannot read the file)", line, offset, end);
			return;
		}
		final Matcher formula = FORMULA_ERROR.matcher(message);
		if (formula.find()) {
			markSpec(formula.group(1), Severity.ERROR,
					"Error in the B formula: " + formula.group(2).trim());
			return;
		}
		final Matcher twice = DECLARED_TWICE.matcher(message);
		if (twice.find()) {
			final List<int[]> lines = operatorLines(twice.group(1));
			final int[] last = lines.isEmpty() ? null : lines.get(lines
					.size() - 1);
			add(Severity.ERROR, "The operator " + twice.group(1)
					+ " is mapped twice.", last == null ? 1 : last[0],
					last == null ? -1 : last[1], last == null ? -1 : last[2]);
			return;
		}
		add(Severity.ERROR, message, 1, -1, -1);
	}

	private void checkMapping(final OperatorMapping mapping) {
		final String name = mapping.getOperatorName();
		final int arity = arity(name);
		if (theory != null && arity < 0) {
			markName(name, Severity.WARNING, "The theory " + theoryName
					+ " has no operator " + name
					+ ": ProB ignores this line.");
		}
		final String spec = mapping.getSpec().trim();
		if (spec.startsWith("$") || !tags.knowsTargets())
			return; // a B formula: checked by the parser
		final int paren = spec.indexOf('(');
		final String functor = (paren < 0 ? spec : spec.substring(0, paren))
				.trim();
		if (NUMBER.matcher(spec).matches())
			return; // a value
		final Target target = tags.target(functor);
		if (target == null) {
			markSpec(name, Severity.ERROR, "ProB has no tag " + functor
					+ ": use a tag listed by \"Create ProB Theory Mapping Files\""
					+ " (at the end of a new file) or a B formula between dollar signs.");
			return;
		}
		if (arity < 0)
			return;
		// an operator without arguments can stand for a lambda expression
		final boolean fits = target.arity == arity
				|| arity == 0 && target.arity > 0
				&& !"aggregation".equals(target.category)
				&& !"inductive_natural".equals(target.category);
		if (!fits)
			markSpec(name, Severity.ERROR, "The tag " + functor
					+ " is for operators with " + arguments(target.arity)
					+ ", but " + name + " has " + arguments(arity) + ".");
	}

	/** @return the number of the operator's arguments, or -1 if unknown */
	private int arity(final String name) {
		if (theory == null)
			return -1;
		final AxiomaticOperator op = theory.axiomaticOperator(name);
		if (op != null)
			return op.arity();
		final Integer defined = theory.definedOperators.get(name);
		return defined == null ? -1 : defined;
	}

	private static String arguments(final int n) {
		return n == 0 ? "no arguments" : n == 1 ? "1 argument" : n
				+ " arguments";
	}

	// ------------------ positions

	/**
	 * @return for each line mapping the operator: its line, and the offsets
	 *         of the name (with the quotes) and of the tag (with the braces)
	 */
	private List<int[]> operatorLines(final String name) {
		final List<int[]> result = new ArrayList<int[]>();
		final Matcher m = Pattern.compile(
				"operator\\s*(\"" + Pattern.quote(name)
						+ "\")\\s*internal\\s*(\\{[^}]*\\})?").matcher(text);
		while (m.find()) {
			final int specStart = m.group(2) == null ? m.start(1) : m
					.start(2);
			final int specEnd = m.group(2) == null ? m.end(1) : m.end(2);
			result.add(new int[] { line(m.start(1)), m.start(1), m.end(1),
					specStart, specEnd });
		}
		return result;
	}

	private void markName(final String name, final Severity severity,
			final String message) {
		final List<int[]> lines = operatorLines(name);
		if (lines.isEmpty())
			add(severity, message, 1, -1, -1);
		else
			add(severity, message, lines.get(0)[0], lines.get(0)[1],
					lines.get(0)[2]);
	}

	private void markSpec(final String name, final Severity severity,
			final String message) {
		final List<int[]> lines = operatorLines(name);
		if (lines.isEmpty())
			add(severity, message, 1, -1, -1);
		else
			add(severity, message, lines.get(0)[0], lines.get(0)[3],
					lines.get(0)[4]);
	}

	/** @return the 1-based line of the offset */
	private int line(final int offset) {
		int line = 1;
		for (int i = 0; i < offset && i < text.length(); i++)
			if (text.charAt(i) == '\n')
				line++;
		return line;
	}

	/** @return the offset of a 1-based line and column (tabs count 1) */
	private int offset(final int line, final int column) {
		int offset = 0;
		for (int l = 1; l < line && offset < text.length(); l++) {
			final int newline = text.indexOf('\n', offset);
			if (newline < 0)
				return text.length();
			offset = newline + 1;
		}
		return Math.min(offset + column - 1, lineEnd(line));
	}

	private int lineEnd(final int line) {
		int offset = 0;
		for (int l = 1; l < line; l++) {
			final int newline = text.indexOf('\n', offset);
			if (newline < 0)
				return text.length();
			offset = newline + 1;
		}
		final int newline = text.indexOf('\n', offset);
		int end = newline < 0 ? text.length() : newline;
		if (end > offset && text.charAt(end - 1) == '\r')
			end--;
		return end;
	}

	private void add(final Severity severity, final String message,
			final int line, final int start, final int end) {
		problems.add(new Problem(severity, message, line, start, end));
	}
}
