/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.edit;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Lukas Ladenberger
 * 
 */
public class PopupCellEditor extends TextCellEditor {

	private Shell parentShell;
	private TextEditorWindow dialog;
	// private int test = 0;
	private boolean isOpen = false;
	private int counter = 0;

	public PopupCellEditor(Composite parent, Shell parentShell) {
		super(parent);
		this.parentShell = parentShell;
		text.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent evt) {
			}

			@Override
			public void focusGained(FocusEvent evt) {
				if (!isOpen && counter == 0) {
					counter = counter + 1;
					openDialogBox();
				} else if (counter == 1) {
					counter = 0;
				}
			}
		});
	}

	protected void openDialogBox() {
		dialog = new TextEditorWindow(this.parentShell, text);
		dialog.addPopupListener(new IPopupListener() {
			@Override
			public void popupOpened() {
				isOpen = true;
			}

			@Override
			public void popupClosed() {
				isOpen = false;
			}
		});
		PopupResult result = dialog.openPopup();
		if (result.getReturncode() == Window.OK) {
			setValue(result.getValue());
		} else if (result.getReturncode() == Window.CANCEL) {
		}
	}

	@Override
	protected void focusLost() {
		if (!isOpen)
			super.focusLost();
	}

	@Override
	protected boolean dependsOnExternalFocusListener() {
		return false;
	}

}
