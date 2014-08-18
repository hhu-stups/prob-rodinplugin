package de.prob.ui.validators;

import org.eclipse.jface.dialogs.IInputValidator;

import de.prob.core.LanguageDependendAnimationPart;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;

public final class PredicateValidator implements IInputValidator {
	final LanguageDependendAnimationPart ldp;

	public PredicateValidator(final LanguageDependendAnimationPart ldp) {
		this.ldp = ldp;
	}

	@Override
	public String isValid(final String newText) {
		if (newText.trim().isEmpty())
			return null;
		if (ldp == null)
			return "Cannot parse predicates for the current formalism";
		try {
			final ProBParserBaseAdapter parser = new ProBParserBaseAdapter(ldp);
			parser.parsePredicate(newText, false);
		} catch (UnsupportedOperationException e) {
			return "The parser for this formalism cannot parse predicates";
		} catch (ProBParseException e) {
			return e.getLocalizedMessage();
		}
		return null;
	}
}