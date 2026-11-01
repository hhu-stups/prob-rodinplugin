/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.List;

/**
 * Lays out a value as printed by ProB (e.g. "{(1↦{2,3}),(4↦{5})}") over
 * several lines: a bracketed group ({...}, (...), [...]) that does not fit
 * into the remaining line is broken into one element per line, indented by
 * its nesting depth. Values whose brackets do not match are returned
 * unchanged.
 */
public final class ValuePrettyPrinter {

	private static final int INDENT = 2;

	private ValuePrettyPrinter() {
	}

	public static String prettyPrint(final String value, final int width) {
		try {
			final Parser parser = new Parser(value);
			final List<Node> seq = parser.parseSeq((char) 0);
			if (parser.pos != value.length())
				return value;
			final StringBuilder sb = new StringBuilder();
			printSeq(seq, 0, width, false, sb);
			return sb.toString();
		} catch (IllegalArgumentException e) {
			return value;
		}
	}

	private interface Node {
		String flat();
	}

	private static final class Atom implements Node {
		final String text;

		Atom(final String text) {
			this.text = text;
		}

		@Override
		public String flat() {
			return text;
		}
	}

	private static final class Group implements Node {
		final char open, close;
		final List<List<Node>> elements = new ArrayList<List<Node>>();

		Group(final char open, final char close) {
			this.open = open;
			this.close = close;
		}

		@Override
		public String flat() {
			final StringBuilder sb = new StringBuilder();
			sb.append(open);
			for (int i = 0; i < elements.size(); i++) {
				if (i > 0)
					sb.append(',');
				sb.append(flatSeq(elements.get(i)));
			}
			return sb.append(close).toString();
		}

		boolean isEmpty() {
			return elements.size() == 1 && elements.get(0).isEmpty();
		}
	}

	private static String flatSeq(final List<Node> seq) {
		final StringBuilder sb = new StringBuilder();
		for (Node n : seq)
			sb.append(n.flat());
		return sb.toString();
	}

	private static final class Parser {
		final String s;
		int pos = 0;

		Parser(final String s) {
			this.s = s;
		}

		/**
		 * Parses up to the closing bracket <code>close</code> or a comma
		 * separating elements of that group (both not consumed), or to the
		 * end if close is 0
		 */
		List<Node> parseSeq(final char close) {
			final List<Node> seq = new ArrayList<Node>();
			final StringBuilder atom = new StringBuilder();
			while (pos < s.length()) {
				final char c = s.charAt(pos);
				final char matching = closing(c);
				if (matching != 0) {
					flush(atom, seq);
					seq.add(parseGroup(c, matching));
				} else if (c == ')' || c == '}' || c == ']') {
					if (c != close)
						throw new IllegalArgumentException("unbalanced");
					break;
				} else if (c == ',' && close != 0) {
					break;
				} else if (c == '"') {
					readString(atom);
				} else {
					atom.append(c);
					pos++;
				}
			}
			if (pos >= s.length() && close != 0)
				throw new IllegalArgumentException("unclosed");
			flush(atom, seq);
			return seq;
		}

		private Group parseGroup(final char open, final char close) {
			final Group g = new Group(open, close);
			pos++; // open
			while (true) {
				g.elements.add(parseSeq(close));
				final char c = s.charAt(pos++);
				if (c == close)
					return g;
				// otherwise a comma, continue with next element
			}
		}

		private void readString(final StringBuilder atom) {
			atom.append(s.charAt(pos++)); // opening quote
			while (pos < s.length()) {
				final char c = s.charAt(pos++);
				atom.append(c);
				if (c == '\\' && pos < s.length()) {
					atom.append(s.charAt(pos++));
				} else if (c == '"') {
					return;
				}
			}
			throw new IllegalArgumentException("unclosed string");
		}

		private static void flush(final StringBuilder atom,
				final List<Node> seq) {
			if (atom.length() > 0) {
				seq.add(new Atom(atom.toString()));
				atom.setLength(0);
			}
		}

		private static char closing(final char c) {
			switch (c) {
			case '(':
				return ')';
			case '{':
				return '}';
			case '[':
				return ']';
			default:
				return 0;
			}
		}
	}

	private static void printSeq(final List<Node> seq, final int indent,
			final int width, final boolean trimStart, final StringBuilder sb) {
		boolean first = true;
		for (Node n : seq) {
			if (n instanceof Group) {
				printGroup((Group) n, indent, width, sb);
			} else {
				final String text = ((Atom) n).text;
				sb.append(first && trimStart ? text.replaceAll("^\\s+", "")
						: text);
			}
			first = false;
		}
	}

	private static void printGroup(final Group g, final int indent,
			final int width, final StringBuilder sb) {
		final String flat = g.flat();
		if (g.isEmpty() || column(sb) + flat.length() <= width) {
			sb.append(flat);
			return;
		}
		if (g.elements.size() == 1) {
			// e.g. a pair (x↦{...}): keep the brackets on the same line,
			// only the nested groups are broken
			sb.append(g.open);
			printSeq(g.elements.get(0), indent, width, false, sb);
			sb.append(g.close);
			return;
		}
		final String inner = spaces(indent + INDENT);
		sb.append(g.open);
		if (allSimple(g)) {
			// e.g. a set of numbers: fill the lines
			sb.append('\n').append(inner);
			for (int i = 0; i < g.elements.size(); i++) {
				final String element = flatSeq(g.elements.get(i)).trim();
				final boolean last = i == g.elements.size() - 1;
				if (i > 0) {
					if (column(sb) + 1 + element.length() + 1 > width) {
						sb.append('\n').append(inner);
					} else {
						sb.append(' ');
					}
				}
				sb.append(element);
				if (!last)
					sb.append(',');
			}
		} else {
			for (int i = 0; i < g.elements.size(); i++) {
				sb.append('\n').append(inner);
				printSeq(g.elements.get(i), indent + INDENT, width, true, sb);
				if (i < g.elements.size() - 1)
					sb.append(',');
			}
		}
		sb.append('\n').append(spaces(indent)).append(g.close);
	}

	/**
	 * @return true if no element of the group contains a nested group
	 */
	private static boolean allSimple(final Group g) {
		for (List<Node> element : g.elements) {
			for (Node n : element) {
				if (n instanceof Group)
					return false;
			}
		}
		return true;
	}

	private static int column(final StringBuilder sb) {
		return sb.length() - (sb.lastIndexOf("\n") + 1);
	}

	private static String spaces(final int n) {
		final StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
			sb.append(' ');
		return sb.toString();
	}
}
