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

import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.model.VisualizationView;
import de.prob.core.Animator;
import de.prob.core.IAnimationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationInsertFormulaCommand;
import de.prob.core.command.EvaluationInsertFormulaCommand.FormulaType;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;

public class Animation implements IAnimationListener, ILifecycleListener {

	private Animator animator;

	private final Map<String, Operation> currentStateOperations;

	private final Map<String, EvaluationElement> cachedEvalElements = new HashMap<String, EvaluationElement>();

	private State currentState;

	private Boolean observerCallBack = true;

	private Simulation simulation;

	public Animation(Animator anim, Simulation simulation) {
		StaticListenerRegistry.registerListener((IAnimationListener) this);
		StaticListenerRegistry.registerListener((ILifecycleListener) this);
		this.currentStateOperations = new HashMap<String, Operation>();
		this.animator = anim;
		this.simulation = simulation;
	}

	private void setNewState(State state) {
		currentState = state;
		currentStateOperations.clear();
		for (Operation op : state.getEnabledOperations()) {
			this.currentStateOperations.put(op.getName(), op);
		}
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
					currentState, cachedEvalElements.values());
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

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				System.out.println("CHECK OBSERVER OF "
						+ simulation.getProjectFile().getName());

				for (VisualizationView visView : simulation
						.getVisualizationViews().values()) {

					Visualization visualization = visView.getVisualization();
					List<BControl> allBControls = new ArrayList<BControl>();
					allBControls.add(visualization);
					collectAllBControls(allBControls, visualization);
					for (BControl c : allBControls)
						c.checkObserver(Animation.this);

				}

			}

		});

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

	public void unregister() {
		StaticListenerRegistry.unregisterListener((IAnimationListener) this);
		// StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
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
		final EvaluationElement evalElement;
		if (cachedEvalElements.containsKey(expressionStr)) {
			evalElement = cachedEvalElements.get(expressionStr);
		} else {
			// TODO: exception handling ...
			evalElement = createPredicateExpressionElement(expressionStr,
					isPredicate);
			cachedEvalElements.put(expressionStr, evalElement);
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

	@Override
	public void reset() {
		if (simulation.isRunning()) {
			System.out.println("STOP SIMULATION "
					+ simulation.getProjectFile().getName());
			simulation.stop();
		}
	}
	
}
