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

	public static List<String> split(String input, char tempReplacement) {
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

	public static List<String> split2(String input, char tempReplacement) {
		while (input.matches(".*\\([^\\)]+\\|->[^\\)]+\\).*")) {
			input = input.replaceAll("(\\([^\\)]+)\\|->([^\\)]+\\))", "$1"
					+ tempReplacement + "$2");
		}
		String[] split = input.split("\\|->");
		List<String> output = new LinkedList<String>();
		for (String s : split) {
			output.add(s.replaceAll(tempReplacement + "", "\\|->").trim());
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

			// System.out.println(fEval);

			// String input = "aa, a, aa, {bb, 1, 2}, {cc}, {dd,5}";
			String input = fEval;
			List<String> rows = split(input, '#');

			AbstractAttribute attributeRows = control
					.getAttribute(AttributeConstants.ATTRIBUTE_ROWS);
			Integer numberOfOldRows = Integer.valueOf(attributeRows
					.getInitValue().toString());

			AbstractAttribute attributeColumns = control
					.getAttribute(AttributeConstants.ATTRIBUTE_COLUMNS);
			Integer numberOfOldColumns = Integer.valueOf(attributeColumns
					.getInitValue().toString());

			int numberOfNewRows = rows.size();

			// Set the correct number of rows
			control.setAttributeValue(AttributeConstants.ATTRIBUTE_ROWS,
					numberOfNewRows + numberOfOldRows, true, false);

			boolean setColumns = false;

			// Set content and the correct number of columns
			for (int i = numberOfOldRows; i < numberOfNewRows + numberOfOldRows; i++) {

				String content = UnicodeTranslator.toAscii(rows.get(i
						- numberOfOldRows));

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

	// private Iterable<MatchResult> findMatches(String pattern, CharSequence s)
	// {
	// List<MatchResult> results = new ArrayList<MatchResult>();
	// for (Matcher m = Pattern.compile(pattern).matcher(s); m.find();)
	// results.add(m.toMatchResult());
	// return results;
	// }

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
