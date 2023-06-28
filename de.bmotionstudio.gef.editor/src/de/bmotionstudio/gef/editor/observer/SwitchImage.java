/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverSwitchImage;
import de.bmotionstudio.gef.editor.util.BMSUtil;

public class SwitchImage extends Observer {

	private List<ToggleObjectImage> toggleObjects;

	public SwitchImage() {
		toggleObjects = new ArrayList<ToggleObjectImage>();
	}

	public void check(final Animation animation, final BControl control) {

		boolean set = false;

		// Collect evaluate predicate objects in list
		for (ToggleObjectImage obj : toggleObjects) {

			obj.setHasError(false);

			// First evaluate predicate (predicate field)
			String bolValue = "true";
			if (obj.getEval().length() > 0) {
				bolValue = BMSUtil.parsePredicate(obj.getEval(), control,
						animation);
			}

			if (!obj.hasError() && Boolean.valueOf(bolValue)) {

				String fImage = obj.getImage();

				if (obj.isExpressionMode()) { // Expression mode
					fImage = BMSUtil.parseExpression(obj.getImage(), control,
							animation);
				}

				IFile pFile = control.getVisualization().getProjectFile();
				String myPath = (pFile.getProject().getLocation() + "/images/" + fImage)
						.replace("file:", "");
				if (!new File(myPath).exists()) {
					// addError(control, animation,
					// "No such image in your library: " + fImage);
				}

				if (!obj.hasError()) {
					if (!control.getAttributeValue(
							AttributeConstants.ATTRIBUTE_IMAGE).equals(fImage)) {
						control.setAttributeValue(
								AttributeConstants.ATTRIBUTE_IMAGE, fImage,
								true, false);
					}
				}

				set = true;

			}

		}

		if (!set)
			control.restoreDefaultValue(AttributeConstants.ATTRIBUTE_IMAGE);

	}

	public ObserverWizard getWizard(final BControl bcontrol) {
		return new WizardObserverSwitchImage(bcontrol, this);
	}

	public List<ToggleObjectImage> getToggleObjects() {
		return this.toggleObjects;
	}

	public void setToggleObjects(final List<ToggleObjectImage> list) {
		this.toggleObjects = list;
	}

	public Observer clone() throws CloneNotSupportedException {
		SwitchImage clonedObserver = (SwitchImage) super.clone();
		List<ToggleObjectImage> list = new ArrayList<ToggleObjectImage>();
		for (ToggleObjectImage obj : getToggleObjects()) {
			list.add(obj.clone());
		}
		clonedObserver.setToggleObjects(list);
		return clonedObserver;
	}

}
