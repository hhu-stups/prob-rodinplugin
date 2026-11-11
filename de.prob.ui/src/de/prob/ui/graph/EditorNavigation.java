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
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

import de.prob.logging.Logger;
import de.prob.ui.ProbUiPlugin;

/**
 * Selects an event, variable, constant or carrier set in an editor of its
 * machine or context, whichever editor is the default one: in a text editor
 * (e.g. Camille) by searching its declaration, e.g. "event NAME" or NAME after
 * "variables", otherwise (e.g. in Rodin's Event-B editor) by going to a marker
 * of the element, as for a problem in the Problems view.
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

	/**
	 * selects a variable of a machine, or a constant or carrier set of a
	 * context
	 */
	static void selectIdentifier(final IEditorPart editor,
			final IEventBRoot model, final String name) {
		final IIdentifierElement element = findIdentifier(model, name);
		if (element == null)
			return;
		final String keyword = element instanceof IVariable ? "variables"
				: element instanceof IConstant ? "constants" : "sets";
		if (selectInText(editor, keyword, name))
			return;
		gotoElement(editor, element);
	}

	private static IIdentifierElement findIdentifier(final IEventBRoot model,
			final String name) {
		try {
			final IIdentifierElement[][] candidates = model instanceof IMachineRoot ? new IIdentifierElement[][] { ((IMachineRoot) model)
					.getVariables() }
					: model instanceof IContextRoot ? new IIdentifierElement[][] {
							((IContextRoot) model).getCarrierSets(),
							((IContextRoot) model).getConstants() }
							: new IIdentifierElement[0][];
			for (IIdentifierElement[] elements : candidates)
				for (IIdentifierElement element : elements)
					if (element.hasIdentifierString()
							&& name.equals(element.getIdentifierString()))
						return element;
		} catch (RodinDBException e) {
			// not selected then
		}
		return null;
	}

	/**
	 * selects the first occurrence of the name after the keyword that starts
	 * its section, e.g. "variables" in Camille
	 *
	 * @return whether it was found and selected
	 */
	private static boolean selectInText(final IEditorPart editor,
			final String keyword, final String name) {
		final IFindReplaceTarget target = editor
				.getAdapter(IFindReplaceTarget.class);
		if (!(target instanceof IFindReplaceTargetExtension3))
			return false;
		final IFindReplaceTargetExtension3 find = (IFindReplaceTargetExtension3) target;
		try {
			final int section = find.findAndSelect(0, "(?<![\\w'])"
					+ keyword + "(?![\\w'])", true, true, false, true);
			if (section < 0)
				return false;
			return find.findAndSelect(section + keyword.length(),
					"(?<![\\w'])" + Pattern.quote(name) + "(?![\\w'])",
					true, true, false, true) >= 0;
		} catch (RuntimeException e) {
			return false; // e.g. the editor's text is not ready yet
		}
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
			final IInternalElement event) {
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
