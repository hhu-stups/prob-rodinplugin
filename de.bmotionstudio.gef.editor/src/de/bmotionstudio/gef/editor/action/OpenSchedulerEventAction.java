/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;
import de.bmotionstudio.gef.editor.scheduler.SchedulerWizard;
import de.prob.logging.Logger;

public class OpenSchedulerEventAction extends SelectionAction {

	private String className;
	private String eventID;

	public OpenSchedulerEventAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	protected void init() {
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	public void run() {

		BControl bcontrol = getControl();

		if (bcontrol != null) {

			SchedulerEvent newSchedulerEvent = bcontrol.getEvent(getEventID());

			if (newSchedulerEvent != null) {

				newSchedulerEvent.setEventID(getEventID());
				SchedulerWizard wizard = newSchedulerEvent.getWizard(Display
						.getDefault().getActiveShell(), bcontrol);

				if (wizard != null) {

					wizard.create();
					wizard.getShell().setSize(wizard.getSize());
					String title = "Scheduler Event: "
							+ newSchedulerEvent.getName()
							+ " Control: "
							+ bcontrol
									.getAttributeValue(AttributeConstants.ATTRIBUTE_ID);
					wizard.getShell().setText(title);
					// wizard.setWindowTitle("BMotion Studio Scheduler Event Wizard");
					// wizard.setTitle(title);
					// wizard.setMessage(newSchedulerEvent.getDescription());
					// wizard.setTitleImage(BMotionStudioImage
					// .getImage(BMotionStudioImage.IMG_LOGO_BMOTION64));
					wizard.open();

				} else {
					Logger.notifyUserWithoutBugreport("The Scheduler Event \""
							+ newSchedulerEvent.getName()
							+ "\" does not support a wizard.");
				}

			} else {
				// TODO: Error message?!
			}

		}

	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return this.className;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String getEventID() {
		return eventID;
	}

	protected BControl getControl() {
		List<?> objects = getSelectedObjects();
		if (objects.isEmpty())
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;
		EditPart part = (EditPart) objects.get(0);
		return (BControl) part.getModel();
	}

}
