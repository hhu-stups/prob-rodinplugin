/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class ImageDialogCellEditor extends DialogCellEditor {

	/**
	 * Creates a new Image dialog cell editor parented under the given control.
	 * The cell editor value is <code>null</code> initially, and has no
	 * validator.
	 * 
	 * @param parent
	 *            the parent control
	 */
	protected ImageDialogCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(Control)
	 */
	@Override
	protected Object openDialogBox(Control arg) {
		ImageDialog dialog = new ImageDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), this);
		if (dialog.open() == Dialog.OK) {
			return getValue();
		}
		return null;
	}

}
