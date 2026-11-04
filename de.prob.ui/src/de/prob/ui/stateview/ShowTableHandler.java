package de.prob.ui.stateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.ui.stateview.statetree.StaticStateElement;
import de.prob.ui.table.TableViewPart;

/**
 * Shows the value of the selected variable or formula of the state view as a
 * table in the ProB Table view.
 */
public class ShowTableHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// the selection of the tree the context menu belongs to; the
		// workbench selection may not yet reflect the right-clicked row
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null)
			selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		final Object element = ((IStructuredSelection) selection)
				.getFirstElement();
		if (!(element instanceof StaticStateElement))
			return null;
		final String expression = ((StaticStateElement) element).getLabel();
		if (expression == null)
			return null;

		final IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		try {
			final TableViewPart view = (TableViewPart) window.getActivePage()
					.showView(TableViewPart.VIEW_ID);
			view.showExpressionTable(expression);
		} catch (PartInitException e) {
			throw new ExecutionException("Could not open the ProB Table view",
					e);
		}
		return null;
	}
}
