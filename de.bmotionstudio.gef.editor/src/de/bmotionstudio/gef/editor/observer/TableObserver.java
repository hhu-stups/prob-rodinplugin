package de.bmotionstudio.gef.editor.observer;

import java.util.LinkedList;
import java.util.List;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardTableObserver;
import de.bmotionstudio.gef.editor.util.BMSUtil;
import de.prob.unicode.UnicodeTranslator;

public class TableObserver extends Observer {

	private String expression;
	private String predicate;
	private boolean overrideCells;
	private boolean keepHeader;

	public static List<String> split(String input, char tempReplacement) {
		while (input.matches(".*\\{[^\\}]+,[^\\}]+\\}.*")) {
			input = input.replaceAll("(\\{[^\\}]+),([^\\}]+\\})", "$1"
					+ tempReplacement + "$2");
		}
		List<String> output = new LinkedList<String>();
		if (input.length() > 0) {
			String[] split = input.split(",");
			for (String s : split) {
				output.add(s.replaceAll(tempReplacement + "", ",").trim());
			}
		}
		return output;
	}

	public static List<String> split2(String input, char tempReplacement) {
		while (input.matches(".*\\([^\\)]+\\|->[^\\)]+\\).*")) {
			input = input.replaceAll("(\\([^\\)]+)\\|->([^\\)]+\\))", "$1"
					+ tempReplacement + "$2");
		}
		List<String> output = new LinkedList<String>();
		if (input.length() > 0) {
			String[] split = input.split("\\|->");
			for (String s : split) {
				output.add(s.replaceAll(tempReplacement + "", "\\|->").trim());
			}
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

			String input = fEval;
			List<String> rows = split(input, '#');

			Integer numberOfOldRows = 0;
			Integer numberOfOldColumns = 0;

			if (!overrideCells) {
				AbstractAttribute attributeRows = control
						.getAttribute(AttributeConstants.ATTRIBUTE_ROWS);
				numberOfOldRows = Integer.valueOf(attributeRows.getInitValue()
						.toString());
				AbstractAttribute attributeColumns = control
						.getAttribute(AttributeConstants.ATTRIBUTE_COLUMNS);
				numberOfOldColumns = Integer.valueOf(attributeColumns
						.getInitValue().toString());
			} else if (keepHeader) {
				numberOfOldRows = 1;
			}

			int numberOfNewRows = rows.size();

			// Set the correct number of rows
			control.setAttributeValue(AttributeConstants.ATTRIBUTE_ROWS,
					numberOfNewRows + numberOfOldRows, true, false);

			boolean setColumns = false;

			// Set content and the correct number of columns
			for (int i = numberOfOldRows; i < numberOfNewRows + numberOfOldRows; i++) {

				String content = rows.get(i - numberOfOldRows);

				if (content != null && content.length() > 0)
					content = UnicodeTranslator.toAscii(content);

				content = content.replaceAll("^\\(", "");
				content = content.replaceAll("\\)$", "");

				List<String> columns = split2(content, '#');
				int numberOfNewColumns = columns.size();

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
					String val = columns.get(z);
					BControl column = control.getChildrenArray().get(z);
					BControl cell = column.getChildrenArray().get(i);
					if (val != null && val.length() > 0)
						val = UnicodeTranslator.toUnicode(val);
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

	public boolean isOverrideCells() {
		return overrideCells;
	}

	public void setOverrideCells(boolean overrideCells) {
		this.overrideCells = overrideCells;
	}

	public boolean isKeepHeader() {
		return keepHeader;
	}

	public void setKeepHeader(boolean keepHeader) {
		this.keepHeader = keepHeader;
	}

}
