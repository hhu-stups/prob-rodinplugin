/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.settings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small JSON reader and writer for the settings files: objects become
 * {@link LinkedHashMap}s (keeping the order of their keys), arrays
 * {@link List}s, numbers {@link Double}s or {@link Long}s, and
 * <code>true</code>, <code>false</code> and <code>null</code> the Java values.
 */
final class Json {

	private final String text;
	private int pos;

	private Json(final String text) {
		this.text = text;
	}

	/** @throws IllegalArgumentException if the text is not JSON */
	static Object parse(final String text) {
		final Json json = new Json(text);
		json.skipSpace();
		final Object value = json.value();
		json.skipSpace();
		if (json.pos < text.length())
			throw json.error("unexpected text after the value");
		return value;
	}

	private IllegalArgumentException error(final String message) {
		int line = 1;
		for (int i = 0; i < pos && i < text.length(); i++)
			if (text.charAt(i) == '\n')
				line++;
		return new IllegalArgumentException(message + " (line " + line + ")");
	}

	private void skipSpace() {
		while (pos < text.length()
				&& Character.isWhitespace(text.charAt(pos)))
			pos++;
	}

	private char peek() {
		if (pos >= text.length())
			throw error("unexpected end");
		return text.charAt(pos);
	}

	private void expect(final char c) {
		if (peek() != c)
			throw error("expected '" + c + "'");
		pos++;
	}

	private Object value() {
		final char c = peek();
		if (c == '{')
			return object();
		if (c == '[')
			return array();
		if (c == '"')
			return string();
		if (text.startsWith("true", pos)) {
			pos += 4;
			return Boolean.TRUE;
		}
		if (text.startsWith("false", pos)) {
			pos += 5;
			return Boolean.FALSE;
		}
		if (text.startsWith("null", pos)) {
			pos += 4;
			return null;
		}
		return number();
	}

	private Map<String, Object> object() {
		final Map<String, Object> result = new LinkedHashMap<String, Object>();
		expect('{');
		skipSpace();
		if (peek() == '}') {
			pos++;
			return result;
		}
		while (true) {
			skipSpace();
			final String key = string();
			skipSpace();
			expect(':');
			skipSpace();
			result.put(key, value());
			skipSpace();
			if (peek() == ',') {
				pos++;
				continue;
			}
			expect('}');
			return result;
		}
	}

	private List<Object> array() {
		final List<Object> result = new ArrayList<Object>();
		expect('[');
		skipSpace();
		if (peek() == ']') {
			pos++;
			return result;
		}
		while (true) {
			skipSpace();
			result.add(value());
			skipSpace();
			if (peek() == ',') {
				pos++;
				continue;
			}
			expect(']');
			return result;
		}
	}

	private String string() {
		expect('"');
		final StringBuilder result = new StringBuilder();
		while (true) {
			final char c = peek();
			pos++;
			if (c == '"')
				return result.toString();
			if (c != '\\') {
				result.append(c);
				continue;
			}
			final char e = peek();
			pos++;
			switch (e) {
			case 'n':
				result.append('\n');
				break;
			case 't':
				result.append('\t');
				break;
			case 'r':
				result.append('\r');
				break;
			case 'b':
				result.append('\b');
				break;
			case 'f':
				result.append('\f');
				break;
			case 'u':
				if (pos + 4 > text.length())
					throw error("incomplete \\u escape");
				try {
					result.append((char) Integer.parseInt(
							text.substring(pos, pos + 4), 16));
				} catch (NumberFormatException ex) {
					throw error("invalid \\u escape");
				}
				pos += 4;
				break;
			default: // " \ /
				result.append(e);
			}
		}
	}

	private Object number() {
		final int start = pos;
		while (pos < text.length()
				&& "+-0123456789.eE".indexOf(text.charAt(pos)) >= 0)
			pos++;
		final String number = text.substring(start, pos);
		if (number.isEmpty())
			throw error("unexpected '" + text.charAt(start) + "'");
		try {
			if (number.matches("-?\\d+"))
				return Long.valueOf(number);
			return Double.valueOf(number);
		} catch (NumberFormatException e) {
			throw error("invalid number " + number);
		}
	}

	/** @return the value as JSON, indented with tabs */
	static String write(final Object value) {
		final StringBuilder out = new StringBuilder();
		write(out, value, 0);
		out.append('\n');
		return out.toString();
	}

	private static void write(final StringBuilder out, final Object value,
			final int indent) {
		if (value instanceof Map) {
			final Map<?, ?> map = (Map<?, ?>) value;
			if (map.isEmpty()) {
				out.append("{}");
				return;
			}
			out.append("{\n");
			int i = 0;
			for (Map.Entry<?, ?> e : map.entrySet()) {
				indent(out, indent + 1);
				writeString(out, String.valueOf(e.getKey()));
				out.append(": ");
				write(out, e.getValue(), indent + 1);
				out.append(++i < map.size() ? ",\n" : "\n");
			}
			indent(out, indent);
			out.append('}');
		} else if (value instanceof List) {
			final List<?> list = (List<?>) value;
			if (list.isEmpty()) {
				out.append("[]");
				return;
			}
			out.append("[\n");
			for (int i = 0; i < list.size(); i++) {
				indent(out, indent + 1);
				write(out, list.get(i), indent + 1);
				out.append(i + 1 < list.size() ? ",\n" : "\n");
			}
			indent(out, indent);
			out.append(']');
		} else if (value instanceof String) {
			writeString(out, (String) value);
		} else {
			out.append(String.valueOf(value)); // numbers, booleans, null
		}
	}

	private static void indent(final StringBuilder out, final int indent) {
		for (int i = 0; i < indent; i++)
			out.append('\t');
	}

	private static void writeString(final StringBuilder out, final String s) {
		out.append('"');
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			switch (c) {
			case '"':
				out.append("\\\"");
				break;
			case '\\':
				out.append("\\\\");
				break;
			case '\n':
				out.append("\\n");
				break;
			case '\r':
				out.append("\\r");
				break;
			case '\t':
				out.append("\\t");
				break;
			default:
				if (c < 0x20)
					out.append(String.format("\\u%04x", (int) c));
				else
					out.append(c); // UTF-8: mathematical symbols are kept
			}
		}
		out.append('"');
	}
}
