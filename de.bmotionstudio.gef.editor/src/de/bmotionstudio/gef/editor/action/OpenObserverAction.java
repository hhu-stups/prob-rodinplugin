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

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.command.RemoveObserverCommand;
import de.bmotionstudio.gef.editor.command.SetObserverCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.prob.logging.Logger;

public class OpenObserverAction extends SelectionAction {

	private String className;

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

		BControl actionControl = getControl();

		if (actionControl != null) {

			Observer oldObserver = null;
			Observer observer = getControl().getObserver(getClassName());

			// If an observer does not exist, add one
			if (observer == null) {

				try {
					observer = (Observer) BMotionEditorPlugin
							.getObserverExtension(getClassName())
							.createExecutableExtension("class");
				} catch (CoreException e) {
				}

			} else { // else edit the current observer

				// therefore, clone the current observer, if the user aborts
				// editing the current observer
				try {
					oldObserver = observer.clone();
				} catch (CloneNotSupportedException e) {
				}

			}

			ObserverWizard wizard = observer.getWizard(actionControl);

			if (wizard != null) {

				BMotionObserverWizardDialog dialog = new BMotionObserverWizardDialog(
						getWorkbenchPart(), wizard);
				dialog.create();
				dialog.getShell().setSize(wizard.getSize());
				String title = "Observer: " + observer.getName()
						+ " Control: " + getControl().getID();
				wizard.setWindowTitle("BMotion Studio Observer Wizard");
				dialog.setTitle(title);
				dialog.setMessage(observer.getDescription());
				dialog.setTitleImage(BMotionStudioImage
						.getImage(BMotionStudioImage.IMG_LOGO_BMOTION64));
				int status = dialog.open();

				// The user clicked on the "OK" button in order to confirm his
				// changes on the observer
				if (status == WizardDialog.OK) {

					// If the observer delete flag is set to true, delete the
					// observer anyway
					if (wizard.isObserverDelete()) {
						RemoveObserverCommand cmd = createRemoveObserverCommand(
								observer, actionControl);
						execute(cmd);
					} else {
						SetObserverCommand cmd = createObserverSetCommand(
								actionControl, observer, oldObserver);
						execute(cmd);
					}

					// else the user canceled his changes on the observer
				} else if (status == WizardDialog.CANCEL) {

					// Reset observer without using a command!
					if (oldObserver != null)
						actionControl.getObservers().put(oldObserver.getID(),
								oldObserver);

					// else the user clicked on the delete button in order to
					// delete the observer
				} else if (status == BMotionObserverWizardDialog.DELETE) {
					RemoveObserverCommand cmd = createRemoveObserverCommand(
							observer, actionControl);
					execute(cmd);
				}

			} else {
				Logger.notifyUser("The Observer \""
						+ observer.getName()
						+ "\" does not support a wizard.");
			}
		}

	}

	private RemoveObserverCommand createRemoveObserverCommand(
			Observer observer, BControl control) {
		RemoveObserverCommand cmd = new RemoveObserverCommand();
		cmd.setControl(control);
		cmd.setObserver(observer);
		return cmd;
	}

	public SetObserverCommand createObserverSetCommand(BControl control,
			Observer newObserver, Observer oldObserver) {
		SetObserverCommand cmd = new SetObserverCommand();
		cmd.setNewObserver(newObserver);
		cmd.setOldObserver(oldObserver);
		cmd.setControl(control);
		return cmd;
	}

	public SetObserverCommand createObserverSetCommand(BControl control,
			Observer newObserver) {
		return createObserverSetCommand(control, newObserver, null);
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
			BControl control = null;
			if (part.getModel() instanceof BControl)
				control = (BControl) part.getModel();
			return control;
		}

		return null;

	}

}
