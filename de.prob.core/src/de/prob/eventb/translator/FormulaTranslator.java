package de.prob.eventb.translator;

import de.prob.unicode.UnicodeTranslator;

public class FormulaTranslator {

	public static String translate(String input) {
		return UnicodeTranslator.toUnicode(input);
	}

}
