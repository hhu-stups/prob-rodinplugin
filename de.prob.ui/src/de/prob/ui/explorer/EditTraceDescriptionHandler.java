package de.prob.ui.explorer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Changes the description of the trace selected in the Event-B Explorer: the
 * "description" field of the trace file's top-level JSON object. The rest of
 * the file is kept as it is, and so is the trace's replay status (see
 * {@link TraceStatus#keepAfterEdit}).
 */
public class EditTraceDescriptionHandler extends AbstractHandler {

	private static final String TITLE = "Edit Trace Description";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TraceNode trace = ReplayExplorerTraceHandler.selectedTrace(event);
		if (trace == null)
			return null;
		final Shell shell = HandlerUtil.getActiveShell(event);
		final IFile file = trace.getFile();
		final String text;
		try {
			text = read(file);
		} catch (IOException e) {
			MessageDialog.openError(shell, TITLE, "Could not read "
					+ file.getFullPath() + ": " + e.getMessage());
			return null;
		}
		final TraceFiles.Summary summary = TraceFiles.summary(file);
		if (summary.steps < 0) {
			MessageDialog.openError(shell, TITLE, "The trace file "
					+ file.getFullPath() + " could not be read: "
					+ summary.error);
			return null;
		}
		final InputDialog ask = new InputDialog(shell, TITLE,
				"Description of the trace " + TraceFiles.displayName(file)
						+ ":", summary.description == null ? ""
						: summary.description, null);
		if (ask.open() != Window.OK
				|| ask.getValue().trim().equals(
						summary.description == null ? "" : summary.description))
			return null;
		final String changed;
		try {
			changed = withDescription(text, ask.getValue().trim());
		} catch (IllegalArgumentException e) {
			MessageDialog.openError(shell, TITLE, "Could not change "
					+ file.getFullPath() + ": " + e.getMessage());
			return null;
		}
		final TraceStatus.Kept kept = TraceStatus.beforeEdit(file,
				trace.getParent().getRoot());
		try {
			file.setContents(
					new ByteArrayInputStream(changed
							.getBytes(StandardCharsets.UTF_8)),
					IResource.FORCE | IResource.KEEP_HISTORY, null);
		} catch (CoreException e) {
			MessageDialog.openError(shell, TITLE, "Could not save "
					+ file.getFullPath() + ": " + e.getMessage());
			return null;
		}
		TraceStatus.keepAfterEdit(file, kept);
		return null;
	}

	private static String read(final IFile file) throws IOException {
		if (file.getLocation() == null)
			throw new IOException("not a local file");
		return new String(Files.readAllBytes(file.getLocation().toFile()
				.toPath()), StandardCharsets.UTF_8);
	}

	/**
	 * @return the JSON text with the top-level "description" set to the
	 *         given text, added at the start of the object if missing
	 * @throws IllegalArgumentException
	 *             if the text is not a JSON object
	 */
	static String withDescription(final String json, final String description) {
		final String value = quote(description);
		int pos = skipSpace(json, 0);
		if (pos >= json.length() || json.charAt(pos) != '{')
			throw new IllegalArgumentException("not a JSON object");
		final int objectStart = pos;
		pos++;
		int depth = 1;
		boolean expectKey = true;
		while (pos < json.length() && depth > 0) {
			final char c = json.charAt(pos);
			if (c == '"') {
				final int end = stringEnd(json, pos);
				if (depth == 1 && expectKey) {
					final String key = SimpleJson.parse(json.substring(pos,
							end)).toString();
					int colon = skipSpace(json, end);
					if (colon >= json.length() || json.charAt(colon) != ':')
						throw new IllegalArgumentException("expected ':'");
					if (key.equals("description")) {
						final int valueStart = skipSpace(json, colon + 1);
						final int valueEnd = valueEnd(json, valueStart);
						return json.substring(0, valueStart) + value
								+ json.substring(valueEnd);
					}
					expectKey = false;
					pos = colon + 1;
					continue;
				}
				pos = end;
				continue;
			}
			if (c == '{' || c == '[')
				depth++;
			else if (c == '}' || c == ']')
				depth--;
			else if (c == ',' && depth == 1)
				expectKey = true;
			pos++;
		}
		// no description yet: add it as the first field
		final int next = skipSpace(json, objectStart + 1);
		final boolean empty = next < json.length() && json.charAt(next) == '}';
		return json.substring(0, objectStart + 1) + "\n  \"description\" : "
				+ value + (empty ? "\n" : ",") + json.substring(objectStart + 1);
	}

	/** @return the index after the string starting at pos */
	private static int stringEnd(final String json, final int pos) {
		int i = pos + 1;
		while (i < json.length()) {
			final char c = json.charAt(i);
			if (c == '\\')
				i += 2;
			else if (c == '"')
				return i + 1;
			else
				i++;
		}
		throw new IllegalArgumentException("unterminated string");
	}

	/** @return the index after the JSON value starting at pos */
	private static int valueEnd(final String json, final int pos) {
		if (pos < json.length() && json.charAt(pos) == '"')
			return stringEnd(json, pos);
		int depth = 0;
		int i = pos;
		while (i < json.length()) {
			final char c = json.charAt(i);
			if (c == '"') {
				i = stringEnd(json, i);
				continue;
			}
			if (c == '{' || c == '[')
				depth++;
			else if (c == '}' || c == ']') {
				if (depth == 0)
					return i;
				depth--;
				if (depth == 0)
					return i + 1;
			} else if (c == ',' && depth == 0)
				return i;
			i++;
		}
		throw new IllegalArgumentException("unexpected end");
	}

	private static int skipSpace(final String json, int pos) {
		while (pos < json.length() && Character.isWhitespace(json.charAt(pos)))
			pos++;
		return pos;
	}

	/** @return the text as a JSON string */
	private static String quote(final String text) {
		final StringBuilder result = new StringBuilder("\"");
		for (char c : text.toCharArray()) {
			switch (c) {
			case '"':
				result.append("\\\"");
				break;
			case '\\':
				result.append("\\\\");
				break;
			case '\n':
				result.append("\\n");
				break;
			case '\r':
				result.append("\\r");
				break;
			case '\t':
				result.append("\\t");
				break;
			default:
				if (c < 0x20)
					result.append(String.format("\\u%04x", (int) c));
				else
					result.append(c);
			}
		}
		return result.append('"').toString();
	}
}
