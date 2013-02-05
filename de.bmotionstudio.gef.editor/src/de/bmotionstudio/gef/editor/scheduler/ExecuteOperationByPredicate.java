/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler;

import java.util.List;
import java.util.Random;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.wizard.WizardExecuteOperationByPredicate;
import de.bmotionstudio.gef.editor.util.BMSUtil;
import de.prob.core.Animator;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.GetCurrentStateIdCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;

public class ExecuteOperationByPredicate extends SchedulerEvent {

	public static String ID = "de.bmotionstudio.gef.editor.scheduler.ExecuteOperationByPredicate";

	private PredicateOperation predicateOperation;

	private transient Random random;

	@Override
	public void execute(final Animation animation, final BControl control) {

		new Thread(new Runnable() {
			public void run() {

				try {
					// The animator
					final Animator animator = animation.getAnimator();

					String currentState = GetCurrentStateIdCommand
							.getID(animator);

					List<Operation> operations = BMSUtil.parseOperation(
							predicateOperation.getOperationName(),
							predicateOperation.getPredicate(),
							predicateOperation.getMaxrandom(), animation,
							currentState, control);

					if (operations != null) {

						Operation executeOp;

						if (predicateOperation.isRandom()) {
							executeOp = operations.get(getRandomizer().nextInt(
									operations.size()));
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

		}).start();

	}

	@Override
	public SchedulerWizard getWizard(BControl bcontrol) {
		return new WizardExecuteOperationByPredicate(bcontrol, this);
	}

	public void setPredicateOperation(PredicateOperation predicateOperation) {
		this.predicateOperation = predicateOperation;
	}

	public PredicateOperation getPredicateOperation() {
		if (this.predicateOperation == null)
			this.predicateOperation = new PredicateOperation();
		return this.predicateOperation;
	}

	public ExecuteOperationByPredicate clone()
			throws CloneNotSupportedException {
		ExecuteOperationByPredicate nse = (ExecuteOperationByPredicate) super
				.clone();
		nse.setPredicateOperation(predicateOperation.clone());
		return nse;
	}

	private Random getRandomizer() {
		if (random == null)
			random = new Random();
		return random;
	}

}
