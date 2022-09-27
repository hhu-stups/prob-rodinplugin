/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import java.util.ArrayList;
import java.util.List;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverCSwitchCoordinates;
import de.bmotionstudio.gef.editor.util.BMSUtil;

public class SwitchChildCoordinates extends Observer {

	private List<ToggleObjectCoordinates> toggleObjects;

	public SwitchChildCoordinates() {
		toggleObjects = new ArrayList<ToggleObjectCoordinates>();
	}

	public void check(final Animation animation, final BControl control) {

		// Collect evaluate predicate objects in list
		for (ToggleObjectCoordinates obj : toggleObjects) {

			obj.setHasError(false);

			// First evaluate predicate (predicate field)
			String bolValue = "true";
			if (obj.getEval().length() > 0) {
				bolValue = BMSUtil.parsePredicate(obj.getEval(), control,
						animation);
			}

			if (!obj.hasError() && Boolean.valueOf(bolValue)) {

				// Handle control field
				BControl toggleControl = null;
				String parsedControl = BMSUtil.parseExpression(
						obj.getBcontrol(), control, animation, false);
				toggleControl = control.getChild(parsedControl);
				if (toggleControl == null) {
					obj.setHasError(true);
					// addError(control, animation, "No such control: "
					// + parsedControl);
				}

				Integer parsedX = 0;
				Integer parsedY = 0;
				// Handle X field
				try {
					parsedX = Integer.valueOf(BMSUtil.parseExpression(
							obj.getX(), control, animation));
				} catch (NumberFormatException n) {
					obj.setHasError(true);
					// addError(control, animation, "x is not a valid integer: "
					// + n.getMessage());
				}
				// Handle Y field
				try {
					parsedY = Integer.valueOf(BMSUtil.parseExpression(
							obj.getY(), control, animation));
				} catch (NumberFormatException n) {
					obj.setHasError(true);
					// addError(control, animation, "y is not a valid integer: "
					// + n.getMessage());
				}

				if (!obj.hasError()) {
					if (Boolean.valueOf(bolValue)) { // If true
						// if (obj.getAnimate()) {
						//
						// AnimationMove aMove = new AnimationMove(500, true,
						// toggleControl, parsedX, parsedY);
						// // aMove.addAnimationListener(animationListener);
						// aMove.start();
						//
						// } else {

						toggleControl.setAttributeValue(
								AttributeConstants.ATTRIBUTE_X, parsedX, true,
								false);
						toggleControl.setAttributeValue(
								AttributeConstants.ATTRIBUTE_Y, parsedY, true,
								false);

						// }
					}
				}

			}

		}

	}

	public ObserverWizard getWizard(final BControl bcontrol) {
		return new WizardObserverCSwitchCoordinates(bcontrol, this);
	}

	public List<ToggleObjectCoordinates> getToggleObjects() {
		return this.toggleObjects;
	}

	public void setToggleObjects(final List<ToggleObjectCoordinates> list) {
		this.toggleObjects = list;
	}

	public Observer clone() throws CloneNotSupportedException {
		SwitchChildCoordinates clonedObserver = (SwitchChildCoordinates) super
				.clone();
		List<ToggleObjectCoordinates> list = new ArrayList<ToggleObjectCoordinates>();
		for (ToggleObjectCoordinates obj : getToggleObjects()) {
			list.add(obj.clone());
		}
		clonedObserver.setToggleObjects(list);
		return clonedObserver;
	}

}
