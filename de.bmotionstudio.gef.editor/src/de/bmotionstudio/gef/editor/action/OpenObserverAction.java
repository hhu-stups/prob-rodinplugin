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

			Observer observer = getControl().getObserver(getClassName());

			ObserverWizard wizard = observer.getWizard(Display.getDefault()
					.getActiveShell(), actionControl);

			if (wizard != null) {
				wizard.create();
				wizard.getShell().setSize(wizard.getSize());
				String title = "Observer: " + observer.getName() + " Control: "
						+ getControl().getID();
				wizard.getShell().setText(title);
				// wizard.setWindowTitle("BMotion Studio Observer Wizard");
				// wizard.setTitle(title);
				// wizard.setMessage(observer.getDescription());
				// wizard.setTitleImage(BMotionStudioImage
				// .getImage(BMotionStudioImage.IMG_LOGO_BMOTION64));
				wizard.open();
			} else {
				Logger.notifyUserWithoutBugreport("The Observer \""
						+ observer.getName()
						+ "\" does not support a wizard.");
			}
		}

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
