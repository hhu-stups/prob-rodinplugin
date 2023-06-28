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

import de.bmotionstudio.gef.editor.scheduler.SchedulerWizard;

public class BMotionSchedulerEventWizardDialog extends BMotionAbstractWizardDialog {

	public BMotionSchedulerEventWizardDialog(IWorkbenchPart workbenchPart,
			IWizard newWizard) {
		super(workbenchPart, newWizard);
		setShellStyle(SWT.CLOSE);
		setDeleteToolTip("Delete Event");
	}

	@Override
	protected void deletePressed() {

		if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				"Do you really want to delete this Event?",
				"Do you really want to delete this Event?")) {
			RemoveSchedulerEventAction action = new RemoveSchedulerEventAction(
					getWorkbenchPart());
			action.setControl(((SchedulerWizard) getWizard()).getBControl());
			action.setSchedulerEvent(((SchedulerWizard) getWizard())
					.getScheduler());
			action.run();
			setReturnCode(DELETE);
			close();
		}

	}
}
