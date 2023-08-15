/**
 * 
 */
package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import de.prob.core.Animator;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.EvaluationStateElement;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryBasedCache;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

/**
 * This command sends a list of expression IDs and a state ID to ProB and
 * retrieves a list of values of their corresponding values in that state.
 * 
 * @see EvaluationExpandCommand
 * @see EvaluationGetTopLevelCommand
 * @author plagge
 */
public class EvaluationGetValuesCommand implements IComposableCommand {
	private static final String COMMAND_NAME = "evaluation_get_values";
	private static final String VALUE_VARNAME = "Values";
	private static final String TRUE = UnicodeTranslator.toUnicode("true");
	private static final String FALSE = UnicodeTranslator.toUnicode("false");
	private static final String CACHE_KEY = EvaluationGetValuesCommand.class
			.getName() + ".valuecache";

	/**
	 * Ask ProB for the values of the given elements in the given state.
	 * 
	 * @param state
	 * @param elements
	 * @return
	 * @throws ProBException
	 */
	public static Collection<EvaluationStateElement> getValuesForExpressionsUncached(
			final State state, final Collection<EvaluationElement> elements)
			throws ProBException {
		final Collection<EvaluationStateElement> result;
		if (state == null || elements.isEmpty()) {
			result = Collections.emptyList();
		} else {
			final EvaluationGetValuesCommand cmd = new EvaluationGetValuesCommand(
					state.getId(), elements);
			Animator.getAnimator().execute(cmd);
			Map<EvaluationElement, EvaluationResult> values = cmd.getResult();
			result = new ArrayList<EvaluationStateElement>(values.size());
			for (final Map.Entry<EvaluationElement, EvaluationResult> entry : values
					.entrySet()) {
				result.add(new EvaluationStateElement(entry.getKey(), state,
						entry.getValue()));
			}
		}
		return result;
	}

	public static Collection<EvaluationStateElement> getValuesForExpressionsCached(
			final State state, final Collection<EvaluationElement> elements)
			throws ProBException {
		final Collection<EvaluationStateElement> result;
		if (state == null || elements.isEmpty()) {
			result = Collections.emptyList();
		} else {
			final Map<EvaluationElement, EvaluationStateElement> cache = getCache(state);
			synchronized (cache) {
				Collection<EvaluationElement> toCompute = new HashSet<EvaluationElement>(
						elements);
				Collection<EvaluationElement> cached = cache.keySet();
				toCompute.removeAll(cached);
				Collection<EvaluationStateElement> computed = getValuesForExpressionsUncached(
						state, toCompute);
				for (final EvaluationStateElement dElement : computed) {
					cache.put(dElement.getElement(), dElement);
				}

				result = new ArrayList<EvaluationStateElement>(elements.size());
				for (final EvaluationElement sElement : elements) {
					result.add(cache.get(sElement));
				}
				result.addAll(computed);
			}
		}
		return result;
	}

