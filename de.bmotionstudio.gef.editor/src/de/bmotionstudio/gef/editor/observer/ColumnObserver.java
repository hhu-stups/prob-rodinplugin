package de.bmotionstudio.gef.editor.observer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardColumnObserver;
import de.bmotionstudio.gef.editor.util.BMSUtil;
import de.prob.unicode.UnicodeTranslator;

public class ColumnObserver extends Observer {

	private String expression;
	private String predicate;

	public static List<String> split(String input, char tempReplacement) {
		while (input.matches(".*\"[^\\{\\}]+,[^\\{\\}]+.*")) {
			input = input.replaceAll("(\"[^\\{\\}]+),([^\\{\\}]+)", "$1"
					+ tempReplacement + "$2");
		}
		while (input.matches(".*\\{[^\\}]+,[^\\}]+\\}.*")) {
			input = input.replaceAll("(\\{[^\\}]+),([^\\}]+\\})", "$1"
					+ tempReplacement + "$2");
		}
		String[] split = input.split(",");
		List<String> output = new LinkedList<String>();
		for (String s : split) {
			output.add(s.replaceAll(tempReplacement + "", ",").trim());
		}
		return output;
	}

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
			fEval = UnicodeTranslator.toAscii(fEval);
			fEval = fEval.replaceAll("^\\{", "");
			fEval = fEval.replaceAll("\\}$", "");
			List<String> output = split(fEval, '#');

			AbstractAttribute attributeRows = control.getParent().getAttribute(
					AttributeConstants.ATTRIBUTE_ROWS);

			Integer defaultRows = Integer.valueOf(attributeRows.getInitValue()
					.toString());

			control.getParent().setAttributeValue(
					AttributeConstants.ATTRIBUTE_ROWS,
					defaultRows + output.size(), true, false);

			for (int i = defaultRows; i < output.size() + defaultRows; i++) {
				String val = output.get(i - defaultRows);
				if (val != null && val.length() > 0)
					val = UnicodeTranslator.toUnicode(val);
				control.getChildrenArray()
						.get(i)
						.setAttributeValue(AttributeConstants.ATTRIBUTE_TEXT,
								val);
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
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new WizardColumnObserver(shell, control, this);
	}

}