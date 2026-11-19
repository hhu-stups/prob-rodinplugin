package de.prob.ui.explorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/**
 * Makes the Event-B Explorer's "open" action, run by a double-click (or
 * Enter), replay the selected trace
 */
public class TraceActionProvider extends CommonActionProvider {

	private ICommonActionExtensionSite site;
	private Action replay;

	@Override
	public void init(final ICommonActionExtensionSite site) {
		super.init(site);
		this.site = site;
		replay = new Action("Replay Trace with ProB") {
			@Override
			public void run() {
				final TraceNode trace = selectedTrace();
				if (trace != null)
					ReplayExplorerTraceHandler.replay(site.getViewSite()
							.getShell(), trace);
			}
		};
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		if (selectedTrace() != null)
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
					replay);
	}

	private TraceNode selectedTrace() {
		final ISelection selection = site.getStructuredViewer().getSelection();
		if (!(selection instanceof IStructuredSelection)
				|| ((IStructuredSelection) selection).size() != 1)
			return null;
		final Object element = ((IStructuredSelection) selection)
				.getFirstElement();
		return element instanceof TraceNode ? (TraceNode) element : null;
	}
}
