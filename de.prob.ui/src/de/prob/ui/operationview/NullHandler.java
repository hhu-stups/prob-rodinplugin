package de.prob.ui.operationview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class NullHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		return null;
	}

}
