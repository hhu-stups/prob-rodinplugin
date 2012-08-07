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
import de.bmotionstudio.gef.editor.command.ObserverCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.prob.logging.Logger;

public class OpenObserverAction extends SelectionAction {

	private String className;
	private Observer clonedObserver;
	private Observer newObserver;
	private BControl actionControl;

	public OpenObserverAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {

		clonedObserver = null;

		actionControl = getControl();

		if (actionControl != null) {

			newObserver = getControl().getObserver(getClassName());

			// Add Observer
			if (newObserver == null) {

				try {
					newObserver = (Observer) BMotionEditorPlugin
							.getObserverExtension(getClassName())
							.createExecutableExtension("class");
				} catch (CoreException e) {
				}

			} else { // Edit Observer

				// Clone Observer
				try {
					clonedObserver = newObserver.clone();
				} catch (CloneNotSupportedException e) {
				}

			}

			ObserverWizard wizard = newObserver.getWizard(getControl());

			if (wizard != null) {

				BMotionObserverWizardDialog dialog = new BMotionObserverWizardDialog(
						getWorkbenchPart(), wizard);
				dialog.create();
				dialog.getShell().setSize(wizard.getSize());
				String title = "Observer: "
						+ newObserver.getName()
						+ " Control: "
						+ getControl().getAttributeValue(
								AttributeConstants.ATTRIBUTE_ID);
				wizard.setWindowTitle("BMotion Studio Observer Wizard");
				dialog.setTitle(title);
				dialog.setMessage(newObserver.getDescription());
				dialog.setTitleImage(BMotionStudioImage
						.getImage(BMotionStudioImage.IMG_LOGO_BMOTION64));
				int status = dialog.open();

				if (status == WizardDialog.OK) {

					ObserverCommand observerCommand = createObserverCommandCommand();
					observerCommand.setNewObserver(newObserver);

					if (wizard.isObserverDelete()) {

						RemoveObserverAction action = new RemoveObserverAction(
								getWorkbenchPart());
						action.setControl(getControl());
						action.setObserver(newObserver);
						action.run();

					} else {
						if (clonedObserver != null) {
							observerCommand.setClonedObserver(clonedObserver);
						}
						execute(observerCommand);
					}

				} else if (status == WizardDialog.CANCEL) {

					if (clonedObserver != null)
						actionControl.addObserver(clonedObserver);

				} else if (status == BMotionObserverWizardDialog.DELETE) {
					RemoveObserverAction action = new RemoveObserverAction(
							getWorkbenchPart());
					action.setControl(getControl());
					action.setObserver(newObserver);
					action.run();
				}

			} else {
				Logger.notifyUserWithoutBugreport("The Observer \""
						+ newObserver.getName()
						+ "\" does not support a wizard.");
			}
		}

	}

	public ObserverCommand createObserverCommandCommand() {
		ObserverCommand command = new ObserverCommand();
		command.setClassName(getClassName());
		command.setControl(actionControl);
		return command;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	protected BControl getControl() {

		List<?> objects = getSelectedObjects();

		if (objects.isEmpty())
			return null;

		if ((objects.get(0) instanceof EditPart)) {
			EditPart part = (EditPart) objects.get(0);
			return (BControl) part.getModel();
		}
		return null;

	}

}
