/**
 * 
 */
package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.ui.ProbUiPlugin;
import de.prob.ui.historyview.HistoryView;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * This handler copies the selected entries in the state view to the history
 * view.
 * 
 * @author plagge
 */
public class ShowInHistoryHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection sel = (IStructuredSelection) selection;
			final Collection<StaticStateElement> sses = new ArrayList<StaticStateElement>();
			for (final Object element : sel.toList()) {
				if (element instanceof StaticStateElement) {
					sses.add((StaticStateElement) element);
				}
			}
			if (!sses.isEmpty()) {
				addToHistory(sses);
			}
		}
		return null;
	}

	private void addToHistory(final Collection<StaticStateElement> sses) {
		final IWorkbenchWindow window = ProbUiPlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		final IViewPart view = findHistoryView(window);
		if (view != null) {
			HistoryView hview = (HistoryView) view;
			hview.addColumns(sses);
		}
	}

	private HistoryView findHistoryView(final IWorkbenchWindow window) {
		final IWorkbenchPage page = window.getActivePage();
		IViewPart view = page.findView(HistoryView.VIEW_ID);
		if (view == null) {
			try {
				view = page.showView(HistoryView.VIEW_ID);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Internal Error",
						"Error while opening the History View");
			}
		}
		if (view == null) {
			MessageDialog.openError(window.getShell(), "Internal Error",
					"Cannot not find the History View");
			return null;
		} else {
			if (view instanceof HistoryView)
				return (HistoryView) view;
			else {
				MessageDialog.openError(window.getShell(), "Internal Error",
						"Not expected type of the History View: "
								+ view.getClass().getName());
				return null;
			}
		}
	}
}
