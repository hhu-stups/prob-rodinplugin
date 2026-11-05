package de.prob.ui.explorer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;

import de.prob.ui.historyview.SaveTraceHandler;

/**
 * Finds the trace files (.prob2trace) of a machine or context: those in the
 * model's own folder of its project, named like the model (e.g. M1/ for
 * M1.bum). The folder decides, not the model name stored in a trace, so a
 * trace of another model can be copied there to be replayed for this one.
 */
public final class TraceFiles {

	private static final Pattern MODEL_NAME = Pattern
			.compile("\"modelName\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

	/** model name of a trace file and the modification stamp it was read at */
	private static final class CachedName {
		final long stamp;
		final String modelName;

		CachedName(final long stamp, final String modelName) {
			this.stamp = stamp;
			this.modelName = modelName;
		}
	}

	private static final Map<IFile, CachedName> CACHE = new ConcurrentHashMap<IFile, CachedName>();

	private TraceFiles() {
	}

	/** @return the model's folder in its project, with its traces */
	public static IFolder folder(final IEventBRoot root) {
		return root.getRodinProject().getProject()
				.getFolder(root.getComponentName());
	}

	/** @return the trace files of the model, sorted by name and number */
	public static List<IFile> of(final IEventBRoot root) {
		final IFolder folder = folder(root);
		if (!folder.exists())
			return Collections.emptyList();
		final List<IFile> result = new ArrayList<IFile>();
		try {
			for (IResource member : folder.members())
				if (member instanceof IFile
						&& member.getName().endsWith(SaveTraceHandler.EXTENSION))
					result.add((IFile) member);
		} catch (CoreException e) {
			return Collections.emptyList();
		}
		Collections.sort(result, BY_NAME_AND_NUMBER);
		return result;
	}

	/** @return the file's name without the .prob2trace extension, e.g. M1_2 */
	public static String displayName(final IFile file) {
		final String name = file.getName();
		return name.endsWith(SaveTraceHandler.EXTENSION) ? name.substring(0,
				name.length() - SaveTraceHandler.EXTENSION.length()) : name;
	}

	/**
	 * @return the model name in the file's metadata, i.e. the model it was
	 *         recorded for, or <code>null</code>
	 */
	public static String modelName(final IFile file) {
		final long stamp = file.getModificationStamp();
		final CachedName cached = CACHE.get(file);
		if (cached != null && cached.stamp == stamp)
			return cached.modelName;
		String modelName = null;
		if (file.getLocation() != null) {
			try {
				final String text = new String(Files.readAllBytes(file
						.getLocation().toFile().toPath()),
						StandardCharsets.UTF_8);
				final Matcher m = MODEL_NAME.matcher(text);
				if (m.find())
					modelName = m.group(1).replace("\\\"", "\"")
							.replace("\\\\", "\\");
			} catch (IOException e) {
				// treated as a file without model name
			}
		}
		CACHE.put(file, new CachedName(stamp, modelName));
		return modelName;
	}

	/** M1_2 before M1_10 */
	private static final Comparator<IFile> BY_NAME_AND_NUMBER = new Comparator<IFile>() {
		private final Pattern numbered = Pattern.compile("(.*?)(\\d+)(\\D*)");

		@Override
		public int compare(final IFile a, final IFile b) {
			final Matcher ma = numbered.matcher(a.getName());
			final Matcher mb = numbered.matcher(b.getName());
			if (ma.matches() && mb.matches()
					&& ma.group(1).equals(mb.group(1))
					&& ma.group(2).length() <= 18
					&& mb.group(2).length() <= 18) {
				final int c = Long.compare(Long.parseLong(ma.group(2)),
						Long.parseLong(mb.group(2)));
				if (c != 0)
					return c;
			}
			return a.getName().compareTo(b.getName());
		}
	};
}
