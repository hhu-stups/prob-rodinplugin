package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardColumnObserver;
import de.bmotionstudio.gef.editor.util.BMSUtil;

public class ColumnObserver extends Observer {

	private String expression;
	private String predicate;

	@Override
	public void check(Animation animation, BControl control) {

		// First evaluate predicate (predicate field)
		String bolValue = "true";
		if (predicate != null && predicate.length() > 0) {
			bolValue = BMSUtil.parsePredicate(predicate, control, animation);
		}

		if (Boolean.valueOf(bolValue)) {

			String fEval = BMSUtil.parseExpression(expression, control,
					animation);
			fEval = fEval.replace("}", "").replace("{", "");
			String[] splitArray = fEval.split(",");

			AbstractAttribute attributeRows = control.getParent().getAttribute(
					AttributeConstants.ATTRIBUTE_ROWS);

			Integer defaultRows = Integer.valueOf(attributeRows.getInitValue()
					.toString());

			control.getParent().setAttributeValue(
					AttributeConstants.ATTRIBUTE_ROWS,
					defaultRows + splitArray.length, true, false);

			for (int i = defaultRows; i < splitArray.length + defaultRows; i++) {
				control.getChildrenArray()
						.get(i)
						.setAttributeValue(AttributeConstants.ATTRIBUTE_TEXT,
								splitArray[i - defaultRows]);
			}

		}

	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	@Override
	public ObserverWizard getWizard(BControl control) {
		return new WizardColumnObserver(control, this);
	}

}