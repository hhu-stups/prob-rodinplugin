/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.*;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.part.FileEditorInput;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;

public class ActionCollection {

	/**
	 * Provides an open action for the BMotion Studio Editor Project File
	 * 
	 * @param site
	 * @return An open action
	 */
	public static Action getOpenAction(final ICommonActionExtensionSite site) {

		final Action doubleClickAction = new Action("Open") {
			@Override
			public void run() {

				IRodinFile component;

				final ISelection selection = site.getStructuredViewer()
						.getSelection();
				final Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IBMotionSurfaceRoot) {

					component = (IRodinFile) ((IRodinElement) obj)
							.getOpenable();

					if (component == null) {
						return;
					}

					try {

						final IEditorDescriptor desc = PlatformUI
								.getWorkbench().getEditorRegistry()
								.getDefaultEditor(
										component.getCorrespondingResource()
												.getName());

						BMotionEditorPlugin.getActivePage().openEditor(
								new FileEditorInput(component.getResource()),
								desc.getId());

						// editor.getSite().getSelectionProvider().setSelection(
						// new StructuredSelection(obj));

					} catch (final PartInitException e) {
						final String errorMsg = "Error open Editor";
						MessageDialog.openError(null, null, errorMsg);
						BMotionEditorPlugin.getDefault().getLog().log(
								new Status(IStatus.ERROR,
										BMotionEditorPlugin.PLUGIN_ID,
										errorMsg, e));
					}

				}

			}
		};
		return doubleClickAction;

	}

	/**
	 * 
	 * @param site
	 * @return An action for deleting bmotion studio project files
	 */
	public static Action getDeleteAction(final ICommonActionExtensionSite site) {
		Action deleteAction = new Action() {
			@Override
			public void run() {
				if (!(site.getStructuredViewer().getSelection().isEmpty())) {

					Collection<IRodinElement> set = new ArrayList<IRodinElement>();

					IStructuredSelection ssel = (IStructuredSelection) site
							.getStructuredViewer().getSelection();

					for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
						final Object obj = it.next();
						if (!(obj instanceof IBMotionSurfaceRoot)) {
							continue;
						}
						IRodinElement elem = (IRodinElement) obj;
						if (elem.isRoot()) {
							elem = elem.getParent();
						}
						set.add(elem);
					}

					int answer = YesToAllMessageDialog.YES;
					for (IRodinElement element : set) {
						if (element instanceof IRodinFile) {
							if (answer != YesToAllMessageDialog.YES_TO_ALL) {
								answer = YesToAllMessageDialog
										.openYesNoToAllQuestion(
												site.getViewSite().getShell(),
												"Confirm File Delete",
												"Are you sure you want to delete file '"
														+ ((IRodinFile) element)
																.getElementName()
														+ "' in project '"
														+ element
																.getParent()
																.getElementName()
														+ "' ?");
							}

							if (answer == YesToAllMessageDialog.NO_TO_ALL
									|| answer == YesToAllMessageDialog.CANCEL)
								break;

							if (answer != YesToAllMessageDialog.NO) {
								try {
									closeOpenedEditor((IRodinFile) element);
									((IRodinFile) element).delete(true,
											new NullProgressMonitor());
								} catch (PartInitException e) {
									MessageDialog.openError(null, "Error",
											"Could not delete file");
								} catch (RodinDBException e) {
									MessageDialog.openError(null, "Error",
											"Could not delete file");
								}
							}
						}
					}

				}
			}
		};
		deleteAction.setText("&Delete");
		deleteAction.setToolTipText("Delete these elements");
		deleteAction.setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/delete_edit.gif"));

		return deleteAction;
	}

	static void closeOpenedEditor(IRodinFile file) throws PartInitException {
		IEditorReference[] editorReferences = BMotionEditorPlugin
				.getActivePage().getEditorReferences();
		for (int j = 0; j < editorReferences.length; j++) {
			IFile inputFile = (IFile) editorReferences[j].getEditorInput()
					.getAdapter(IFile.class);

			if (file.getResource().equals(inputFile)) {
				IEditorPart editor = editorReferences[j].getEditor(true);
				IWorkbenchPage page = BMotionEditorPlugin.getActivePage();
				page.closeEditor(editor, false);
			}
		}
	}

}
