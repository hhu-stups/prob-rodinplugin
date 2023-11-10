/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import de.bmotionstudio.gef.editor.BMotionStudioImage;

public class ImportImagesAction extends AbstractLibraryAction {

	public ImportImagesAction(LibraryPage page) {
		super(page);
		setText("Import images...");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/import_wiz.gif"));
	}

	@Override
	public void run() {

		// The file dialog
		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(),
				SWT.OPEN | SWT.MULTI);
		fd.setText("Open");
		fd.setFilterPath("C:/");
		String[] filterExt = { "*.jpg", "*.gif", "*.png", "*.*" };
		fd.setFilterExtensions(filterExt);

		fd.open();

		// File path
		String folderPath = fd.getFilterPath();

		// Selected items
		String[] selectedFiles = fd.getFileNames();

		// The project file
		IFile pFile = getPage().getEditor().getVisualization().getProjectFile();

		try {

			IProject project = pFile.getProject();
			IFolder folder = project.getFolder("images");
			NullProgressMonitor monitor = new NullProgressMonitor();

			if (!folder.exists())
				folder.create(true, true, monitor);

			// Iterate the selected files
			for (String fileName : selectedFiles) {

				File inputFile = new File(folderPath + File.separator
						+ fileName);
				IFile newFile = folder.getFile(fileName);
				FileInputStream fileInputStream = new FileInputStream(inputFile);
				
				if (!newFile.exists()) {
					newFile.create(fileInputStream, true,
							monitor);
				} else {
					// The file already exists; asks for confirmation
					MessageBox mb = new MessageBox(fd.getParent(),
							SWT.ICON_WARNING | SWT.YES | SWT.NO);
					mb.setMessage(fileName
							+ " already exists. Do you want to replace it?");
					// If they click Yes, we're done and we drop out. If
					// they click No, we redisplay the File Dialog
					if (mb.open() == SWT.YES)
						newFile.setContents(fileInputStream, true, false,
								monitor);
				}

			}

		} catch (CoreException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		getPage().refresh();

	}

}
