/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

import de.prob.core.command.GetUnsatPropertiesPositionsCommand;
import de.prob.core.command.GetUnsatPropertiesPositionsCommand.Position;
import de.prob.core.internal.Activator;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * Tells the user that ProB could not find valid constants or initial values:
 * in one non-modal notice, which also takes over ProB's warnings about the
 * axioms reported by the same call (e.g. "AXIOMS are unsatisfiable (but all
 * CONSTANTS valued)"), and for constants with problem markers on the axioms
 * concerned.
 */
public final class UnsatAxiomsNotice {

	/**
	 * The markers are Rodin problem markers, not of a subtype: Camille only
	 * places markers of exactly this type at their element in the text.
	 */
	private static final String MARKER_TYPE = RodinMarkerUtil.RODIN_PROBLEM_MARKER;

	/** set to true on ProB's markers, to tell them from Rodin's */
	private static final String PROB_MARKER_ATTRIBUTE = Activator.PLUGIN_ID
			+ ".unsatAxiom";

	/** a notice reported while the result of a call is processed */
	private static final class Pending {
		final String headline;
		/** or null */
		final String description;
		/** if it is about the constants, i.e. the axioms */
		final boolean axioms;

		Pending(final String headline, final String description,
				final boolean axioms) {
			this.headline = headline;
			this.description = description;
			this.axioms = axioms;
		}
	}

	/** the notice reported while the result of the thread's call is processed */
	private static final ThreadLocal<Pending> PENDING = new ThreadLocal<Pending>();

	/** makes the marker jobs run one after the other */
	private static final ISchedulingRule MUTEX = new ISchedulingRule() {
		@Override
		public boolean isConflicting(final ISchedulingRule rule) {
			return rule == this;
		}

		@Override
		public boolean contains(final ISchedulingRule rule) {
			return rule == this;
		}
	};

	private UnsatAxiomsNotice() {
	}

	/**
	 * Called while the result of a command is processed: ProB could not find
	 * valid constants, see the description. The notice is shown when the
	 * animator has the call's messages, see {@link #handlePending}.
	 */
	public static void report(final String description) {
		report(new Pending(
				"ProB could not find valid constants which satisfy all axioms.",
				description, true));
	}

	/**
	 * Like {@link #report(String)}, but without a description of the axioms
	 * concerned.
	 */
	public static void reportNoConstants(final String message) {
		report(new Pending(message, null, true));
	}

	/** ProB could not find valid initial values for the variables */
	public static void reportNoInitialisation(final String message) {
		report(new Pending(message, null, false));
	}

	private static void report(final Pending pending) {
		if (PENDING.get() == null) // the first one of a call, e.g. a trace
			PENDING.set(pending);
	}

	/** called by the animator before a call */
	public static void discardPending() {
		PENDING.remove();
	}

	/**
	 * Called by the animator after a call: shows the notice reported during
	 * the call, if any, and marks the axioms concerned.
	 *
	 * @param messages
	 *            ProB's messages of the call (or <code>null</code>)
	 * @return the messages still to be shown to the user, i.e. without the
	 *         warnings about the axioms which the notice shows
	 */
	public static List<String> handlePending(final List<String> messages,
			final boolean onlyWarnings, final boolean quiet) {
		final Pending pending = PENDING.get();
		PENDING.remove();
		if (pending == null)
			return messages;
		if (!pending.axioms) {
			// nothing to mark, and ProB's messages are not about it
			if (quiet)
				Logger.info(pending.headline);
			else
				Logger.notifyUserNonModal(pending.headline, null);
			return messages;
		}
		final List<String> taken = new ArrayList<String>();
		List<String> rest = messages;
		if (!quiet && messages != null) {
			if (onlyWarnings) {
				rest = new ArrayList<String>();
				for (final String message : messages) {
					(isAboutAxioms(message) ? taken : rest).add(message);
				}
			} else if (messages.stream().allMatch(
					UnsatAxiomsNotice::isAboutAxioms)) {
				// e.g. "AXIOMS are unsatisfiable (but some CONSTANTS valued,
				// e.g., z)", an error for ProB: the notice says it all, and
				// the model is loaded (without valid constants)
				taken.addAll(messages);
				rest = Collections.emptyList();
			}
			// otherwise there are other errors, which make the call fail: all
			// are left alone, as we do not know which ones are errors
		}
		final Job job = new MarkerJob(pending, taken, !quiet);
		job.schedule();
		return rest;
	}

	/** removes the markers, e.g. when another model is animated */
	public static void clearMarkers() {
		final Job job = new MarkerJob(null, Collections.<String> emptyList(),
				false);
		job.schedule();
	}

	/** e.g. "AXIOMS are unsatisfiable (but all CONSTANTS valued)" */
	private static boolean isAboutAxioms(final String message) {
		return message.contains("CONSTANTS valued");
	}

	private static final class MarkerJob extends Job {
		/** null if the markers are only to be removed */
		private final Pending pending;
		private final List<String> warnings;
		private final boolean notify;

