/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.util.FileUtil;

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

		String imagePath = (pFile.getProject().getLocationURI() + "/images")
				.replace("file:", "");
		File imageFilePath = new File(imagePath);

		// Check if images folder exists!
		// If no such folder exists -> create
		if (!imageFilePath.exists()) {
			Boolean check = imageFilePath.mkdir();
			if (check) {
				// TODO: Do something with return value
			}
		}

		// Iterate the selected files
		for (String fileName : selectedFiles) {

			String filePath = (pFile.getProject().getLocation() + "/images/" + fileName)
					.replace("file:", "");

			// Copy files into project sub folder project/images
			File inputFile = new File(folderPath + File.separator + fileName);
			File outputFile = new File(filePath);

			boolean ok = false;

			if (outputFile.exists()) {
				// The file already exists; asks for confirmation
				MessageBox mb = new MessageBox(fd.getParent(), SWT.ICON_WARNING
						| SWT.YES | SWT.NO);
				mb.setMessage(fileName
						+ " already exists. Do you want to replace it?");
				// If they click Yes, we're done and we drop out. If
				// they click No, we redisplay the File Dialog
				ok = mb.open() == SWT.YES;
			} else {
				ok = true;
			}

			if (ok) {
				try {
					FileUtil.copyFile(inputFile, outputFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		getPage().refresh();

	}

}
