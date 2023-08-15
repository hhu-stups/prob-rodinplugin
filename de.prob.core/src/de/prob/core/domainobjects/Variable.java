/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.domainobjects;

import java.util.Objects;

import de.prob.core.command.CommandException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ResultParserException;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

public final class Variable {

	private final String identifier;
	private final String value;
	private final PrologTerm rawValue;

	public Variable(final CompoundPrologTerm binding) throws CommandException  {

		CompoundPrologTerm variableName;
		try {
			variableName = BindingGenerator.getCompoundTerm(
					binding.getArgument(1), 0);
		} catch (ResultParserException e) {
			CommandException commandException = new CommandException(e.getLocalizedMessage(), e);
			commandException.notifyUserOnce();
			throw commandException;
		}
		identifier = variableName.getFunctor();

		final PrologTerm raw = binding.getArgument(2);
		if (raw.hasFunctor("?", 0)) {
			rawValue = null;
			value = null;
		} else {
			rawValue = raw;
			CompoundPrologTerm term;
			try {
				term = BindingGenerator.getCompoundTerm(
						binding.getArgument(3), 0);
			} catch (ResultParserException e) {
				CommandException commandException = new CommandException(e.getLocalizedMessage(), e);
				commandException.notifyUserOnce();
				throw commandException;
			}
			// value = ReverseTranslate.reverseTranslate(term.getFunctor());
			value = UnicodeTranslator.toUnicode(term.getFunctor());
		}

	}

	public String getIdentifier() {
		return identifier;
	}

	public String getValue() {
		return value;
	}

	/**
	 * This method is for internal usage, it will be replaced when switching to
	 * a DOM for values
	 * 
	 * @return
	 */
	@Deprecated
	public PrologTerm getRawValue() {
		return rawValue;
	}

	public boolean hasValue() {
		return rawValue != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(identifier);
		sb.append(": ").append(value);
		return sb.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		final boolean equal;
		if (obj == this) {
			equal = true;
		} else if (obj != null && obj instanceof Variable) {
			Variable other = (Variable) obj;
			equal = this.identifier.equals(other.identifier)
					&& Objects.equals(this.value, other.value);
		} else {
			equal = false;
		}
		return equal;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
