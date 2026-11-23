/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.stateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.ui.stateview.statetree.StateTreeExpression;

/**
 * Removes a formula the user added to the State view ("Add new Formula"),
 * also from the model's settings file, so it is not added again when the
 * model is animated again.
 */
public class RemoveFormulaHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// the right-clicked row; the workbench selection may lag behind
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null)
			selection = HandlerUtil.getCurrentSelection(event);
		final IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (!(selection instanceof IStructuredSelection)
				|| !(part instanceof StateViewPart))
			return null;
		for (Object element : ((IStructuredSelection) selection).toList())
			if (element instanceof StateTreeExpression)
				((StateViewPart) part)
						.removeUserFormula(((StateTreeExpression) element)
								.getStaticElement());
		return null;
	}
}
