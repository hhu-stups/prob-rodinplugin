/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.wizard.WizardExecuteScheduler;
import de.bmotionstudio.gef.editor.util.BMSUtil;
import de.prob.core.Animator;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.GetCurrentStateIdCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;

public class ExecuteAnimationScript extends SchedulerEvent {

	public static String ID = "de.bmotionstudio.gef.editor.scheduler.ExecuteAnimationScript";

	private List<AnimationScriptObject> list;

	private transient Random random;

	public ExecuteAnimationScript() {
		this.list = new ArrayList<AnimationScriptObject>();
	}

	@Override
	public void execute(final Animation animation, final BControl control) {

		// new Thread(new Runnable() {
		// public void run() {

		// The animator
		Animator animator = animation.getAnimator();

		// Iterate schedulers
		for (AnimationScriptObject obj : list) {

			// First evaluate predicate (predicate field)
			// If true (execute operation sequence)
			if (Boolean.valueOf(BMSUtil.parsePredicate(obj.getPredicate(),
					control, animation))) {

				for (AnimationScriptStep step : obj.getSteps()) {

					try {

						String currentState = GetCurrentStateIdCommand
								.getID(animator);

						List<Operation> operations = BMSUtil.parseOperation(
								step.getCommand(), step.getParameter(),
								step.getMaxrandom(), animation, currentState,
								control);

						if (operations != null) {

							Operation executeOp;

							if (step.isRandom()) {
								executeOp = operations.get(getRandomizer()
										.nextInt(operations.size()));
							} else {
								executeOp = operations.get(0);
							}

							ExecuteOperationCommand.executeOperation(animator,
									executeOp);

						} else {
							// TODO: error message!?
						}

					} catch (ProBException e) {
						e.printStackTrace();
					}

				}

				return;

			}

		}
		// }

		// }).start();

	}

	@Override
	public SchedulerWizard getWizard(Shell shell, BControl bcontrol) {
		return new WizardExecuteScheduler(shell, bcontrol, this);
	}

	public ExecuteAnimationScript clone() throws CloneNotSupportedException {
		ExecuteAnimationScript nse = (ExecuteAnimationScript) super.clone();
		List<AnimationScriptObject> list = new ArrayList<AnimationScriptObject>();
		for (AnimationScriptObject po : this.getList()) {
			list.add(po.clone());
		}
		nse.setList(list);
		return nse;
	}

	public void setList(final List<AnimationScriptObject> list) {
		this.list = list;
	}

	public List<AnimationScriptObject> getList() {
		if (this.list == null) {
			this.list = new ArrayList<AnimationScriptObject>();
		}
		return list;
	}

	private Random getRandomizer() {
		if (random == null)
			random = new Random();
		return random;
	}

	// private boolean checkCallBack(ArrayList<ObserverCallBackObject>
	// callBackList) {
	//
	// Boolean callback = false;
	//
	// for (ObserverCallBackObject callBackObj : callBackList) {
	//
	// BControl control = callBackObj.getControl();
	// Observer observer = control
	// .getObserver(callBackObj.getObserverID());
	//
	// Boolean oCallBack;
	//
	// if (observer != null) {
	// oCallBack = observer.isCallBack();
	// } else {
	// oCallBack = true;
	// }
	//
	// if (!oCallBack) {
	// callback = false;
	// } else {
	// callback = true;
	// }
	//
	// }
	//
	// return callback;
	//
	// }

}
