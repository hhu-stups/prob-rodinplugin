/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

public class BMotionObserverWizardDialog extends BMotionAbstractWizardDialog {

	public BMotionObserverWizardDialog(IWorkbenchPart workbenchPart, IWizard newWizard) {
		super(workbenchPart, newWizard);
		setShellStyle(SWT.CLOSE);
		setDeleteToolTip("Delete Observer");
	}

	@Override
	protected void deletePressed() {

		if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				"Do you really want to delete this Observer?",
				"Do you really want to delete this Observer?")) {
			setReturnCode(DELETE);
			close();
		}

	}

}
