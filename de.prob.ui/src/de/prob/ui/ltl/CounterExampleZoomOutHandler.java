package de.prob.ui.ltl;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class CounterExampleZoomOutHandler extends AbstractHandler implements
		IHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		final CounterExampleViewPart counterExampleView = (CounterExampleViewPart) workbenchPage
				.findView(CounterExampleViewPart.ID);

		if (counterExampleView != null) {
			counterExampleView.zoomOutCounterExample();
		}

		return null;
	}
}
