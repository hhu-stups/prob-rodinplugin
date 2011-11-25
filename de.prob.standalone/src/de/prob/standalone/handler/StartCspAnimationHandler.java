package de.prob.standalone.handler;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.command.LoadClassicalBModelCommand;
import de.prob.core.command.LoadCspModelCommand;
import de.prob.exceptions.ProBException;

public class StartCspAnimationHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection fSelection = HandlerUtil.getCurrentSelection(event);

		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				 if (element instanceof IFile) {
					IFile f = (IFile) element;
					String n = f.getName();
					String name = n.substring(0,n.length()-4);
					File file = new File(f.getLocationURI());
					try {
						LoadCspModelCommand.load(Animator.getAnimator(), file, name);
					} catch (ProBException e) {
						throw new ExecutionException(e.getLocalizedMessage(),e);
					}
				}
			}
		}
		return null;
	}

	
	
	
}
