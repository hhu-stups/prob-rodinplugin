package de.prob.ui.ltl.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import de.prob.ui.ltl.CounterExampleViewPart;

public class CounterExamplePrintHandler extends AbstractHandler implements
		IHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final CounterExampleViewPart counterExampleView = CounterExampleViewPart
				.getDefault();
		if (counterExampleView != null) {
			counterExampleView.printCounterExample();
		}
		return null;
	}
}
