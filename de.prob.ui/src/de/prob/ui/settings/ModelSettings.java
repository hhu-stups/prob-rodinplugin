/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.settings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IEventBRoot;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.logging.Logger;
import de.prob.ui.ProbUiPlugin;
import de.prob.ui.explorer.TraceFiles;

/**
 * The settings of a machine or context, in a JSON file in the model's folder
 * of its project, next to its traces: e.g. <code>M1/M1.probsettings</code>
 * for <code>M1.bum</code>. They are shared with the project (e.g. in git).
 * For now they are the formulas saved in the ProB Graph and Table views and
 * the formulas added to the State view:
 *
 * <pre>
 * {
 *   "metadata": { "fileType": "ProB for Rodin Settings", "formatVersion": 1, "model": "M1" },
 *   "graphFormulas": [ { "formula": "card(active)", "command": "expr_as_graph" } ],
 *   "tableFormulas": [ { "formula": "owner", "command": "expr_as_table" } ],
 *   "stateFormulas": [ { "formula": "card(active) > 1" } ]
 * }
 * </pre>
 *
 * Other entries of the file are kept when it is written. The last formula
 * used for each graph or table is not shared: it is stored in the workspace,
 * with the model's file.
 */
public final class ModelSettings {

	/** the kinds of formulas, i.e. where they are used */
	public enum Kind {
		GRAPH("graphFormulas"), TABLE("tableFormulas"), STATE("stateFormulas");

		final String key;

		Kind(final String key) {
			this.key = key;
		}
	}

	/** a saved formula */
	public static final class SavedFormula {
		/** the formula as entered */
		public final String formula;
		/**
		 * ProB's command it was saved for, e.g. expr_as_graph; null for the
		 * State view
		 */
		public final String command;

		public SavedFormula(final String formula, final String command) {
			this.formula = formula;
			this.command = command;
		}

		boolean same(final String otherFormula, final String otherCommand) {
			return formula.equals(otherFormula)
					&& (command == null ? otherCommand == null : command
							.equals(otherCommand));
		}
	}

	public static final String EXTENSION = "probsettings";

	private static final String FILE_TYPE = "ProB for Rodin Settings";

	private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<Runnable>();

	private ModelSettings() {
	}

	/** called (in the UI thread) when settings were changed by ProB for Rodin */
	public static void addListener(final Runnable listener) {
		LISTENERS.add(listener);
	}

	public static void removeListener(final Runnable listener) {
		LISTENERS.remove(listener);
	}

	/**
	 * @return the animated machine or context, or <code>null</code> if no
	 *         Event-B model is animated
	 */
	public static IEventBRoot animatedModel() {
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (!animator.isMachineLoaded() || !(ldp instanceof EventBAnimatorPart))
			return null;
		return ((EventBAnimatorPart) ldp).getRoot();
	}

	/** @return the settings file of the model, which may not exist */
	public static IFile file(final IEventBRoot model) {
		return TraceFiles.folder(model).getFile(
				model.getComponentName() + "." + EXTENSION);
	}

	/**
	 * @return the saved formulas of this kind, in the order they were saved;
	 *         empty if there are none or the file cannot be read (see
	 *         {@link #readError(IEventBRoot)})
	 */
	public static List<SavedFormula> formulas(final IEventBRoot model,
			final Kind kind) {
		final Map<String, Object> settings;
		try {
			settings = read(model);
		} catch (CoreException e) {
			return Collections.emptyList();
		}
		final List<SavedFormula> result = new ArrayList<SavedFormula>();
		final Object list = settings.get(kind.key);
		if (list instanceof List)
			for (Object entry : (List<?>) list)
				if (entry instanceof Map) {
					final Object formula = ((Map<?, ?>) entry).get("formula");
					final Object command = ((Map<?, ?>) entry).get("command");
					if (formula instanceof String
							&& !((String) formula).trim().isEmpty())
						result.add(new SavedFormula((String) formula,
								command instanceof String ? (String) command
										: null));
				}
		return result;
	}

	/**
	 * @return why the settings file cannot be read, or <code>null</code> if
	 *         it can (or does not exist)
	 */
	public static String readError(final IEventBRoot model) {
		try {
			read(model);
			return null;
		} catch (CoreException e) {
			return e.getMessage();
		}
	}

	/**
	 * Saves a formula, unless it is already saved (for the same command).
	 *
	 * @param command
	 *            ProB's command; null for the State view
	 * @return whether it was added
	 */
	public static boolean add(final IEventBRoot model, final Kind kind,
			final String formula, final String command) throws CoreException {
		for (SavedFormula saved : formulas(model, kind))
			if (saved.same(formula, command))
				return false;
		final Map<String, Object> settings = read(model);
		final List<Object> list = list(settings, kind);
		final Map<String, Object> entry = new LinkedHashMap<String, Object>();
		entry.put("formula", formula);
		if (command != null)
			entry.put("command", command);
		list.add(entry);
		write(model, settings);
		return true;
	}

