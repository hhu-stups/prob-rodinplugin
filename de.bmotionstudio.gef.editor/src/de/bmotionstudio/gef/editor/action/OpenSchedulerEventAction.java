/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.command.SchedulerEventCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;
import de.bmotionstudio.gef.editor.scheduler.SchedulerWizard;
import de.prob.logging.Logger;

public class OpenSchedulerEventAction extends SelectionAction {

	private String className;
	private String eventID;
	private SchedulerEvent clonedSchedulerEvent;

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
		clonedSchedulerEvent = null;

		if (bcontrol != null) {

			SchedulerEvent newSchedulerEvent = bcontrol.getEvent(getEventID());

			// Add Scheduler Event
			if (newSchedulerEvent == null) {

				try {
					newSchedulerEvent = (SchedulerEvent) BMotionEditorPlugin
							.getSchedulerExtension(getClassName())
							.createExecutableExtension("class");
				} catch (CoreException e) {
				}

			} else { // Edit Scheduler Event

				// Clone Scheduler Event
				try {
					clonedSchedulerEvent = newSchedulerEvent.clone();
				} catch (CloneNotSupportedException e) {
				}

			}

			if (newSchedulerEvent != null) {

				newSchedulerEvent.setEventID(getEventID());
				SchedulerWizard wizard = newSchedulerEvent.getWizard(bcontrol);

				if (wizard != null) {

					BMotionSchedulerEventWizardDialog dialog = new BMotionSchedulerEventWizardDialog(
							getWorkbenchPart(), wizard);
					dialog.create();
					dialog.getShell().setSize(wizard.getSize());
					String title = "Scheduler Event: "
							+ newSchedulerEvent.getName()
							+ " Control: "
							+ bcontrol
									.getAttributeValue(AttributeConstants.ATTRIBUTE_ID);
					wizard.setWindowTitle("BMotion Studio Scheduler Event Wizard");
					dialog.setTitle(title);
					dialog.setMessage(newSchedulerEvent.getDescription());
					dialog.setTitleImage(BMotionStudioImage
							.getImage(BMotionStudioImage.IMG_LOGO_BMOTION64));
					int status = dialog.open();

					if (status == WizardDialog.OK) {

						SchedulerEventCommand schedulerEventCommand = createSchedulerEventCommand();
						schedulerEventCommand
								.setNewSchedulerEvent(newSchedulerEvent);

						if (wizard.isEventDelete()) {

							RemoveSchedulerEventAction action = new RemoveSchedulerEventAction(
									getWorkbenchPart());
							action.setControl(getControl());
							action.setSchedulerEvent(clonedSchedulerEvent);
							action.run();

						} else {
							if (clonedSchedulerEvent != null) {
								schedulerEventCommand
										.setClonedSchedulerEvent(clonedSchedulerEvent);
							}
							execute(schedulerEventCommand);
						}

					} else if (status == WizardDialog.CANCEL) {
						if (clonedSchedulerEvent != null)
							bcontrol.addEvent(getEventID(),
									clonedSchedulerEvent);
					}

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

	public SchedulerEventCommand createSchedulerEventCommand() {
		SchedulerEventCommand command = new SchedulerEventCommand();
		command.setClassName(getClassName());
		command.setEventID(getEventID());
		command.setControl(getControl());
		return command;
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
