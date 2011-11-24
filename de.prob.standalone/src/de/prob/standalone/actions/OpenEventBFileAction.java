package de.prob.standalone.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import de.prob.core.Animator;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class OpenEventBFileAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public OpenEventBFileAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(final IAction action) {
		String filename = getFileDialog();
		String projectName = getProjectName(filename);
		// IProject tempProject =
		createTempProject(projectName, filename);
		load(projectName, filename);
		// deleteTempProject(tempProject);
	}

	// private void deleteTempProject(final IProject tempProject) {
	// try {
	// tempProject.delete(true, null);
	// } catch (CoreException e) {
	// e.printStackTrace();
	// }
	// }

	private String getProjectName(final String filename) {
		File f = new File(filename);
		String projectName = f.getName() + "_tmp_" + System.currentTimeMillis();
		return projectName;
	}

	private String getFileName(final String filename) {
		File f = new File(filename);
		return f.getName();
	}

	private String getParent(final String filename) {
		File f = new File(filename);
		return f.getParent();
	}

	private void load(final String projectName, final String fileName) {
		IRodinDB rodinDB = RodinCore.getRodinDB();
		IRodinProject rodinProject = rodinDB.getRodinProject(projectName);
		IInternalElement root = rodinProject
				.getRodinFile(getFileName(fileName)).getRoot();
		if (root instanceof IEventBRoot) {
			IEventBRoot machineRoot = (IEventBRoot) root;
			try {
				LoadEventBModelCommand
						.load(Animator.getAnimator(), machineRoot);
			} catch (ProBException e) {
				e.printStackTrace();
			}
		}
	}

	private IProject createTempProject(final String projectName,
			final String filename) {

		IPath location = Platform.getLocation();
		String projectLocation = location.toOSString() + File.separator
				+ projectName;

		IRodinDB rodinDB = RodinCore.getRodinDB();
		IWorkspaceRoot workspaceRoot = rodinDB.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(projectName);
		try {
			project.create(null);
			String parent = getParent(filename);
			String[] filesToCopy = new File(parent).list();
			for (String string : filesToCopy) {
				File src = new File(parent + File.separator + string);
				File dst = new File(projectLocation + File.separator
						+ src.getName());
				copyFile(src, dst);
			}
			project.open(null);
			project.getDescription().setNatureIds(
					new String[] { RodinCore.NATURE_ID });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return project;
	}

	private String getFileDialog() {
		FileDialog fileDialog = new FileDialog(window.getShell());
		fileDialog.setFilterExtensions(new String[] { "bum" });
		String filename = fileDialog.open();
		return filename;
	}

	public void copyFile(final File in, final File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(final IAction action,
			final ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}
}