	/** removes a saved formula; @return whether it was saved */
	public static boolean remove(final IEventBRoot model, final Kind kind,
			final String formula, final String command) throws CoreException {
		final Map<String, Object> settings = read(model);
		final List<Object> list = list(settings, kind);
		boolean removed = false;
		for (int i = list.size() - 1; i >= 0; i--) {
			final Object entry = list.get(i);
			if (!(entry instanceof Map))
				continue;
			final Object f = ((Map<?, ?>) entry).get("formula");
			final Object c = ((Map<?, ?>) entry).get("command");
			if (formula.equals(f)
					&& (command == null ? c == null : command.equals(c))) {
				list.remove(i);
				removed = true;
			}
		}
		if (removed)
			write(model, settings);
		return removed;
	}

	/**
	 * @return the formula last used for the command (in this workspace), or
	 *         <code>null</code>
	 */
	public static String lastFormula(final IEventBRoot model,
			final Kind kind, final String command) {
		final IResource resource = model.getResource();
		try {
			return resource == null || !resource.exists() ? null : resource
					.getPersistentProperty(lastKey(kind, command));
		} catch (CoreException e) {
			return null;
		}
	}

	/** remembers the formula last used for the command (in this workspace) */
	public static void setLastFormula(final IEventBRoot model,
			final Kind kind, final String command, final String formula) {
		final IResource resource = model.getResource();
		// persistent properties are limited to 2 KB
		if (resource == null || !resource.exists() || formula.length() > 1000)
			return;
		try {
			resource.setPersistentProperty(lastKey(kind, command), formula);
		} catch (CoreException e) {
			Logger.info("Could not remember the formula for " + command
					+ ": " + e.getMessage());
		}
	}

	private static QualifiedName lastKey(final Kind kind, final String command) {
		return new QualifiedName(ProbUiPlugin.PLUGIN_ID, "lastFormula."
				+ kind.key + "." + command);
	}

	@SuppressWarnings("unchecked")
	private static List<Object> list(final Map<String, Object> settings,
			final Kind kind) {
		final Object list = settings.get(kind.key);
		if (list instanceof List)
			return (List<Object>) list;
		final List<Object> created = new ArrayList<Object>();
		settings.put(kind.key, created);
		return created;
	}

	/**
	 * @return the settings; empty if the file does not exist
	 * @throws CoreException
	 *             if it cannot be read or is not a JSON object; it must then
	 *             not be overwritten
	 */
	@SuppressWarnings("unchecked")
	private static synchronized Map<String, Object> read(
			final IEventBRoot model) throws CoreException {
		final IFile file = file(model);
		if (!file.exists()) {
			// e.g. copied there outside Rodin
			if (file.getLocation() == null
					|| !file.getLocation().toFile().exists())
				return new LinkedHashMap<String, Object>();
			try {
				file.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException e) {
				// read it below if possible
			}
			if (!file.exists())
				return new LinkedHashMap<String, Object>();
		}
		final String text;
		try (InputStream in = file.getContents(true)) {
			text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw error("Could not read " + file.getFullPath() + ": "
					+ e.getMessage(), e);
		}
		if (text.trim().isEmpty())
			return new LinkedHashMap<String, Object>();
		final Object parsed;
		try {
			parsed = Json.parse(text);
		} catch (IllegalArgumentException e) {
			throw error(file.getFullPath() + " is not valid JSON: "
					+ e.getMessage(), e);
		}
		if (!(parsed instanceof Map))
			throw error(file.getFullPath() + " does not contain a JSON object",
					null);
		return (Map<String, Object>) parsed;
	}

	private static synchronized void write(final IEventBRoot model,
			final Map<String, Object> settings) throws CoreException {
		final Map<String, Object> metadata = new LinkedHashMap<String, Object>();
		metadata.put("fileType", FILE_TYPE);
		metadata.put("formatVersion", Long.valueOf(1));
		metadata.put("model", model.getComponentName());
		// the metadata first
		final Map<String, Object> ordered = new LinkedHashMap<String, Object>();
		ordered.put("metadata", metadata);
		for (Map.Entry<String, Object> e : settings.entrySet())
			if (!"metadata".equals(e.getKey()))
				ordered.put(e.getKey(), e.getValue());
		final byte[] bytes = Json.write(ordered).getBytes(
				StandardCharsets.UTF_8);
		final IFile file = file(model);
		final IFolder folder = (IFolder) file.getParent();
		if (!folder.exists())
			folder.create(true, true, null);
		if (file.exists())
			file.setContents(new ByteArrayInputStream(bytes), true, true, null);
		else
			// no setCharset: changing the project's encoding settings makes
			// Rodin's builder rebuild (and prove) the whole project, as it
			// rebuilds its graph for a change of the project without changed
			// children (RodinBuilder's delta visitor). The file is read and
			// written as UTF-8 by this class anyway.
			file.create(new ByteArrayInputStream(bytes), true, null);
		for (Runnable listener : LISTENERS)
			listener.run();
	}

	private static CoreException error(final String message,
			final Throwable cause) {
		return new CoreException(new Status(IStatus.ERROR,
				ProbUiPlugin.PLUGIN_ID, message, cause));
	}
}
