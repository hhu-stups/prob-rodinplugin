/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.prolog;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.prob.core.types.BaseProbType;
import de.prob.core.types.CoupleProbType;
import de.prob.core.types.FreetypeProbType;
import de.prob.core.types.GivenSetProbType;
import de.prob.core.types.OperationProbType;
import de.prob.core.types.ProbDataType;
import de.prob.core.types.RecordProbType;
import de.prob.core.types.SequenceDataType;
import de.prob.core.types.SetProbType;
import de.prob.core.types.TypedIdentifier;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Generates a typed identifier from a Prolog term.
 * 
 * @author plagge
 * 
 */
public class TypedIdentifierGenerator {
	private static final Map<String, Handler> handlers = initHandlers();

	public static TypedIdentifier[] extract(final ListPrologTerm list) {
		TypedIdentifier[] result = new TypedIdentifier[list.size()];
		for (int i = 0; i < list.size(); i++) {
			PrologTerm element = list.get(i);
			if (element.isTerm()) {
				result[i] = extract((CompoundPrologTerm) element);
			} else
				throw expectationFailed("list of compound terms", element);
		}
		return result;
	}

	public static TypedIdentifier extract(final CompoundPrologTerm term) {
		final TypedIdentifier typedIdentifier;
		if (term.hasFunctor("b", 3)) {
			final String name = getIdentifier(term.getArgument(1));
			final ProbDataType type = extractType(term.getArgument(2));
			final String[] sections = extractSections(term.getArgument(3));
			typedIdentifier = new TypedIdentifier(name, type, sections);
		} else
			throw expectationFailed("b/3", term);
		return typedIdentifier;
	}

	private static String[] extractSections(final PrologTerm term) {
		if (term.isList()) {
			String[] sections = null;
			for (PrologTerm arg : ((ListPrologTerm) term)) {
				if (arg.hasFunctor("occurrences", 1)) {
					PrologTerm psections = ((CompoundPrologTerm) arg)
							.getArgument(1);
					if (psections.isList()) {
						sections = extractSectionsFromList((ListPrologTerm) psections);
					} else
						throw expectationFailed("list of sections", psections);
				}
			}
			return sections;
		} else
			throw expectationFailed("list of identifier informations", term);
	}

	private static String[] extractSectionsFromList(
			final ListPrologTerm psections) {
		String[] sections = new String[psections.size()];
		for (int i = 0; i < psections.size(); i++) {
			PrologTerm term = psections.get(i);
			if (term.isAtom()) {
				sections[i] = ((CompoundPrologTerm) term).getFunctor();
			}
		}
		return sections;
	}

	/**
	 * Generates a single Prob data type from a Prolog term like ProB sends
	 * them.
	 * 
	 * @param aterm
	 *            the prolog term representing the type, never <code>null</code>
	 * @return the prolog type, never <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the prolog term cannot be handled.
	 */
	public static ProbDataType extractType(final PrologTerm aterm) {
		ProbDataType type;
		if (aterm.isTerm()) {
			CompoundPrologTerm term = (CompoundPrologTerm) aterm;
			String functor = term.getFunctor();
			String lookup = functor + "/" + term.getArity();
			Handler handler = handlers.get(lookup);
			if (handler != null) {
				type = handler.handle(term);
			} else
				throw new IllegalArgumentException("unknown type handler for "
						+ lookup);
		} else
			throw expectationFailed("compound term", aterm);
		return type;
	}

	private static String getIdentifier(final PrologTerm term) {
		final String name;
		if (term.hasFunctor("identifier", 1)) {
			final PrologTerm arg1 = ((CompoundPrologTerm) term).getArgument(1);
			name = arg1.isAtom() ? PrologTerm.atomicString(arg1) : arg1
					.toString();
		} else
			throw expectationFailed("identifier/1", term);
		return name;
	}

	private static IllegalArgumentException expectationFailed(
			final String expected, final PrologTerm term) {
		return new IllegalArgumentException("expected " + expected
				+ ", but was: " + term.toString());
	}

