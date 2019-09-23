/** 
 * (c) 2013 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.core.Animator;
import de.prob.core.command.*;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.unicode.UnicodeTranslator;

public class CustomPreconditionInputDialog extends InputDialog {

	private final Animator animator;
	private final Operation op;

	public CustomPreconditionInputDialog(final Shell parentShell, Operation op) {
		super(parentShell, "Execute with additional Guard", getMenuText(op),
				"", new EventBInputValidator());
		this.op = op;
		animator = Animator.getAnimator();
	}

	private static String getMenuText(Operation op) {
		StringBuffer sb = new StringBuffer();
		sb.append("Enter Guard to be added to the Event \"");
		sb.append(op.getName());
		sb.append("\" before execution.");
		if (op.getArguments().size()>0) {
			sb.append("\nParameters are:");
			for (String arg : op.getArguments()) {
			    sb.append(" ");
			    sb.append(arg);
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
				new Shell(), op);
		if (osd.open() == InputDialog.OK)
			return osd.getCustomOperation();
		else
			return null;
	}

}