	public static EvaluationStateElement getSingleValueCached(
			final State state, final EvaluationElement element)
			throws ProBException {
		EvaluationStateElement result;
		if (state == null) {
			result = null;
		} else {
			final Map<EvaluationElement, EvaluationStateElement> cache = getCache(state);
			synchronized (cache) {
				result = cache.get(element);
				if (result == null) {
					Collection<EvaluationStateElement> values = getValuesForExpressionsUncached(
							state, Collections.singleton(element));
					result = values.iterator().next();
					cache.put(element, result);
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static Map<EvaluationElement, EvaluationStateElement> getCache(
			final State state) {
		Map<EvaluationElement, EvaluationStateElement> cache;
		final Animator animator = Animator.getAnimator();
		synchronized (animator) {
			HistoryBasedCache<Map<EvaluationElement, EvaluationStateElement>> hcache = (HistoryBasedCache<Map<EvaluationElement, EvaluationStateElement>>) animator
					.getData(CACHE_KEY);
			if (hcache == null) {
				History history = animator.getHistory();
				hcache = new HistoryBasedCache<Map<EvaluationElement, EvaluationStateElement>>(
						history);
				history.addListener(hcache);
				animator.setData(CACHE_KEY, hcache);
			}
			cache = hcache.get(state);
			if (cache == null) {
				cache = new HashMap<EvaluationElement, EvaluationStateElement>();
				hcache.put(state, cache);
			}
		}
		return cache;
	}

	private final String stateId;
	private final Collection<EvaluationElement> elements;

	private Map<EvaluationElement, EvaluationResult> result;

	public EvaluationGetValuesCommand(final String stateId,
			final Collection<EvaluationElement> elements) {
		this.stateId = stateId;
		this.elements = new ArrayList<EvaluationElement>(elements);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final ListPrologTerm valueTerms = (ListPrologTerm) bindings
				.get(VALUE_VARNAME);
		result = retrieveValues(elements, valueTerms);
	}

	public Map<EvaluationElement, EvaluationResult> getResult() {
		return result;
	}

	private static Map<EvaluationElement, EvaluationResult> retrieveValues(
			final Collection<EvaluationElement> elements,
			final Collection<PrologTerm> valueTerms) {
		if (valueTerms.size() != elements.size())
			throw new IllegalStateException(COMMAND_NAME
					+ " returned the wrong number of results");
		Map<EvaluationElement, EvaluationResult> results = new HashMap<EvaluationElement, EvaluationResult>();
		Iterator<PrologTerm> it = valueTerms.iterator();
		for (final EvaluationElement element : elements) {
			final PrologTerm valueTerm = it.next();
			final EvaluationResult value;
			if (valueTerm.hasFunctor("p", 1)) {
				final CompoundPrologTerm vc = (CompoundPrologTerm) valueTerm;
				final String valString = ((CompoundPrologTerm) vc
						.getArgument(1)).getFunctor();
				final boolean predTrue = "true".equals(valString);
				final String asString = predTrue ? TRUE : FALSE;
				value = new EvaluationResult(asString, true, true, predTrue,
						false);
			} else if (valueTerm.hasFunctor("v", 1)) {
				final CompoundPrologTerm vc = (CompoundPrologTerm) valueTerm;
				final String valString = ((CompoundPrologTerm) vc
						.getArgument(1)).getFunctor();
				final String translated = valString.length() == 0 ? ""
						: UnicodeTranslator.toUnicode(valString);
				value = new EvaluationResult(translated, true, false, false,
						false);
			} else if (valueTerm.hasFunctor("e", 1)) {
				final CompoundPrologTerm vc = (CompoundPrologTerm) valueTerm;
				final String error = ((CompoundPrologTerm) vc.getArgument(1))
						.getFunctor();
				value = new EvaluationResult(error, true, false, false, true);
			} else if (valueTerm.hasFunctor("i", 0)) {
				value = new EvaluationResult(null, false, false, false, false);
			} else
				throw new IllegalArgumentException(COMMAND_NAME
						+ " returned unexpected term " + valueTerm.toString());
			results.put(element, value);
		}
		return results;
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		pto.openList();
		for (final EvaluationElement element : elements) {
			element.getId().toTermOutput(pto);
		}
		pto.closeList();
		pto.printAtomOrNumber(stateId);
		pto.printVariable(VALUE_VARNAME);
		pto.closeTerm();
	}

	public static class EvaluationResult {
		private final String text;
		private final boolean isActive;
		private final boolean isPredicate;
		private final boolean isPredicateTrue;
		private final boolean hasError;

		public EvaluationResult(final String text, final boolean isActive,
				final boolean isPredicate, final boolean isPredicateTrue,
				final boolean hasError) {
			this.text = text;
			this.isActive = isActive;
			this.isPredicate = isPredicate;
			this.isPredicateTrue = isPredicateTrue;
			this.hasError = hasError;
		}

		public String getText() {
			return text;
		}

		public boolean isActive() {
			return isActive;
		}

		public boolean isPredicate() {
			return isPredicate;
		}

		/**
		 * note: returns arbitrary value if called on something else than a
		 * predicate.
		 * 
		 * @return
		 */
		public boolean isPredicateTrue() {
			return isPredicateTrue;
		}

		public boolean hasError() {
			return hasError;
		}
	};

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(
				"EvaluationGetValuesCommand[elements=");
		for (EvaluationElement element : elements) {
			sb.append(element.getId());
			sb.append(',');
		}
		sb.append(" stateId=").append(stateId).append(']');
		return sb.toString();
	}

}