	private static Map<String, Handler> initHandlers() {
		Map<String, Handler> handlers = new HashMap<String, Handler>();
		handlers.put("integer/0", new BasicHandler(BaseProbType.INTEGER));
		handlers.put("float/0", new BasicHandler(BaseProbType.FLOAT));
		handlers.put("real/0", new BasicHandler(BaseProbType.REAL));
		handlers.put("string/0", new BasicHandler(BaseProbType.STRING));
		handlers.put("boolean/0", new BasicHandler(BaseProbType.BOOL));
		handlers.put("pred/0", new BasicHandler(BaseProbType.PREDICATE));
		handlers.put("subst/0", new BasicHandler(BaseProbType.SUBSTITUTION));
		handlers.put("any/0", new BasicHandler(BaseProbType.ANY));
		handlers.put("set/1", new SetHandler());
		handlers.put("seq/1", new SeqHandler());
		handlers.put("global/1", new GivenSetHandler());
		handlers.put("couple/2", new CoupleHandler());
		handlers.put("op/2", new OperationHandler());
		handlers.put("freetype/1", new FreetypeHandler());
		handlers.put("record/1", new RecordHandler());
		return Collections.unmodifiableMap(handlers);
	}

	private interface Handler {
		ProbDataType handle(CompoundPrologTerm term);
	}

	private static class BasicHandler implements Handler {
		private final ProbDataType type;

		public BasicHandler(final ProbDataType type) {
			super();
			this.type = type;
		}

		public ProbDataType handle(final CompoundPrologTerm term) {
			return type;
		}

	}

	private static class SetHandler implements Handler {
		public ProbDataType handle(final CompoundPrologTerm term) {
			return new SetProbType(extractType(term.getArgument(1)));
		}
	}

	private static class SeqHandler implements Handler {
		public ProbDataType handle(final CompoundPrologTerm term) {
			return new SequenceDataType(extractType(term.getArgument(1)));
		}
	}

	private static class GivenSetHandler implements Handler {
		public ProbDataType handle(final CompoundPrologTerm term) {
			return new GivenSetProbType(getString(term.getArgument(1)));
		}
	}

	private static class CoupleHandler implements Handler {
		public ProbDataType handle(final CompoundPrologTerm term) {
			return new CoupleProbType(extractType(term.getArgument(1)),
					extractType(term.getArgument(2)));
		}
	}

	private static class OperationHandler implements Handler {
		public ProbDataType handle(final CompoundPrologTerm term) {
			ProbDataType[] results = createList(term.getArgument(1));
			ProbDataType[] params = createList(term.getArgument(2));
			return new OperationProbType(results, params);
		}
	}

	private static class FreetypeHandler implements Handler {
		public ProbDataType handle(final CompoundPrologTerm term) {
			return new FreetypeProbType(getString(term.getArgument(1)));
		}
	}

	private static class RecordHandler implements Handler {
		public ProbDataType handle(final CompoundPrologTerm term) {
			if (term.getArgument(1) instanceof ListPrologTerm) {
				ListPrologTerm list = (ListPrologTerm) term.getArgument(1);
				Map<String, ProbDataType> fields = new HashMap<String, ProbDataType>();
				for (int i = 0; i < list.size(); i++) {
					PrologTerm field = list.get(i);
					if (field.hasFunctor("field", 2)) {
						final CompoundPrologTerm cfield = (CompoundPrologTerm) field;
						final ProbDataType ftype = extractType(cfield
								.getArgument(2));
						fields.put(getString(cfield.getArgument(1)), ftype);
					} else
						throw new IllegalArgumentException(
								"expected field/2, but was: "
										+ field.toString());
				}
				return new RecordProbType(fields);
			} else
				throw new IllegalArgumentException(
						"expected field list, but was: " + term.toString());
		}
	}

	private static String getString(final PrologTerm term) {
		return term.isAtom() ? ((CompoundPrologTerm) term).getFunctor() : term
				.toString();
	}

	private static ProbDataType[] createList(final PrologTerm term) {
		ProbDataType[] result;
		if (term instanceof ListPrologTerm) {
			ListPrologTerm list = (ListPrologTerm) term;
			result = new ProbDataType[list.size()];
			for (int i = 0; i < list.size(); i++) {
				result[i] = extractType(list.get(i));
			}
		} else
			throw new IllegalArgumentException("expected list, but was: "
					+ term.toString());
		return result;
	}
}
