package de.prob.ui.explorer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.rodinp.core.RodinDBException;

import de.prob.logging.Logger;
import de.prob.ui.ProbUiPlugin;

/**
 * Whether a trace file was replayed successfully, stored as persistent
 * properties of the file (in the workspace's metadata, not in the file).
 * Stored with it is a fingerprint of the model: the content hashes of the
 * machine or context and of what it depends on (abstract machines, seen and
 * extended contexts). When the model has changed since, the status is
 * {@link Status#UNKNOWN}.
 */
public final class TraceStatus {

	/** the outcome of the last replay */
	public enum Status {
		/** all steps replayed exactly */
		PERFECT,
		/** all steps replayed, but not exactly (e.g. other parameters) */
		IMPERFECT,
		/** not all steps could be replayed, or ProB failed */
		FAILED,
		/** not replayed, or the model has changed since */
		UNKNOWN
	}

	private static final QualifiedName STATUS = name("replayStatus");
	private static final QualifiedName FINGERPRINT = name("replayFingerprint");
	private static final QualifiedName DETAIL = name("replayDetail");
	private static final QualifiedName[] KEYS = { STATUS, FINGERPRINT, DETAIL };

	/** content hash of a file and the modification stamp it was computed at */
	private static final class CachedHash {
		final long stamp;
		final String hash;

		CachedHash(final long stamp, final String hash) {
			this.stamp = stamp;
			this.hash = hash;
		}
	}

	private static final Map<IFile, CachedHash> HASHES = new ConcurrentHashMap<IFile, CachedHash>();

	/** told when a status was stored, e.g. to update the Explorer */
	private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<Runnable>();

	private TraceStatus() {
	}

	private static QualifiedName name(final String localName) {
		return new QualifiedName(ProbUiPlugin.PLUGIN_ID, localName);
	}

	/**
	 * Stores the outcome of a replay of a trace file in the workspace, for
	 * the current version of the model. Nothing is stored for files outside
	 * the workspace.
	 *
	 * @param detail
	 *            e.g. "12 of 12 steps"; the date is added
	 */
	public static void store(final java.io.File traceFile,
			final IEventBRoot root, final Status status, final String detail) {
		final IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocationURI(traceFile.toURI());
		for (IFile file : files)
			store(file, root, status, detail);
	}

