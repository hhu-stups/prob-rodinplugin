/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.prob.logging.Logger;

public class AnimationRefinementAction implements IObjectActionDelegate {

	private ISelection selection;
	private IWorkbenchPart part;

	public AnimationRefinementAction() {
	}

	public void setActivePart(final IAction action,
			final IWorkbenchPart targetPart) {
		this.part = targetPart;

	}

	public void run(final IAction action) {
		// Get the Selection
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		final IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() != 1) {
			return;
		}
		final Object element = ssel.getFirstElement();

		IMachineRoot abs = null;

		if ((element instanceof IMachineRoot)) {
			abs = (IMachineRoot) element;
		}
		if ((element instanceof IContextRoot)) {
			// FIXME could be done for contexts as well
			// we start with models
			// abs = (IMachineRoot) element;
		}

		if (abs == null) {
			return;
		}

		String basename = askForBaseName(abs.getRodinFile());

		IContextRoot ctx = createContext(abs, basename);
		createRefinement(abs, basename, ctx);
		openWizard();
	}

	private IMachineRoot getMachineFileRoot(final String name,
			final IRodinProject prj) {
		final String fileName = EventBPlugin.getMachineFileName(name);
		return (IMachineRoot) prj.getRodinFile(fileName).getRoot();
	}

	private IContextRoot createContext(final IMachineRoot abs,
			final String basename) {
		final String fileName = EventBPlugin.getContextFileName(basename
				+ "_ctx");
		IRodinFile file = abs.getRodinProject().getRodinFile(fileName);
		try {
			file.create(true, null);
			IContextRoot root = (IContextRoot) file.getRoot();
			if (!root.exists()) {
				root.create(null, null);
			}
			return root;
		} catch (RodinDBException e1) {
			String message = "Internal Error \n" + e1.getLocalizedMessage();
			Logger.notifyUser(message, e1);
			e1.printStackTrace();
		}
		return null;
	}

	private void createRefinement(final IMachineRoot abs, final String name,
			final IContextRoot ctx) {
		IMachineRoot conc = getMachineFileRoot(name + "_mch",
				abs.getRodinProject());
		CreateRefinement op = new CreateRefinement(abs, conc, ctx);
		try {
			RodinCore.run(op, null);
		} catch (RodinDBException e) {
			// TODO report error to end user
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Asks the user the name of the concrete machine to create and returns it.
	 * 
	 * @param abs
	 *            the abstract machine to refine
	 * @return the concrete machine entered by the user or <code>null</code> if
	 *         canceled.
	 */
	private String askForBaseName(final IRodinFile abs) {
		final InputDialog dialog = new InputDialog(
				part.getSite().getShell(),
				"New REFINES Clause",
				"Please enter the name of the new animation model (the tool will automatically append _mch resp. _ctx)",
				abs.getBareName() + "_ani1", new FileInputValidator(abs
						.getRodinProject()));
		dialog.open();

		final String name = dialog.getValue();
		return name;
	}

	private void openWizard() {

	}

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		this.selection = selection;

	}

}
