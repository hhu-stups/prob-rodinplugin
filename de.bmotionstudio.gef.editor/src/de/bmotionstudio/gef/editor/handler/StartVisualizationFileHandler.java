/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioEditor;
import de.bmotionstudio.gef.editor.internal.VisualizationProgressBar;
import de.prob.core.Animator;
import de.prob.logging.Logger;

public class StartVisualizationFileHandler extends AbstractHandler implements
		IHandler {

	private ISelection fSelection;
	private VisualizationProgressBar dpb;
	private BMotionStudioEditor activeEditor;

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		BMotionStudioEditor editor = BMotionEditorPlugin.getActiveEditor();
		if (editor != null) {
			if (BMotionEditorPlugin.getActiveEditor().isDirty()) {
				if (MessageDialog
						.openConfirm(
								Display.getDefault().getActiveShell(),
								"Please confirm",
								"You made changes in your editor. Do you want to safe before starting visualization?")) {

					BMotionEditorPlugin.getActiveEditor().doSave(
							new NullProgressMonitor());
				}
			}
		}

		IFile f = null;

		// Get the Selection
		if (fSelection instanceof IStructuredSelection) {

			IStructuredSelection ssel = (IStructuredSelection) fSelection;

			if (ssel.size() == 1) {

				f = getBmsFileFromSelection(ssel);

				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();

				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor(f.getName());

				try {
					IEditorPart part = page.openEditor(new FileEditorInput(f),
							desc.getId());
					if (part instanceof BMotionStudioEditor) {
						activeEditor = (BMotionStudioEditor) part;
					} else {
						// TODO: Return some useful error?!
						return null;
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}

				// Get ProB Animator
				Animator animator = Animator.getAnimator();

				// Open Run Perspective
				IWorkbench workbench = PlatformUI.getWorkbench();
				try {
					workbench.showPerspective(
							"de.bmotionstudio.perspective.run",
							workbench.getActiveWorkbenchWindow());
				} catch (WorkbenchException e) {
					Logger.notifyUser(
							"Error opening BMotion Studio Run perspective.", e);
				}

				// First, kill old visualization (only if exists)
				if (dpb != null)
					dpb.kill();
				// Create a new visualization
				dpb = new VisualizationProgressBar(Display.getDefault()
						.getActiveShell(), animator, activeEditor, f);
				dpb.initGuage();
				dpb.open();

			}

		}

		return null;

	}

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		fSelection = selection;
	}

	protected IFile getBmsFileFromSelection(IStructuredSelection ssel) {
		if (ssel.getFirstElement() instanceof IFile)
			return (IFile) ssel.getFirstElement();
		return null;
	}

}
