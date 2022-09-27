/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.property;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

public class FileDialogCellEditor extends DialogCellEditor {

	/**
	 * Creates a new File dialog cell editor parented under the given control.
	 * The cell editor value is <code>null</code> initially, and has no
	 * validator.
	 * 
	 * @param parent
	 *            the parent control
	 */
	protected FileDialogCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(Control)
	 */
	protected Object openDialogBox(Control cellEditorWindow) {
		FileDialog ftDialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());

		String value = (String) getValue();

		String fData = ftDialog.open();

		// if ((value != null) && (value.length() > 0)) {
		// ftDialog.setFontList(new FontData[] { new FontData(value) });
		// }
		// FontData fData = ftDialog.open();

		if (fData != null) {
			value = fData.toString();
		}

		return value;
	}

}
