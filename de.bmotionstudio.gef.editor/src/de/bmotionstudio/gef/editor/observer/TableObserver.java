package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardTableObserver;
import de.bmotionstudio.gef.editor.util.BMSUtil;
import de.prob.unicode.UnicodeTranslator;

public class TableObserver extends Observer {

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

			fEval = fEval.replace("}", "").replace("{", "").replace(")", "")
					.replace("(", "");
			String[] splitArray = fEval.split(",");

			// ---------------------------------------------------------------

			Integer numberOfOldRows = Integer.valueOf(control
					.getAttributeValue(AttributeConstants.ATTRIBUTE_ROWS)
					.toString());
			Integer numberOfOldColumns = Integer.valueOf(control
					.getAttributeValue(AttributeConstants.ATTRIBUTE_COLUMNS)
					.toString());
			int numberOfNewRows = splitArray.length;

			// Set the correct number of rows
			control.setAttributeValue(AttributeConstants.ATTRIBUTE_ROWS,
					numberOfNewRows + numberOfOldRows, true, false);

			boolean setColumns = false;

			// Set content and the correct number of columns
			for (int i = numberOfOldRows; i < numberOfNewRows + numberOfOldRows; i++) {

				String content = UnicodeTranslator.toAscii(
						splitArray[i - numberOfOldRows]).replace("|->", ",");

				String[] vals = content.split(",");
				int numberOfNewColumns = vals.length;

				// Set only one time the number of columns!
				if (!setColumns) {
					int ncolumns = numberOfNewColumns;
					if (numberOfOldColumns > numberOfNewColumns)
						ncolumns = numberOfOldColumns;
					control.setAttributeValue(
							AttributeConstants.ATTRIBUTE_COLUMNS, ncolumns,
							true, false);
					setColumns = true;
				}

				for (int z = 0; z < numberOfNewColumns; z++) {
					String val = vals[z];
					BControl column = control.getChildrenArray().get(z);
					BControl cell = column.getChildrenArray().get(i);
					cell.setAttributeValue(AttributeConstants.ATTRIBUTE_TEXT,
							val);
				}

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
		return new WizardTableObserver(control, this);
	}

}
