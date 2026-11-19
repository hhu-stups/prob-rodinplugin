package de.prob.ui.explorer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small JSON reader, enough for trace files: objects become
 * {@link LinkedHashMap}s, arrays {@link ArrayList}s, numbers
 * {@link Double}s, and true, false and null {@link Boolean}s and
 * <code>null</code>.
 */
final class SimpleJson {

	private final String text;
	private int pos;

	private SimpleJson(final String text) {
		this.text = text;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the text is not valid JSON
	 */
	static Object parse(final String text) {
		final SimpleJson json = new SimpleJson(text);
		final Object value = json.value();
		json.skipSpace();
		if (json.pos != text.length())
			throw json.error("unexpected text after the value");
		return value;
	}

	private Object value() {
		skipSpace();
		if (pos >= text.length())
			throw error("unexpected end");
		final char c = text.charAt(pos);
		switch (c) {
		case '{':
			return object();
		case '[':
			return array();
		case '"':
			return string();
		case 't':
			return literal("true", Boolean.TRUE);
		case 'f':
			return literal("false", Boolean.FALSE);
		case 'n':
			return literal("null", null);
		default:
			return number();
		}
	}

	private Map<String, Object> object() {
		final Map<String, Object> result = new LinkedHashMap<String, Object>();
		pos++; // {
		skipSpace();
		if (peek('}'))
			return result;
		do {
			skipSpace();
			final String key = string();
			skipSpace();
			expect(':');
			result.put(key, value());
			skipSpace();
		} while (peek(','));
		expect('}');
		return result;
	}

	private List<Object> array() {
		final List<Object> result = new ArrayList<Object>();
		pos++; // [
		skipSpace();
		if (peek(']'))
			return result;
		do {
			result.add(value());
			skipSpace();
		} while (peek(','));
		expect(']');
		return result;
	}

	private String string() {
		expect('"');
		final StringBuilder result = new StringBuilder();
		while (true) {
			if (pos >= text.length())
				throw error("unterminated string");
			final char c = text.charAt(pos++);
			if (c == '"')
				return result.toString();
			if (c != '\\') {
				result.append(c);
				continue;
			}
			if (pos >= text.length())
				throw error("unterminated string");
			final char e = text.charAt(pos++);
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
					throw error("bad \\u escape");
				result.append((char) Integer.parseInt(
						text.substring(pos, pos + 4), 16));
				pos += 4;
				break;
			default: // " \ /
				result.append(e);
			}
		}
	}

	private Object literal(final String word, final Object value) {
		if (!text.startsWith(word, pos))
			throw error("unexpected character");
		pos += word.length();
		return value;
	}

	private Double number() {
		final int start = pos;
		while (pos < text.length() && "+-0123456789.eE".indexOf(text.charAt(pos)) >= 0)
			pos++;
		if (start == pos)
			throw error("unexpected character");
		try {
			return Double.valueOf(text.substring(start, pos));
		} catch (NumberFormatException e) {
			throw error("bad number");
		}
	}

	private void skipSpace() {
		while (pos < text.length() && Character.isWhitespace(text.charAt(pos)))
			pos++;
	}

	private boolean peek(final char c) {
		if (pos < text.length() && text.charAt(pos) == c) {
			pos++;
			return true;
		}
		return false;
	}

	private void expect(final char c) {
		if (!peek(c))
			throw error("expected '" + c + "'");
	}

	private IllegalArgumentException error(final String message) {
		return new IllegalArgumentException("Invalid JSON at character "
				+ pos + ": " + message);
	}
}