	public static void store(final IFile file, final IEventBRoot root,
			final Status status, final String detail) {
		if (!file.exists())
			return;
		try {
			file.setPersistentProperty(STATUS, status.name());
			file.setPersistentProperty(FINGERPRINT, fingerprint(root));
			file.setPersistentProperty(DETAIL, detail + ", "
					+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
							DateFormat.SHORT).format(new Date()));
		} catch (CoreException e) {
			Logger.info("Could not store the replay status of "
					+ file.getFullPath() + ": " + e.getMessage());
		}
		// persistent properties are not reported as resource changes
		for (Runnable listener : LISTENERS)
			listener.run();
	}

	public static void addListener(final Runnable listener) {
		LISTENERS.add(listener);
	}

	public static void removeListener(final Runnable listener) {
		LISTENERS.remove(listener);
	}

	/**
	 * @return the status of the last replay of the trace for the model, or
	 *         {@link Status#UNKNOWN} if the model has changed since
	 */
	public static Status get(final IFile file, final IEventBRoot root) {
		try {
			final String status = file.getPersistentProperty(STATUS);
			if (status == null)
				return Status.UNKNOWN;
			final String stored = file.getPersistentProperty(FINGERPRINT);
			if (stored == null || !stored.equals(fingerprint(root)))
				return Status.UNKNOWN;
			return Status.valueOf(status);
		} catch (CoreException e) {
			return Status.UNKNOWN;
		} catch (IllegalArgumentException e) {
			return Status.UNKNOWN; // an unknown status name
		}
	}

	/** @return e.g. "12 of 12 steps, 5 Nov 2026, 14:32", or null */
	public static String getDetail(final IFile file) {
		try {
			return file.getPersistentProperty(DETAIL);
		} catch (CoreException e) {
			return null;
		}
	}

	/**
	 * @return the stored status of a trace, e.g. to keep it when renaming
	 *         the trace (see {@link #restoreIfMissing})
	 */
	public static Map<QualifiedName, String> saved(final IFile file) {
		final Map<QualifiedName, String> saved = new HashMap<QualifiedName, String>();
		try {
			for (QualifiedName key : KEYS) {
				final String value = file.getPersistentProperty(key);
				if (value != null)
					saved.put(key, value);
			}
		} catch (CoreException e) {
			// nothing to keep
		}
		return saved;
	}

	/**
	 * sets the status saved before a rename, unless it was moved with the
	 * file
	 */
	public static void restoreIfMissing(final IFile file,
			final Map<QualifiedName, String> saved) {
		try {
			if (saved.isEmpty() || file.getPersistentProperty(STATUS) != null)
				return;
			for (Map.Entry<QualifiedName, String> entry : saved.entrySet())
				file.setPersistentProperty(entry.getKey(), entry.getValue());
		} catch (CoreException e) {
			// the status is unknown then
		}
	}

	/**
	 * @return a hash over the contents of the model's file and of the files
	 *         of the models it depends on
	 */
	static String fingerprint(final IEventBRoot root) {
		final List<IEventBRoot> models = new ArrayList<IEventBRoot>(
				dependencies(root));
		Collections.sort(models, new Comparator<IEventBRoot>() {
			@Override
			public int compare(final IEventBRoot a, final IEventBRoot b) {
				return a.getPath().toString().compareTo(b.getPath().toString());
			}
		});
		final StringBuilder all = new StringBuilder();
		for (IEventBRoot model : models) {
			final IResource resource = model.getRodinFile().getResource();
			all.append(model.getPath()).append('=');
			all.append(resource instanceof IFile && resource.exists() ? hash((IFile) resource)
					: "missing");
			all.append('\n');
		}
		return sha256(all.toString().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * @return the model and, transitively, the machines it refines and the
	 *         contexts it sees or extends
	 */
	private static Set<IEventBRoot> dependencies(final IEventBRoot root) {
		final Set<IEventBRoot> result = new LinkedHashSet<IEventBRoot>();
		final List<IEventBRoot> todo = new ArrayList<IEventBRoot>();
		todo.add(root);
		while (!todo.isEmpty()) {
			final IEventBRoot model = todo.remove(todo.size() - 1);
			if (!result.add(model) || !model.exists())
				continue;
			try {
				if (model instanceof IMachineRoot) {
					final IMachineRoot machine = (IMachineRoot) model;
					for (IRefinesMachine refines : machine.getRefinesClauses())
						if (refines.hasAbstractMachineName())
							todo.add(refines.getAbstractMachineRoot());
					for (ISeesContext sees : machine.getSeesClauses())
						if (sees.hasSeenContextName())
							todo.add(sees.getSeenContextRoot());
				} else if (model instanceof IContextRoot) {
					for (IExtendsContext ext : ((IContextRoot) model)
							.getExtendsClauses())
						if (ext.hasAbstractContextName())
							todo.add(ext.getAbstractContextRoot());
				}
			} catch (RodinDBException e) {
				// counted with the files found so far
			}
		}
		return result;
	}

	private static String hash(final IFile file) {
		final long stamp = file.getModificationStamp();
		final CachedHash cached = HASHES.get(file);
		if (cached != null && cached.stamp == stamp)
			return cached.hash;
		String hash;
		try {
			hash = file.getLocation() == null ? "missing" : sha256(Files
					.readAllBytes(file.getLocation().toFile().toPath()));
		} catch (IOException e) {
			hash = "unreadable";
		}
		HASHES.put(file, new CachedHash(stamp, hash));
		return hash;
	}

	private static String sha256(final byte[] bytes) {
		try {
			final byte[] digest = MessageDigest.getInstance("SHA-256").digest(
					bytes);
			final StringBuilder hex = new StringBuilder();
			for (byte b : digest)
				hex.append(String.format("%02x", b));
			return hex.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e); // always available
		}
	}
}
