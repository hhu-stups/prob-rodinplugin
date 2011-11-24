/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.gef.editor.internal.Animation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.ObserverEvalObject;
import de.bmotionstudio.gef.editor.part.AppAbstractEditPart;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.GetOperationByPredicateCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.EvaluationStateElement;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;

public abstract class AbstractExpressionControl extends BindingObject {

	private static final Pattern PATTERN = Pattern.compile("\\$(.+?)\\$");

	protected transient String ID;
	protected transient String name;
	protected transient String description;
	protected transient Boolean hasError = false;
	private transient static final String DEFAULT_PREDICATE = "1=1";
	private transient static final String DEFAULT_BOOLVAL = "true";

	public String getID() {
		return this.ID;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public void addError(BControl control, Animation animation, String message) {
		// TODO: Implement me!
		// History history = animation.getAnimator().getHistory();
		// int currentHistoryPos = history.getCurrentPosition();
		// control.getVisualization().addError(
		// new ErrorMessage(history.getAllItems()[currentHistoryPos - 1],
		// this, control, message));
	}

	/**
	 * tbd
	 * 
	 * @param expressionString
	 * @param control
	 * @param animation
	 * @param obj
	 * @return true or false
	 */
	protected String parsePredicate(String expressionString, BControl control,
			Animation animation, ObserverEvalObject obj) {
		if (expressionString == null || expressionString.trim().length() == 0)
			return DEFAULT_BOOLVAL;
		return parseExpression(expressionString, true, control, animation, obj,
				true);
	}

	protected String parseExpression(String expressionString, BControl control,
			Animation animation, ObserverEvalObject obj) {
		return parseExpression(expressionString, true, control, animation, obj,
				false);
	}

	protected String parseExpression(final String expressionString,
			final boolean evalSingleExpression, final BControl control,
			final Animation animation, final ObserverEvalObject obj,
			final boolean isPredicate) {

		Map<EvaluationElement, String> evaluationKeys = new HashMap<EvaluationElement, String>();

		boolean hasSubExpressions = false;

		hasError = false;

		// Find expressions and collect ExpressionEvalElements
		final Matcher matcher = PATTERN.matcher(expressionString);

		while (matcher.find()) {
			final String subExpr = matcher.group(1);
			collectEvalElements(subExpr, "$" + subExpr + "$", isPredicate,
					animation, control, evaluationKeys);
			hasSubExpressions = true;
		}

		// We have only one expression (without "$$")
		if (!hasSubExpressions) {
			if (evalSingleExpression) {
				collectEvalElements(expressionString, expressionString,
						isPredicate, animation, control, evaluationKeys);
			} else {
				return expressionString;
			}
		}

		// Try to get expression results and parse expression string
		Collection<EvaluationStateElement> resultList;
		try {
			resultList = getExpressionValues(control, animation,
					new ArrayList<EvaluationElement>(evaluationKeys.keySet()));
		} catch (ProBException e) {
			resultList = Collections.emptyList();
			hasError = true;
		}

		// If getting ExpressionEvalElement throws no error, try to get
		// expression results
		String result = expressionString;
		if (!hasError) {
			for (EvaluationStateElement stateElement : resultList) {
				final EvaluationElement evalElement = stateElement.getElement();
				final String text;
				if (isPredicate) {
					text = stateElement.getResult().isPredicateTrue() ? "true"
							: "false";
				} else {
					text = stateElement.getText();
				}
				final String subExpression = evaluationKeys.get(evalElement);
				result = result.replace(subExpression, text);
			}
		} else {
			if (obj != null)
				obj.setHasError(true);
			addError(control, animation,
					"An error occurred while evaluating expression\\predicate value: \""
							+ expressionString
							+ "\". Please check your expression\\predicate.");
		}

		return result;

	}

	private void collectEvalElements(final String subexpression,
			final String key, final boolean isPredicate,
			final Animation animation, final BControl control,
			final Map<EvaluationElement, String> evaluationKeys) {

		final String parsedSubexpr = parseControls(subexpression, control);
		EvaluationElement evalElement;
		try {
			evalElement = animation.getCachedEvalElement(parsedSubexpr,
					isPredicate);
			evaluationKeys.put(evalElement, key);
		} catch (UnsupportedOperationException e) {
			hasError = true;
		} catch (ProBException e) {
			hasError = true;
		} catch (ProBParseException e) {
			addError(control, animation, e.getMessage());
			hasError = true;
		}

	}

	protected List<Operation> parseOperation(final String opName,
			String opPredicate, int opRandom, final Animation animation,
			final String currentState, final BControl control) {

		try {

			if (opPredicate != null && opPredicate.length() > 0)
				opPredicate = parseControls(opPredicate, control);
			else
				opPredicate = DEFAULT_PREDICATE;

			if (opRandom < 1)
				opRandom = 1;

			return GetOperationByPredicateCommand.getOperations(
					animation.getAnimator(), currentState, opName, opPredicate,
					opRandom);

		} catch (ProBException e) {
			addError(control, animation, e.getMessage());
			hasError = true;
		} catch (BException e) {
			addError(control, animation, e.getMessage());
			hasError = true;
		}

		return null;

	}

	/**
	 * This method matches the pattern <i>(\\w+)</i>. This means that the method
	 * matches alphanumeric words in the given predicate or expression string.
	 * The method focuses on control id's or the key word <i>this</i>. In the
	 * first case the method tries to find a reference on the corresponding
	 * control in the visualization regarding to the matched control id. In the
	 * second case the method creates a reference to the control which contains
	 * the observer. In addition in both cases the method returns the value of
	 * {@link AppAbstractEditPart#getValueOfData()} of the located
	 * {@link BControl}.
	 * 
	 * @param expressionString
	 * @param control
	 * @return the parsed expression
	 */
	protected String parseControls(String expressionString, BControl control) {

		List<String> allControlIDs = control.getVisualization()
				.getAllBControlNames();

		// Search for control ids
		Pattern cPattern = Pattern.compile("(\\w+)");
		Matcher cMatcher = cPattern.matcher(expressionString);

		while (cMatcher.find()) {

			String controlID = cMatcher.group(1);

			if (controlID.equals("this")) {

				expressionString = expressionString.replace(controlID, control
						.getAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM)
						.toString());

			} else if (allControlIDs.contains(controlID)) {

				expressionString = expressionString.replace(controlID, control
						.getVisualization().getBControl(controlID)
						.getValueOfData());

			} else {
				// TODO: Return error if no control exists
			}
		}

		return expressionString;

	}

	protected Collection<EvaluationStateElement> getExpressionValues(
			final BControl control, final Animation animation,
			final Collection<EvaluationElement> evalElements)
			throws ProBException {
		final State state = animation.getAnimator().getCurrentState();
		// TODO[DP, 11.04.2011] Add an animator to the parameters!
		final Collection<EvaluationStateElement> values = EvaluationGetValuesCommand
				.getValuesForExpressionsCached(state, evalElements);
		return values;
	}

	/**
	 * This method is invoked before the expression control ({@link IObserver}
	 * or {@link SchedulerEvent}) will be deleted.
	 * 
	 * @param control
	 *            which holds the expression control
	 */
	public void beforeDelete(BControl control) {
	}

}
