package de.prob.ui.stateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.ui.graph.GraphViewPart;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * Shows a graph for the selected variable or formula of the state view in the
 * ProB Graph view: its value as a graph, or the state space projected onto
 * it (see the subclasses).
 */
public abstract class ShowGraphHandler extends AbstractHandler {

	private final String graphCommand;

	/** @param graphCommand ProB's command for the graph */
	protected ShowGraphHandler(final String graphCommand) {
		this.graphCommand = graphCommand;
	}

	/** the value of the selection as a graph */
	public static final class Value extends ShowGraphHandler {
		public Value() {
			super(GraphViewPart.EXPRESSION_GRAPH);
		}
	}

	/** the state space projected onto the selection */
	public static final class Projection extends ShowGraphHandler {
		public Projection() {
			super(GraphViewPart.EXPRESSION_PROJECTION);
		}
	}

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
		final String formula = ((StaticStateElement) element).getLabel();
		if (formula == null)
			return null;

		final IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		try {
			final GraphViewPart view = (GraphViewPart) window.getActivePage()
					.showView(GraphViewPart.VIEW_ID);
			view.showFormulaGraph(graphCommand, formula);
		} catch (PartInitException e) {
			throw new ExecutionException("Could not open the ProB Graph view",
					e);
		}
		return null;
	}
}
