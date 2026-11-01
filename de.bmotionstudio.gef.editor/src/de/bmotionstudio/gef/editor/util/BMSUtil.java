package de.bmotionstudio.gef.editor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.GetOperationByPredicateCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.EvaluationStateElement;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;

public class BMSUtil {

	private static final Pattern PATTERN = Pattern.compile("\\$(.+?)\\$");

	// private static Boolean hasError = false;
	private static final String DEFAULT_PREDICATE = "1=1";
	private static final String DEFAULT_BOOLVAL = "true";

	public static String parseExpression(String expressionString,
			BControl control, Animation animation) {
		return parseExpression(expressionString, control, animation, false,
				true);
	}

	public static String parseExpression(final String expressionString,
			final BControl control, final Animation animation,
			boolean parseControls) {
		return parseExpression(expressionString, control, animation, false,
				parseControls);
	}

	public static String parsePredicate(String expressionString,
			BControl control, Animation animation) {
		return parsePredicate(expressionString, control, animation, true);
	}
	
	public static String parsePredicate(String expressionString,
			BControl control, Animation animation, boolean parseControls) {
		if (expressionString == null || expressionString.trim().length() == 0)
			return DEFAULT_BOOLVAL;
		return parseExpression(expressionString, control, animation, true,
				parseControls);
	}

	private static String parseExpression(String expressionString,
			BControl control, Animation animation, boolean isPredicate,
			boolean parseControls) {

		boolean hasError = false;

		boolean hasSubExpressions = false;

		Map<EvaluationElement, String> evaluationKeys = new HashMap<EvaluationElement, String>();

		// Find expressions and collect ExpressionEvalElements
		final Matcher matcher = PATTERN.matcher(expressionString);
		while (matcher.find()) {
			final String subExpr = matcher.group(1);
			collectEvalElements(subExpr, "$" + subExpr + "$", isPredicate,
					animation, control, evaluationKeys, parseControls);
			hasSubExpressions = true;
		}

		// We have only one expression (without "$$")
		if (!hasSubExpressions) {
			collectEvalElements(expressionString, expressionString,
					isPredicate, animation, control, evaluationKeys,
					parseControls);
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
			final Map<String, String> values = new HashMap<String, String>();
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
				if (subExpression != null && text != null)
					values.put(subExpression, text);
			}
			if (hasSubExpressions) {
				// Replace each $...$ at its own position, so that a value
				// is never substituted into another one or into a longer
				// sub-expression containing the same text
				final StringBuffer sb = new StringBuffer();
				final Matcher m = PATTERN.matcher(expressionString);
				while (m.find()) {
					final String text = values.get(m.group());
					m.appendReplacement(sb,
							Matcher.quoteReplacement(text != null ? text : m
									.group()));
				}
				m.appendTail(sb);
				result = sb.toString();
			} else if (values.containsKey(expressionString)) {
				result = values.get(expressionString);
			}
		} else {
			// if (obj != null)
			// obj.setHasError(true);
			// addError(control, animation,
			// "An error occurred while evaluating expression\\predicate value: \""
			// + expressionString
			// + "\". Please check your expression\\predicate.");
		}

		return result;

	}

	private static boolean collectEvalElements(final String subexpression,
			final String key, final boolean isPredicate,
			final Animation animation, final BControl control,
			final Map<EvaluationElement, String> evaluationKeys,
			final boolean parseControls) {

		String parsedSubexpr = subexpression;

		if(parseControls)
			parsedSubexpr = parseControls(parsedSubexpr, control);
		EvaluationElement evalElement;
		try {
			evalElement = animation.getCachedEvalElement(parsedSubexpr,
					isPredicate);
			evaluationKeys.put(evalElement, key);
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		} catch (ProBException e) {
			return false;
		} catch (ProBParseException e) {
			// addError(control, animation, e.getMessage());
			return false;
		}

	}

	public static String parseControls(String expressionString,
			BControl control) {

		List<String> allControlIDs = control.getVisualization()
				.getAllBControlNames();

		// Search for control ids
		Pattern cPattern = Pattern.compile("(\\w+)");
		Matcher cMatcher = cPattern.matcher(expressionString);

		// Replace whole identifiers at their own position only, so that
		// e.g. control "x" does not change "x2" or "max", and a substituted
		// value is never searched again
		final StringBuffer sb = new StringBuffer();
		while (cMatcher.find()) {

			String controlID = cMatcher.group(1);
			String replacement = null;

			if (controlID.equals("this")) {

				replacement = control.getAttributeValue(
						AttributeConstants.ATTRIBUTE_CUSTOM).toString();

			} else if (allControlIDs.contains(controlID)) {

				replacement = control.getVisualization()
						.getBControl(controlID).getValueOfData();

			} else {
				// TODO: Return error if no control exists
			}

			cMatcher.appendReplacement(sb, Matcher
					.quoteReplacement(replacement != null ? replacement
							: controlID));
		}
		cMatcher.appendTail(sb);

		return sb.toString();

	}

	private static Collection<EvaluationStateElement> getExpressionValues(
			final BControl control, final Animation animation,
			final Collection<EvaluationElement> evalElements)
			throws ProBException {
		final State state = animation.getAnimator().getCurrentState();
		// TODO[DP, 11.04.2011] Add an animator to the parameters!
		final Collection<EvaluationStateElement> values = EvaluationGetValuesCommand
				.getValuesForExpressionsCached(state, evalElements);
		return values;
	}

	public static List<Operation> parseOperation(final String opName,
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
		} catch (BCompoundException e) {
		}

		return null;

	}

}
