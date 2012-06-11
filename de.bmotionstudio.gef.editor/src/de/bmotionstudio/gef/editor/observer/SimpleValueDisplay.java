/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.internal.Animation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverSimpleValueDisplay;

public class SimpleValueDisplay extends Observer {

	private String type;
	private String eval;
	private String predicate;
	private String replacementString;
	private transient String orgString;
	private transient boolean isOrgStringSet = false;

	public void check(final Animation animation, final BControl bcontrol) {

		// First evaluate predicate (predicate field)
		String bolValue = "true";
		if (predicate != null && predicate.length() > 0) {
			bolValue = parsePredicate(predicate, bcontrol, animation, null);
		}

		if (Boolean.valueOf(bolValue)) {

			String fEval = parseExpression(eval, bcontrol, animation, null);

			if (!isOrgStringSet) {
				orgString = bcontrol.getAttributeValue(
						AttributeConstants.ATTRIBUTE_TEXT).toString();
				isOrgStringSet = true;
			}

			String parseString = orgString;

			if (replacementString != null) {
				if (replacementString.length() > 0) {
					parseString = orgString.replace(replacementString, fEval);
				}
			} else {
				parseString = fEval;
			}

			bcontrol.setAttributeValue(AttributeConstants.ATTRIBUTE_TEXT,
					parseString);

		}

	}

	public ObserverWizard getWizard(final BControl bcontrol) {
		return new WizardObserverSimpleValueDisplay(bcontrol, this);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setEval(String eval) {
		this.eval = eval;
	}

	public String getEval() {
		return eval;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public void setReplacementString(String replacementString) {
		this.replacementString = replacementString;
	}

	public String getReplacementString() {
		return replacementString;
	}

	public Observer clone() throws CloneNotSupportedException {
		SimpleValueDisplay clonedObserver = (SimpleValueDisplay) super.clone();
		clonedObserver.isOrgStringSet = false;
		return clonedObserver;
	}

}
