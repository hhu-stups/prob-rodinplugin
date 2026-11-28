/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.ltl;

import de.be4.ltl.core.parser.LtlParseException;
import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.parserbase.ProBParseException;

/**
 * Explains errors in LTL formulas, for the console's :ltl and the LTL Model
 * Checking dialog.
 */
public final class LtlErrors {

	private LtlErrors() {
	}

	/**
	 * Explains an error in an LTL formula: whether it is in the LTL syntax or
	 * in a B part ({PRED} or [EVENT]), and a syntax or type error; where it
	 * is (marked below the formula if withMarker, for a monospaced font;
	 * otherwise the column); for a type error, the types of the
	 * model's identifiers in the B part.
	 */
	public static String describe(final Animator animator,
			final String formula, final LtlParseException e,
			final boolean withMarker) {
		final Throwable cause = e.getCause();
		String token = null;
		int line = -1, column = -1;
		try {
			token = e.getTokenString();
			line = e.getTokenLine();
			column = e.getTokenColumn();
		} catch (RuntimeException noToken) {
			// no position
		}
		final StringBuilder sb = new StringBuilder();
		if (cause instanceof ProBParseException) {
			// a predicate or event in the formula
			String message = cause.getMessage() == null ? "" : cause
					.getMessage().trim();
			final boolean typeError = message.toLowerCase().contains("type");
			final java.util.regex.Matcher m = TYPES_DO_NOT_MATCH
					.matcher(message);
			if (m.find())
				message = "the types " + m.group(1).trim() + " and "
						+ m.group(2).trim() + " do not match"
						+ message.substring(m.end());
			sb.append(typeError ? "Type error" : "Syntax error");
			if (token != null && !token.trim().isEmpty())
				sb.append(" in ").append(braced(token.trim()));
			sb.append(": ").append(message);
			if (withMarker)
				marker(sb, formula, token, line, column);
			else if (column >= 1)
				sb.append(" (column ").append(column).append(')');
			if (typeError && token != null) {
				final String types = identifierTypes(animator, token);
				if (!types.isEmpty())
					sb.append("\nTypes in the model: ").append(types);
			}
		} else {
			// the LTL syntax: "[1,5] expecting: ..." or "Unknown token"
			String message = cause != null && cause.getMessage() != null ? cause
					.getMessage() : e.getMessage();
			if (message == null)
				message = String.valueOf(cause != null ? cause : e);
			message = message.replaceFirst("^[\\w.]+Exception:\\s*", "");
			// "[line,column] ...": the position if the token is unknown
			final java.util.regex.Matcher pos = java.util.regex.Pattern
					.compile("^\\s*\\[(\\d+),(\\d+)\\]\\s*").matcher(message);
			if (pos.find()) {
				if (column < 1) {
					line = Integer.parseInt(pos.group(1));
					column = Integer.parseInt(pos.group(2));
				}
				message = message.substring(pos.end());
			}
			message = shortenExpecting(message);
			sb.append("Syntax error in the LTL formula");
			if (token != null && !token.trim().isEmpty())
				sb.append(" at ").append(token.trim());
			sb.append(": ").append(message);
			if (withMarker)
				marker(sb, formula, token, line, column);
			else if (column >= 1)
				sb.append(" (column ").append(column).append(')');
			sb.append("\nLTL syntax, e.g.: G {PRED}, F [EVENT], X, U, W, R, not, &, or, =>, e(EVENT), deadlock, sink");
		}
		// not translated into mathematical symbols: the marker would no
		// longer be below the token
		return sb.toString();
	}

	/** "expecting: a, b, ..." with at most 12 alternatives */
	private static String shortenExpecting(final String message) {
		final int i = message.indexOf("expecting:");
		if (i < 0)
			return message;
		final String[] alternatives = message.substring(
				i + "expecting:".length()).split(",");
		if (alternatives.length <= 12)
			return message;
		final StringBuilder sb = new StringBuilder(message.substring(0, i))
				.append("expecting one of:");
		for (int k = 0; k < 12; k++)
			sb.append(k == 0 ? "" : ",").append(alternatives[k]);
		return sb.append(", ...").toString();
	}

	private static final java.util.regex.Pattern TYPES_DO_NOT_MATCH = java.util.regex.Pattern
			.compile("Type:\\s*(.+?)\\s+does not match type:\\s*(\\S+)");

	/** the B part as written in the formula, in braces if it has none */
	private static String braced(final String token) {
		if (token.startsWith("{") || token.startsWith("["))
			return token;
		return "{" + token + "}";
	}

	/** "cur : S, ..." for the predicate (or [EVENT | PRED]) of the token */
	private static String identifierTypes(final Animator animator,
			final String token) {
		final LanguageDependendAnimationPart ldp = animator == null ? null
				: animator.getLanguageDependendPart();
		if (!(ldp instanceof EventBAnimatorPart))
			return "";
		String text = token.trim();
		if ((text.startsWith("{") && text.endsWith("}"))
				|| (text.startsWith("[") && text.endsWith("]")))
			text = text.substring(1, text.length() - 1);
		final int bar = text.indexOf('|');
		if (token.trim().startsWith("[") && bar >= 0)
			text = text.substring(bar + 1);
		try {
			return ((EventBAnimatorPart) ldp).describeIdentifierTypes(text);
		} catch (RuntimeException e) {
			return "";
		}
	}

	/** the formula with the token (or its position) marked below it */
	private static void marker(final StringBuilder sb, final String formula,
			final String token, final int line, final int column) {
		final String[] lines = formula.split("\n", -1);
		final int l = line >= 1 && line <= lines.length ? line - 1 : 0;
		final String text = lines[l];
		int start = column - 1;
		int length = token == null ? 1 : Math.max(1, token.length());
		if (token != null && !token.isEmpty()) {
			if (start < 0 || !text.startsWith(token, start)) {
				// the column may be off, e.g. at the brace
				final int found = text.indexOf(token.trim());
				if (found >= 0) {
					start = found;
					length = token.trim().length();
				}
			}
		}
		if (start < 0 || start > text.length())
			return;
		length = Math.min(length, Math.max(1, text.length() - start));
		final StringBuilder underline = new StringBuilder();
		for (int i = 0; i < start; i++)
			underline.append(text.charAt(i) == '\t' ? '\t' : ' ');
		for (int i = 0; i < length; i++)
			underline.append('^');
		sb.append("\n  ").append(text).append("\n  ").append(underline);
	}

}
