package de.prob.ui.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;

import de.prob.ui.eventb.AnimationPreferencesDialog;

public class ShowProBConfigCommand extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		IEventBRoot selectedObject = null;

		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		ISelectionService selectionService = window.getSelectionService();

		ISelection selection = selectionService
				.getSelection("fr.systerel.explorer.navigator.view");

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object s = sel.getFirstElement();
			if (s instanceof IEventBRoot) {
				selectedObject = (IEventBRoot) s;
			}
		}

		AnimationPreferencesDialog.openAndAnimate(selectedObject);
		return null;
	}
}
