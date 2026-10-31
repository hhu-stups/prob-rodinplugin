/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.Animator;
import de.prob.core.IAnimationListener;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationInsertFormulaCommand;
import de.prob.core.command.EvaluationInsertFormulaCommand.FormulaType;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;

public class Animation implements IAnimationListener {

	private Animator animator;

	// The state is set by the ProB thread (currentStateChanged) and read by the
	// UI thread (observers), so it is published via volatile fields and the
	// operations map is replaced, never modified
	private volatile Map<String, Operation> currentStateOperations;

	// filled by observers on the UI thread, read on the ProB thread
	private final Map<String, EvaluationElement> cachedEvalElements = new ConcurrentHashMap<String, EvaluationElement>();

	private volatile State currentState;

	private Visualization visualization;

	private Boolean observerCallBack = true;

	public Animation(Animator anim, Visualization visualization) {
		StaticListenerRegistry.registerListener((IAnimationListener) this);
		this.currentStateOperations = new HashMap<String, Operation>();
		this.animator = anim;
		this.visualization = visualization;
		this.visualization.setAnimation(this);
	}

	private void setNewState(State state) {
		final Map<String, Operation> operations = new HashMap<String, Operation>();
		for (Operation op : state.getEnabledOperations()) {
			operations.put(op.getName(), op);
		}
		currentStateOperations = operations;
		currentState = state;
	}

	@Override
	public void currentStateChanged(State currentState, Operation operation) {
		// set new state and remember old state, if possible
		setNewState(currentState);
		updateCachedExpressions(currentState);
		if (currentState.isInitialized()) {

			if (animator == null) {
				animator = Animator.getAnimator();
			}

			checkObserver();

		}

	}

	/**
	 * Get values for all used expressions. This should speed up the
	 * communication between ProB's core and Java. The result is not used, just
	 * to fill the caches.
	 * 
	 * @param currentState
	 */
	private void updateCachedExpressions(State currentState) {
		try {
			EvaluationGetValuesCommand.getValuesForExpressionsCached(
					currentState, new ArrayList<EvaluationElement>(
							cachedEvalElements.values()));
		} catch (ProBException e) {
			// TODO Log this
		}
	}

	private void collectAllBControls(List<BControl> allBControls,
			BControl control) {

		if (control.getChildrenArray().isEmpty())
			return;

		for (BControl bcontrol : control.getChildrenArray()) {
			allBControls.add(bcontrol);
			collectAllBControls(allBControls, bcontrol);
		}

	}

	public void checkObserver() {
		if (visualization.isRunning()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					List<BControl> allBControls = new ArrayList<BControl>();
					allBControls.add(visualization);
					collectAllBControls(allBControls, visualization);
					for (BControl c : allBControls)
						c.checkObserver(Animation.this);
				}
			});
		}
	}

	public State getState() {
		return currentState;
	}

	public Animator getAnimator() {
		return animator;
	}

	public Operation getCurrentStateOperation(String operation) {
		return currentStateOperations.get(operation);
	}

	public Visualization getVisualization() {
		return this.visualization;
	}

	public void unregister() {
		StaticListenerRegistry.unregisterListener((IAnimationListener) this);
	}

	public void setObserverCallBack(Boolean observerCallBack) {
		this.observerCallBack = observerCallBack;
	}

	public boolean isObserverCallBack() {
		return observerCallBack;
	}

	public EvaluationElement getCachedEvalElement(String expressionStr,
			boolean isPredicate) throws UnsupportedOperationException,
			ProBException, ProBParseException {
		EvaluationElement evalElement = cachedEvalElements.get(expressionStr);
		if (evalElement == null) {
			// TODO: exception handling ...
			evalElement = createPredicateExpressionElement(expressionStr,
					isPredicate);
			if (evalElement != null) {
				cachedEvalElements.put(expressionStr, evalElement);
			}
		}
		return evalElement;
	}

	private EvaluationElement createPredicateExpressionElement(
			String expressionStr, boolean isPredicate)
			throws UnsupportedOperationException, ProBException,
			ProBParseException {
		final EvaluationInsertFormulaCommand.FormulaType type = isPredicate ? FormulaType.PREDICATE
				: FormulaType.EXPRESSION;
		final EvaluationElement evaluationElement = EvaluationInsertFormulaCommand
				.insertFormula(animator, type, expressionStr);
		return evaluationElement;
	}

	public Map<String, Operation> getCurrentStateOperations() {
		return currentStateOperations;
	}
	
}
