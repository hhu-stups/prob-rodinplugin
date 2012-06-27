package de.bmotionstudio.gef.editor.scheduler;

import java.util.ArrayList;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.BindingObject;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.wizard.WizardExecuteOperationByPredicateMulti;
import de.bmotionstudio.gef.editor.util.BMSUtil;

public class ExecuteOperationByPredicateMulti extends SchedulerEvent {

	public static String ID = "de.bmotionstudio.gef.editor.scheduler.ExecuteOperationByPredicateMulti";

	private ArrayList<BindingObject> operationList;

	public ExecuteOperationByPredicateMulti() {
		this.operationList = new ArrayList<BindingObject>();
	}

	@Override
	public void execute(final Animation animation, final BControl control) {

		for (BindingObject op : operationList) {

			String bolValue = "true";
			String executePredicate = ((PredicateOperation) op)
					.getExecutePredicate();

			if (executePredicate.length() > 0) {
				bolValue = BMSUtil.parsePredicate(executePredicate, control,
						animation);
			}

			if (Boolean.valueOf(bolValue)) { // If true
				executeOperation(animation, (PredicateOperation) op, control);
				break; // Execute only the first operation which is true
			}

		}

	}

	private void executeOperation(final Animation animation,
			final PredicateOperation predicateOperation, final BControl control) {
		ExecuteOperationByPredicate executeCmd = new ExecuteOperationByPredicate();
		executeCmd.setPredicateOperation(predicateOperation);
		executeCmd.execute(animation, control);
	}

	@Override
	public SchedulerWizard getWizard(BControl bcontrol) {
		return new WizardExecuteOperationByPredicateMulti(bcontrol, this);
	}

	public void setOperationList(ArrayList<BindingObject> operationList) {
		this.operationList = operationList;
	}

	public ArrayList<BindingObject> getOperationList() {
		if (operationList == null)
			operationList = new ArrayList<BindingObject>();
		return operationList;
	}

	public ExecuteOperationByPredicateMulti clone()
			throws CloneNotSupportedException {
		ExecuteOperationByPredicateMulti nse = (ExecuteOperationByPredicateMulti) super
				.clone();
		ArrayList<BindingObject> opList = new ArrayList<BindingObject>();
		for (BindingObject p : getOperationList()) {
			opList.add(((PredicateOperation) p).clone());
		}
		nse.setOperationList(opList);
		return nse;
	}

}
