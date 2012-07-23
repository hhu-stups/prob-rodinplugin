/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverListenOperationByPredicate;
import de.bmotionstudio.gef.editor.scheduler.PredicateOperation;
import de.bmotionstudio.gef.editor.util.BMSUtil;
import de.prob.core.Animator;
import de.prob.core.command.GetOperationByPredicateCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

public class ListenOperationByPredicate extends Observer {

	public static String ID = "de.bmotionstudio.gef.editor.observer.ListenOperationByPredicate";

	private ArrayList<PredicateOperation> list = new ArrayList<PredicateOperation>();
	private transient List<String> setAttributes;

	public ListenOperationByPredicate() {
		this(new ArrayList<PredicateOperation>());
	}

	public ListenOperationByPredicate(ArrayList<PredicateOperation> list) {
		super();
		this.setAttributes = new ArrayList<String>();
		this.list = list;
	}

	protected Object readResolve() {
		this.setAttributes = new ArrayList<String>();
		return super.readResolve();
	}

	@Override
	public void check(Animation animation, BControl control) {

		this.setAttributes.clear();

		State state = animation.getState();
		Animator animator = animation.getAnimator();

		for (PredicateOperation pop : getList()) {

			if (pop.getAttribute() == null) {
				pop.setAttribute(AttributeConstants.ATTRIBUTE_ENABLED);
			}

			if (pop.getValue() == null) {
				pop.setValue(true);
			}

			String fPredicate = pop.getPredicate();
			String fOpName = pop.getOperationName();

			if (fOpName != null && fPredicate != null) {

				if (animation.getCurrentStateOperations().containsKey(fOpName)) {

					if (fPredicate.length() > 0) {
						fPredicate = BMSUtil.parseControls(fPredicate, control);
					}

					try {
						if (fPredicate.equals(""))
							fPredicate = "1=1";
						Operation operation = GetOperationByPredicateCommand
								.getOperation(animator, state.getId(), fOpName,
										fPredicate);
						if (operation != null) { // Operation enabled

							String attributeID = pop.getAttribute();

							AbstractAttribute attributeObj = control
									.getAttribute(attributeID);

							Object attributeVal = pop.getValue();

							if (pop.isExpressionMode()) {
								String strAtrVal = BMSUtil.parseExpression(
										attributeVal.toString(), control,
										animation);
								String er = attributeObj.validateValue(
										strAtrVal, null);
								if (er != null) {
									// addError(
									// control,
									// animation,
									// "You selected "
									// + attributeObj.getName()
									// +
									// " as attribute. There is a problem with your value: "
									// + strAtrVal + " - Reason: "
									// + er);
									pop.setHasError(true);
								} else {
									attributeVal = attributeObj
											.unmarshal(strAtrVal);
								}
							}

							if (!pop.hasError()) {
								Object oldAttrVal = control
										.getAttributeValue(attributeID);
								if (!oldAttrVal.equals(attributeVal)) {
									control.setAttributeValue(attributeID,
											attributeVal, true, false);
								}
							}

							setAttributes.add(attributeID);

						}
					} catch (ProBException e) {
						// addError(control, animation,
						// "An error occurred while evaluating. Reason: "
						// + e.getMessage());
					} catch (BException e) {
						// addError(control, animation, "Parsing error in: "
						// + fPredicate + " Reason: " + e.getMessage());
					}
				}

			}

		}

		// Restore attribute values
		for (PredicateOperation obj : list) {
			if (!setAttributes.contains(obj.getAttribute())) {
				AbstractAttribute attributeObj = control.getAttribute(obj
						.getAttribute());
				Object oldAttrVal = control.getAttributeValue(obj
						.getAttribute());
				if (!oldAttrVal.equals(attributeObj.getInitValue())) {
					control.restoreDefaultValue(attributeObj.getID());
				}
			}
		}

	}

	@Override
	public ObserverWizard getWizard(BControl control) {
		return new WizardObserverListenOperationByPredicate(control, this);
	}

	public void setList(ArrayList<PredicateOperation> list) {
		this.list = list;
	}

	public ArrayList<PredicateOperation> getList() {
		if (this.list == null)
			this.list = new ArrayList<PredicateOperation>();
		return this.list;
	}

	public void addPredicateOperation(PredicateOperation predicateOperation) {
		getList().add(predicateOperation);
	}

	public void removePredicateOperation(PredicateOperation predicateOperation) {
		getList().remove(predicateOperation);
	}

	// public Boolean removePredicateOperationByUniqueID(String uniqueID) {
	// for (PredicateOperation po : this.list) {
	// String cuID = po.getUniqueID();
	// if (cuID != null) {
	// if (cuID.equals(uniqueID)) {
	// this.list.remove(po);
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	public Observer clone() throws CloneNotSupportedException {
		ListenOperationByPredicate clone = (ListenOperationByPredicate) super
				.clone();
		ArrayList<PredicateOperation> clonedList = new ArrayList<PredicateOperation>();
		for (PredicateOperation pop : this.getList()) {
			clonedList.add(pop.clone());
		}
		clone.setList(clonedList);
		return clone;
	}

}
