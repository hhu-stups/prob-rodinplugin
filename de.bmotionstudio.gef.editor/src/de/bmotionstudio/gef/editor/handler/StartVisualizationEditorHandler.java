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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.internal.VisualizationProgressBar;
import de.prob.core.Animator;
import de.prob.logging.Logger;

public class StartVisualizationEditorHandler extends AbstractHandler implements
		IHandler {

	private VisualizationProgressBar dpb;

	public Object execute(final ExecutionEvent event) throws ExecutionException {

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

		IFile projectFile = BMotionEditorPlugin.getActiveEditor()
				.getVisualization().getProjectFile();

		// Get ProB Animator
		Animator animator = Animator.getAnimator();

		// Open Run Perspective
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective("de.bmotionstudio.perspective.run",
					workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			Logger.notifyUser("Error opening BMotion Studio Run perspective.",
					e);
		}

		// First, kill old visualization (only if exists)
		if (dpb != null)
			dpb.kill();
		// Create a new visualization
		dpb = new VisualizationProgressBar(Display.getDefault()
				.getActiveShell(), animator,
				BMotionEditorPlugin.getActiveEditor(), projectFile);
		dpb.initGuage();
		dpb.open();

		return null;

	}

}
