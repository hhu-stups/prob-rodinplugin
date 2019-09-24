/** 
 * (c) 2013 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.core.Animator;
import de.prob.core.command.*;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.OperationInfo;
import de.prob.exceptions.ProBException;
import de.prob.unicode.UnicodeTranslator;

public class CustomPreconditionInputDialog extends InputDialog {

	private final Animator animator;
	private final Operation op;

	public CustomPreconditionInputDialog(final Shell parentShell, Operation op, Animator a) {
		super(parentShell, "Execute with additional Guard Constraint", getMenuText(op,a),
				"", new EventBInputValidator());
		this.op = op;
		animator = a;
	}

	private static String getMenuText(Operation op, Animator animator) {
		StringBuffer sb = new StringBuffer();
		sb.append("Enter Guard Constraint to be added to the Event \"");
		sb.append(op.getName());
		sb.append("\" before execution.");
		sb.append("\nParameters are: ");
		sb.append(getOperationParams(op,animator));
		if (op.getArguments().size()>0) {
			sb.append("\nValues are:");
			for (String arg : op.getArguments()) {
			    sb.append(" ");
			    sb.append(arg); // these are the parameter values not the parameter names !!!
			}
		}

		// sb.append("\nYou may use the parameters: ");

		// List<String> arguments = op.getArguments();
		// for (int i = 0; i < arguments.size() - 1; i++) {
		// sb.append(arguments.get(i));
		// sb.append("' ");
		// }
		// sb.append(arguments.get(arguments.size() - 1));

		return sb.toString();
	}

	private Operation getCustomOperation() {
		try {
			Operation customOp = GetOperationByPredicateCommand2.getOperation(
					animator, op.getSource(),
					Operation.getInternalName(op.getName()),
					UnicodeTranslator.toUnicode(getValue()));

			ExecuteOperationCommand.executeOperation(animator, customOp);
		} catch (ProBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static Operation getOperation(Operation op) {
		
		CustomPreconditionInputDialog osd = new CustomPreconditionInputDialog(
				new Shell(), op, Animator.getAnimator());
		if (osd.open() == InputDialog.OK)
			return osd.getCustomOperation();
		else
			return null;
	}
	
    // copy from Paramter OperationTableViewer
	private static List<String> getOperationParams(final Operation op, Animator animator) {
		Collection<OperationInfo> infos = null;
		try {
			infos = GetOperationNamesCommand.getNames(animator);
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
		final OperationInfo params = infos == null ? null : OperationInfo
				.getParams(op.getName(), infos);
		final List<String> result;
		if (params != null) {
			result = params.getParameters();
		} else {
			// If we cannot see the parameter names, we just use the operation's
			// number of arguments and use empty titles
			final int numArgs = op.getArguments().size();
			result = new ArrayList<String>(numArgs);
			for (int i = 0; i < numArgs; i++) {
				result.add("");
			}
		}
		return result;
	}
	

}
