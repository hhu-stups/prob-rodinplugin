package de.prob.ui.eventb;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;

import de.prob.ui.PerspectiveFactory;

public class CreateConfigurationHandler extends AbstractHandler implements
		IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		ISelection fSelection = HandlerUtil.getCurrentSelection(event);
		if (!(fSelection instanceof IStructuredSelection))
			return null;
		final IStructuredSelection ssel = (IStructuredSelection) fSelection;
		if (ssel.size() != 1)
			return null;
		final Object element = ssel.getFirstElement();
		if (!(element instanceof IEventBRoot))
			return null;

		PerspectiveFactory.openPerspective();

		AnimationPreferencesDialog.openAndAnimate((IEventBRoot) element);

		return null;
	}
}
