/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.rodinp.core.RodinCore;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;

public class NewBMotionProjectWizard extends Wizard implements INewWizard {

	// The wizard page.
	private NewBMotionProjectWizardPage page;

	// The selection when the wizard is launched.
	private IStructuredSelection selection;

	public NewBMotionProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		page = new NewBMotionProjectWizardPage(selection);
		addPage(page);
	}

	public void init(final IWorkbench workbench, final IStructuredSelection sel) {
		this.selection = sel;
	}

	@Override
	public boolean performFinish() {

		// New project/file name
		final String projectName = page.getFileName();

		// Selected rodin project root
		final String projectRoot = page.getProjectRoot();

		final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(
							projectRoot,
							projectName,
							page.getProject(),
							page.getInitialContents(BMotionEditorPlugin.FILEEXT_STUDIO),
							monitor);
				} catch (final CoreException e) {
					Logger.getAnonymousLogger().log(Level.SEVERE,
							"CoreException", e);
				} catch (UnsupportedEncodingException e) {
					Logger.getAnonymousLogger().log(Level.SEVERE,
							"CoreException", e);
				} finally {
					monitor.done();
				}
			}

		};
		try {
			getContainer().run(true, false, op);
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}

		return true;
	}

	private void doFinish(String projectRoot, final String projectName,
			final IProject project, final InputStream defaultContentStudio,
			final IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Creating " + projectName
				+ " BMotion Studio Visualization", 2);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(projectRoot));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throw new CoreException(new Status(IStatus.ERROR,
					"org.eventb.internal.ui", IStatus.OK, "Project \""
							+ projectRoot + "\" does not exist.", null));
		}

		RodinCore.run(new IWorkspaceRunnable() {

			public void run(final IProgressMonitor pMonitor)
					throws CoreException {

				// Create .bmso file
				IFile file = project.getFile(projectName + "."
						+ BMotionEditorPlugin.FILEEXT_STUDIO);

				file.create(defaultContentStudio, false, monitor);
				file.refreshLocal(IResource.DEPTH_ZERO, null);

			}

		}, monitor);

		monitor.worked(1);

	}

}
