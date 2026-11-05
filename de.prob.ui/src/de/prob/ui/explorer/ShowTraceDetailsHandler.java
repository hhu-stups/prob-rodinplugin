package de.prob.ui.explorer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Shows the details of the trace selected in the Event-B Explorer: its
 * metadata (creation date, creator, ProB version, model), file, replay
 * status and steps.
 */
public class ShowTraceDetailsHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TraceNode trace = ReplayExplorerTraceHandler.selectedTrace(event);
		if (trace == null)
			return null;
		final Shell shell = HandlerUtil.getActiveShell(event);
		new DetailsDialog(shell, TraceFiles.displayName(trace.getFile()),
				details(trace))
				.open();
		return null;
	}

	/** @return the details as text */
	static String details(final TraceNode trace) {
		final IFile file = trace.getFile();
		final StringBuilder text = new StringBuilder();
		line(text, "File", file.getFullPath().toString());
		final File local = file.getLocation() == null ? null : file
				.getLocation().toFile();
		if (local != null)
			line(text, "Size", (local.length() + 1023) / 1024 + " KB, modified "
					+ format(new Date(local.lastModified())));
		line(text, "Replay status", replayStatus(trace));

		final Map<?, ?> json;
		try {
			final Object parsed = SimpleJson.parse(new String(Files
					.readAllBytes(local.toPath()), StandardCharsets.UTF_8));
			if (!(parsed instanceof Map))
				throw new IllegalArgumentException("not a JSON object");
			json = (Map<?, ?>) parsed;
		} catch (IOException | RuntimeException e) {
			text.append("\nThe trace file could not be read: ").append(
					e.getMessage());
			return text.toString();
		}

		final Map<?, ?> metadata = json.get("metadata") instanceof Map ? (Map<?, ?>) json
				.get("metadata") : null;
		final List<?> steps = json.get("transitionList") instanceof List ? (List<?>) json
				.get("transitionList") : null;
		line(text, "Steps", steps == null ? "none" : String.valueOf(steps
				.size()));
		line(text, "Description", json.get("description"));
		if (metadata != null) {
			line(text, "Model", metadata.get("modelName"));
			line(text, "Model file", metadata.get("modelFile"));
			line(text, "Created", metadata.get("savedAt"));
			line(text, "Creator", metadata.get("creator"));
			line(text, "ProB version", join(metadata.get("proBCliVersion"),
					metadata.get("proBCliRevision")));
			line(text, "Format", join(metadata.get("fileType"), "version "
					+ number(metadata.get("formatVersion"))));
		}
		if (steps != null && !steps.isEmpty()) {
			text.append("\nSteps:\n");
			int n = 1;
			for (Object step : steps)
				text.append(String.format("%4d. ", n++)).append(step(step))
						.append('\n');
		}
		return text.toString();
	}

	/** @return e.g. inc(x=1, y=2) --> r=3 */
	private static String step(final Object step) {
		if (!(step instanceof Map))
			return String.valueOf(step);
		final Map<?, ?> map = (Map<?, ?>) step;
		final StringBuilder text = new StringBuilder(String.valueOf(map
				.get("name")));
		final String params = bindings(map.get("params"));
		if (!params.isEmpty())
			text.append('(').append(params).append(')');
		final String results = bindings(map.get("results"));
		if (!results.isEmpty())
			text.append(" --> ").append(results);
		return text.toString();
	}

	private static String bindings(final Object object) {
		if (!(object instanceof Map))
			return "";
		final StringBuilder text = new StringBuilder();
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
			if (text.length() > 0)
				text.append(", ");
			text.append(entry.getKey()).append('=').append(entry.getValue());
		}
		return text.toString();
	}

	private static String replayStatus(final TraceNode trace) {
		final String detail = TraceStatus.getDetail(trace.getFile());
		switch (TraceStatus.get(trace.getFile(), trace.getParent().getRoot())) {
		case PERFECT:
			return "replayed perfectly (" + detail + ")";
		case IMPERFECT:
			return "replayed imperfectly (" + detail + ")";
		case FAILED:
			return "replay failed (" + detail + ")";
		default:
			return "not replayed for the current model and trace";
		}
	}

	private static void line(final StringBuilder text, final String label,
			final Object value) {
		if (value == null || String.valueOf(value).isEmpty())
			return;
		text.append(String.format("%-14s %s%n", label + ":", value));
	}

	private static String join(final Object a, final Object b) {
		if (a == null)
			return b == null ? null : String.valueOf(b);
		return b == null ? String.valueOf(a) : a + " (" + b + ")";
	}

	/** @return 1 instead of 1.0 */
	private static String number(final Object value) {
		if (value instanceof Double
				&& ((Double) value) == Math.rint((Double) value))
			return String.valueOf(((Double) value).longValue());
		return String.valueOf(value);
	}

	private static String format(final Date date) {
		return DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.SHORT).format(date);
	}

	/** the details in a read-only text, which can be selected and copied */
	private static final class DetailsDialog extends Dialog {
		private final String title;
		private final String details;

		DetailsDialog(final Shell shell, final String title,
				final String details) {
			super(shell);
			this.title = title;
			this.details = details;
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		@Override
		protected void configureShell(final Shell shell) {
			super.configureShell(shell);
			shell.setText("Trace Details: " + title);
		}

		@Override
		protected Control createDialogArea(final Composite parent) {
			final Composite area = (Composite) super.createDialogArea(parent);
			final Text text = new Text(area, SWT.MULTI | SWT.READ_ONLY
					| SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			text.setFont(JFaceResources.getTextFont());
			text.setText(details);
			final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.widthHint = convertWidthInCharsToPixels(100);
			data.heightHint = convertHeightInCharsToPixels(25);
			text.setLayoutData(data);
			return area;
		}

		@Override
		protected void createButtonsForButtonBar(final Composite parent) {
			createButton(parent, IDialogConstants.OK_ID,
					IDialogConstants.CLOSE_LABEL, true);
		}
	}
}
