package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardColumnObserver;
import de.bmotionstudio.gef.editor.util.BMSUtil;
import de.prob.unicode.UnicodeTranslator;

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

			fEval = fEval.replace("}", "").replace("{", "").replace(")", "")
					.replace("(", "");
			String[] splitArray = fEval.split(",");

			// ---------------------------------------------------------------

			int numberOfRows = splitArray.length;

			BControl tableControl = control.getParent().getParent();

			// Set the correct number of rows
			tableControl
					.setAttributeValue(AttributeConstants.ATTRIBUTE_ROWS,
							numberOfRows, true, false);

			System.out.println("number of rows: " + numberOfRows);

			boolean setColumns = false;

			// Set content and the correct number of columns
			for (int i = 0; i < numberOfRows; i++) {

				String content = UnicodeTranslator.toAscii(splitArray[i])
						.replace("|->", ",");

				String[] vals = content.split(",");
				int numberOfColumns = vals.length;
				
				// Set only one time the number of columns!
				if (!setColumns) {
					tableControl
							.setAttributeValue(
									AttributeConstants.ATTRIBUTE_COLUMNS,
									numberOfColumns, true, false);
					setColumns = true;
					System.out.println("number of columns: " + numberOfColumns);
				}

				for (int z = 0; z < numberOfColumns; z++) {
					String val = vals[z];
					BControl column = tableControl.getChildrenArray().get(z);
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
		return new WizardColumnObserver(control, this);
	}

}
