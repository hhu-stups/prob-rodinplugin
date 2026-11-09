package de.prob.ui.graph;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IGotoMarker;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

import de.prob.logging.Logger;
import de.prob.ui.ProbUiPlugin;

/**
 * Selects an event in an editor of its machine, whichever editor is the
 * default one: in a text editor (e.g. Camille) by searching its declaration
 * "event NAME", otherwise (e.g. in Rodin's Event-B editor) by going to a
 * marker of the event, as for a problem in the Problems view.
 */
final class EditorNavigation {

	/**
	 * a non-persistent subtype of Rodin's problem markers (only those are
	 * understood by Rodin's editor), created only to go to an element and
	 * deleted at once
	 */
	private static final String GOTO_MARKER = ProbUiPlugin.PLUGIN_ID
			+ ".gotoElement";

	private EditorNavigation() {
	}

	static void selectEvent(final IEditorPart editor,
			final IMachineRoot machine, final String eventName) {
		if (selectInText(editor, eventName))
			return;
		final IEvent event = findEvent(machine, eventName);
		if (event != null)
			gotoElement(editor, event);
	}

	/** @return whether the event's declaration was found and selected */
	private static boolean selectInText(final IEditorPart editor,
			final String eventName) {
		final IFindReplaceTarget target = editor
				.getAdapter(IFindReplaceTarget.class);
		if (!(target instanceof IFindReplaceTargetExtension3))
			return false;
		// e.g. "event inc" or "event inc refines inc" in Camille
		final String regex = "\\bevent\\s+" + Pattern.quote(eventName)
				+ "(?![\\w'])";
		try {
			return ((IFindReplaceTargetExtension3) target).findAndSelect(0,
					regex, true, true, false, true) >= 0;
		} catch (RuntimeException e) {
			return false; // e.g. the editor's text is not ready yet
		}
	}

	private static IEvent findEvent(final IMachineRoot machine,
			final String eventName) {
		try {
			for (IEvent event : machine.getEvents())
				if (eventName.equals(event.getLabel()))
					return event;
		} catch (RodinDBException e) {
			// not selected then
		}
		return null;
	}

	private static void gotoElement(final IEditorPart editor,
			final IEvent event) {
		final IGotoMarker gotoMarker = editor instanceof IGotoMarker ? (IGotoMarker) editor
				: editor.getAdapter(IGotoMarker.class);
		final IResource resource = event.getRodinFile().getResource();
		if (gotoMarker == null || !(resource instanceof IFile))
			return;
		IMarker marker = null;
		try {
			marker = resource.createMarker(GOTO_MARKER);
			marker.setAttribute(RodinMarkerUtil.ELEMENT,
					event.getHandleIdentifier());
			gotoMarker.gotoMarker(marker);
		} catch (CoreException e) {
			Logger.info("Could not select the event " + event.getElementName()
					+ ": " + e.getMessage());
		} catch (RuntimeException e) {
			// e.g. the editor does not understand the marker: only opened
		} finally {
			if (marker != null)
				try {
					marker.delete();
				} catch (CoreException e) {
					// not persistent, gone at the next start anyway
				}
		}
	}
}
