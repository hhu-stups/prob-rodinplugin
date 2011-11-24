/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.internal.BMotionFileInputValidator;

/**
 * @author Lukas Ladenberger
 * 
 */
public class RenameFileHandler extends AbstractHandler implements IHandler {

	private ISelection fSelection;
	private IWorkbenchPart part;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);
		part = HandlerUtil.getActivePart(event);

		if (fSelection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) fSelection;

			if (ssel.size() == 1) {

				Object obj = ssel.getFirstElement();

				if (obj instanceof IBMotionSurfaceRoot) {

					final IBMotionSurfaceRoot root = (IBMotionSurfaceRoot) obj;
					if (root.getParent() instanceof IRodinFile) {

						final IRodinFile file = root.getRodinFile();
						final IRodinProject prj = file.getRodinProject();

						InputDialog dialog = new InputDialog(part.getSite()
								.getShell(), "Rename BMotion Studio Project",
								"Please enter the new name for the Project",
								getDefaultName(root),
								new BMotionFileInputValidator(prj));

						dialog.open();

						final String bareName = dialog.getValue();

						if (dialog.getReturnCode() == InputDialog.CANCEL)
							return null; // Cancel

						assert bareName != null;

						try {
							RodinCore.run(new IWorkspaceRunnable() {

								public void run(IProgressMonitor monitor)
										throws RodinDBException {
									String newName = bareName
											+ "."
											+ BMotionEditorPlugin.FILEEXT_STUDIO;
									if (newName != null)
										file.rename(newName, false, monitor);
								}

							}, null);
						} catch (RodinDBException e) {
							e.printStackTrace();
						}

					}
				}

			}
		}

		return null;

	}

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		fSelection = selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	private String getDefaultName(IBMotionSurfaceRoot root) {
		return root.getResource().getName().replace(".bmso", "");
	}

}
