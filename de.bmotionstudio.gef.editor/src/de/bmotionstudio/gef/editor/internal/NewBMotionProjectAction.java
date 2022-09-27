/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.rodinp.core.IRodinProject;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;

@Deprecated
public class NewBMotionProjectAction implements IObjectActionDelegate {

	// private IWorkbenchPart targetPart;
	private IStructuredSelection currentSelection;
	private IRodinProject rodinProject;

	public NewBMotionProjectAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(final IAction action,
			final IWorkbenchPart targetPart) {
		// this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		if (rodinProject == null) {
			return;
		}

		NewBMotionProjectWizard wizard = new NewBMotionProjectWizard();
		wizard.init(BMotionEditorPlugin.getDefault().getWorkbench(),
				currentSelection);
		WizardDialog dialog = new WizardDialog(BMotionEditorPlugin
				.getActiveEditor().getSite().getShell(), wizard);
		dialog.create();
		dialog.open();

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			currentSelection = ssel;
			if (ssel.size() == 1) {
				Object firstElement = ssel.getFirstElement();
				if (firstElement instanceof IRodinProject) {
					rodinProject = (IRodinProject) firstElement;
					return;
				}
			}
		}
		rodinProject = null;

	}

}