		MarkerJob(final Pending pending, final List<String> warnings,
				final boolean notify) {
			super("Marking the axioms ProB could not satisfy");
			this.pending = pending;
			this.warnings = warnings;
			this.notify = notify;
			setSystem(true);
			setRule(MUTEX);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final List<String> marked = new ArrayList<String>();
			try {
				final IWorkspace workspace = ResourcesPlugin.getWorkspace();
				deleteMarkers(workspace);
				if (pending != null) {
					final List<Position> positions = positions();
					workspace.run(m -> mark(positions, marked), null,
							IWorkspace.AVOID_UPDATE, monitor);
				}
			} catch (CoreException e) {
				Logger.info("Could not update the markers of the axioms: "
						+ e.getMessage());
			}
			if (notify)
				Logger.notifyUserNonModal(message(marked), details());
			else if (pending != null)
				Logger.info(pending.description == null ? pending.headline
						: pending.headline + "\n" + pending.description);
			return Status.OK_STATUS;
		}

		/** deletes ProB's markers, but not Rodin's */
		private static void deleteMarkers(final IWorkspace workspace)
				throws CoreException {
			final IMarker[] markers = workspace.getRoot().findMarkers(
					MARKER_TYPE, false, IResource.DEPTH_INFINITE);
			final List<IMarker> own = new ArrayList<IMarker>();
			for (final IMarker marker : markers) {
				if (marker.getAttribute(PROB_MARKER_ATTRIBUTE, false))
					own.add(marker);
			}
			if (!own.isEmpty())
				workspace.deleteMarkers(own.toArray(new IMarker[own.size()]));
		}

		private static List<Position> positions() {
			final GetUnsatPropertiesPositionsCommand command = new GetUnsatPropertiesPositionsCommand();
			try {
				Animator.getAnimator().executeQuietly(command);
				return command.getPositions();
			} catch (ProBException e) {
				// e.g. a probcli without this command
				Logger.info("Could not get the axioms ProB could not satisfy: "
						+ e.getMessage());
				return Collections.emptyList();
			}
		}

		private static void mark(final List<Position> positions,
				final List<String> marked) throws CoreException {
			final LanguageDependendAnimationPart part = Animator.getAnimator()
					.getLanguageDependendPart();
			if (!(part instanceof EventBAnimatorPart))
				return;
			final IEventBRoot model = ((EventBAnimatorPart) part).getRoot();
			if (model == null || !model.exists())
				return;
			for (final Position position : positions) {
				final IAxiom axiom = findAxiom(model, position);
				if (axiom == null)
					continue;
				final IMarker marker = axiom.getResource().createMarker(
						MARKER_TYPE);
				marker.setAttributes(
						new String[] { IMarker.MESSAGE, IMarker.SEVERITY,
								IMarker.LOCATION, RodinMarkerUtil.ELEMENT,
								RodinMarkerUtil.ATTRIBUTE_ID,
								PROB_MARKER_ATTRIBUTE,
								// not saved when Rodin is closed
								IMarker.TRANSIENT },
						new Object[] {
								markerMessage(position),
								position.isFalse ? IMarker.SEVERITY_ERROR
										: IMarker.SEVERITY_WARNING,
								position.label,
								axiom.getHandleIdentifier(),
								EventBAttributes.PREDICATE_ATTRIBUTE.getId(),
								Boolean.TRUE, Boolean.TRUE });
				marked.add(position.label);
			}
		}

		private static IAxiom findAxiom(final IEventBRoot model,
				final Position position) throws RodinDBException {
			final IRodinFile file = model.getRodinProject().getRodinFile(
					EventBPlugin.getContextFileName(position.component));
			if (file == null || !file.exists()
					|| !(file.getRoot() instanceof IContextRoot))
				return null;
			for (final IAxiom axiom : ((IContextRoot) file.getRoot())
					.getAxioms()) {
				if (axiom.hasLabel()
						&& position.label.equals(axiom.getLabel()))
					return axiom;
			}
			return null;
		}

		private static String markerMessage(final Position position) {
			if (position.together)
				return position.isFalse ? "ProB: no valid constants satisfy this axiom together with the other axioms on the same constants"
						: "ProB could not find valid constants for this axiom together with the other axioms on the same constants";
			return position.isFalse ? "ProB: this axiom prevented finding valid constants (it is false)"
					: "ProB: this axiom prevented finding valid constants (its value is unknown)";
		}

		private String message(final List<String> marked) {
			final StringBuilder sb = new StringBuilder(pending.headline);
			for (final String warning : warnings) {
				sb.append('\n').append(firstLine(warning));
			}
			if (!marked.isEmpty()) {
				sb.append("\nMarked as problems: ")
						.append(String.join(", ", marked)).append('.');
			}
			return sb.toString();
		}

		/** @return null if there are none */
		private String details() {
			final StringBuilder sb = new StringBuilder();
			if (pending.description != null)
				sb.append(pending.description.trim());
			if (!warnings.isEmpty()) {
				if (sb.length() > 0)
					sb.append("\n\n");
				sb.append("ProB reported:\n").append(
						String.join("\n", warnings).trim());
			}
			return sb.length() == 0 ? null : sb.toString();
		}

		private static String firstLine(final String text) {
			final String trimmed = text.trim();
			final int end = trimmed.indexOf('\n');
			return end < 0 ? trimmed : trimmed.substring(0, end);
		}
	}
}
