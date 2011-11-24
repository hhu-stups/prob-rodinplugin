/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.internal.Animation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverClone;

public class CloneObserver extends Observer {

	private transient Collection<BControl> clonedControls;
	private List<ObserverCloneObject> observerCloneObjects;
	private boolean newControls = false;
	private int oldInt = 0;

	public CloneObserver() {
		observerCloneObjects = new ArrayList<ObserverCloneObject>();
	}

	@Override
	public void check(Animation animation, final BControl control) {

		for (ObserverCloneObject obj : observerCloneObjects) {

			final BControl toCloneControl = animation.getVisualization()
					.getBControl(obj.getControlId());
			String evalString = obj.getEval();

			String fEval = parseExpression(evalString, control, animation, obj);

			if (toCloneControl == null) {
				addError(control, animation,
						"No control found with id: " + obj.getControlId());
			} else {

				int clones = 0;

				try {
					clones = Integer.parseInt(fEval);
				} catch (NumberFormatException e) {
					addError(control, animation, "The expression: " + fEval
							+ " should return an integer value!");
				}

				if (oldInt == clones) {

					newControls = false;

				} else {

					for (BControl c : getClonedControls()) {
						control.removeChild(c);
					}

					getClonedControls().clear();

					for (int i = obj.getCounter(); i < clones
							+ obj.getCounter(); i++) {
						try {
							BControl clonedControl = toCloneControl.clone();
							clonedControl.setAttributeValue(
									AttributeConstants.ATTRIBUTE_CUSTOM, i);
							getClonedControls().add(clonedControl);
						} catch (CloneNotSupportedException e) {
						}
					}

					oldInt = clones;
					newControls = true;

				}

			}

		}

	}

	@Override
	public ObserverWizard getWizard(BControl control) {
		return new WizardObserverClone(control, this);
	}

	public List<ObserverCloneObject> getObserverCloneObjects() {
		return observerCloneObjects;
	}

	public void setObserverCloneObjects(
			List<ObserverCloneObject> observerCloneObjects) {
		this.observerCloneObjects = observerCloneObjects;
	}

	public Observer clone() throws CloneNotSupportedException {
		CloneObserver clonedObserver = (CloneObserver) super.clone();
		List<ObserverCloneObject> list = new ArrayList<ObserverCloneObject>();
		for (ObserverCloneObject obj : getObserverCloneObjects()) {
			list.add(obj.clone());
		}
		clonedObserver.setObserverCloneObjects(list);
		return clonedObserver;
	}

	@Override
	public void afterCheck(final Animation animation, final BControl control) {
		if (newControls) {
			synchronized (control) {
				for (BControl c : clonedControls) {
					control.addChild(c);
					c.checkObserver(animation);
				}
			}
		}
	}

	public Collection<BControl> getClonedControls() {
		if (clonedControls == null)
			clonedControls = new ArrayList<BControl>();
		return clonedControls;
	}

}
