/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverSetAttribute;
import de.bmotionstudio.gef.editor.util.BMSUtil;

public class SetAttribute extends Observer {

	private List<SetAttributeObject> setAttributeObjects;
	private transient List<String> setAttributes;

	public SetAttribute() {
		super();
		setAttributeObjects = new ArrayList<SetAttributeObject>();
		setAttributes = new ArrayList<String>();
	}

	protected Object readResolve() {
		setAttributes = new ArrayList<String>();
		return super.readResolve();
	}

	@Override
	public void check(Animation animation, BControl control) {
		
		this.setAttributes.clear();

		// Collect evaluate predicate objects in list
		for (SetAttributeObject obj : setAttributeObjects) {

			obj.setHasError(false);

			// First evaluate predicate (predicate field)
			String bolValue = "true";
			if (obj.getEval().length() > 0) {
				bolValue = BMSUtil.parsePredicate(obj.getEval(), control,
						animation);
			}

			if (!obj.hasError() && Boolean.valueOf(bolValue)) {

				String attributeID = obj.getAttribute();

				AbstractAttribute attributeObj = control
						.getAttribute(attributeID);

				Object attributeVal = obj.getValue();

				if (obj.isExpressionMode()) {
					String strAtrVal = BMSUtil.parseExpression(
							attributeVal.toString(), control, animation);
					String er = attributeObj.validateValue(strAtrVal, null);
					if (er != null) {
						// addError(
						// control,
						// animation,
						// "You selected "
						// + attributeObj.getName()
						// +
						// " as attribute. There is a problem with your value: "
						// + strAtrVal + " - Reason: " + er);
						obj.setHasError(true);
					} else {
						attributeVal = attributeObj.unmarshal(strAtrVal);
					}
				}

				if (!obj.hasError()) {
					Object oldAttrVal = control.getAttributeValue(attributeID);
					if (!oldAttrVal.equals(attributeVal)) {
						control.setAttributeValue(attributeID, attributeVal,
								true, false);
					}
				}

				setAttributes.add(attributeID);

			}

		}

		// Restore attribute values
		for (SetAttributeObject obj : setAttributeObjects) {
			if (!setAttributes.contains(obj.getAttribute())) {
				AbstractAttribute attributeObj = control.getAttribute(obj
						.getAttribute());
				Object oldAttrVal = control.getAttributeValue(obj
						.getAttribute());
				if (!oldAttrVal.equals(attributeObj.getInitValue())) {
					control.restoreDefaultValue(attributeObj.getID());
				}
			}
		}

	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new WizardObserverSetAttribute(shell, control, this);
	}

	public void setSetAttributeObjects(
			List<SetAttributeObject> setAttributeObjects) {
		this.setAttributeObjects = setAttributeObjects;
	}

	public List<SetAttributeObject> getSetAttributeObjects() {
		return setAttributeObjects;
	}

	public Observer clone() throws CloneNotSupportedException {
		SetAttribute clonedObserver = (SetAttribute) super.clone();
		List<SetAttributeObject> list = new ArrayList<SetAttributeObject>();
		for (SetAttributeObject obj : getSetAttributeObjects()) {
			list.add(obj.clone());
		}
		clonedObserver.setSetAttributeObjects(list);
		return clonedObserver;
	}

	@Override
	public IFigure getToolTip(BControl control) {
		// // TODO: This method need rework!!!
		// StringBuilder builder = new StringBuilder();
		// builder.append("Set Attribute Observer:\n\n");
		// for (SetAttributeObject obj : getSetAttributeObjects()) {
		// if (obj.getEval() != null) {
		// builder.append("[Predicate: " + obj.getEval());
		// }
		// if (obj.getAttribute() != null) {
		// builder.append(" | Attribute: "
		// + control.getAttribute(obj.getAttribute()).getName());
		// }
		// if (obj.getValue() != null) {
		// builder.append(" | Value: " + obj.getValue() + "]");
		// }
		// builder.append("\n");
		// }
		// Label lb = new Label(builder.toString());
		return null;
	}

}
