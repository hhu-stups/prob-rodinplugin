package de.prob.ui.eventb;

import org.eclipse.core.commands.ExecutionEvent;
import org.eventb.core.IEventBRoot;

/**
 * Starts the animation of the machine or context of the active editor
 * (Camille or Rodin's editors), from their right-click menus and toolbars;
 * the selection is ignored (it may be text or belong to another view).
 */
public class StartEditorAnimationHandler extends StartAnimationHandler {

	@Override
	protected IEventBRoot findRootElement(final ExecutionEvent event) {
		return getEditorRootElement(event);
	}
}
