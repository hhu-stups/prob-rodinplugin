/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.stateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationGetValuesCommand.EvaluationResult;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.ui.stateview.statetree.StateDependendElement;
import de.prob.ui.stateview.statetree.StateTreeExpression;
import de.prob.ui.stateview.statetree.StateTreeVariable;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * Shows the complete value of the selected variable or formula of the state
 * view in the current state. The view itself shows long values in one line
 * and ProB truncates long formula values.
 */
public class ShowFullValueHandler extends AbstractHandler {

	private static final String RODIN_FONT_KEY = "org.rodinp.keyboard.textFont";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		// the selection of the tree the context menu belongs to; the
		// workbench selection may not yet reflect the right-clicked row
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null)
			selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		final Object element = ((IStructuredSelection) selection)
				.getFirstElement();
		if (!(element instanceof StaticStateElement))
			return null;
		final StaticStateElement sse = (StaticStateElement) element;

		final State state = Animator.getAnimator().getCurrentState();
		if (state == null) {
			MessageDialog.openInformation(shell, "ProB",
					"There is no current state.");
			return null;
		}

		final String value;
		try {
			value = getFullValue(sse, state);
		} catch (ProBException e) {
			e.notifyUserOnce();
			return null;
		}

		new FullValueDialog(shell, sse.getLabel(),
				value != null ? value : "(no value in the current state)")
				.open();
		return null;
	}

	private static String getFullValue(final StaticStateElement element,
			final State state) throws ProBException {
		if (element instanceof StateTreeExpression) {
			// ask again without ProB's truncation of long values
			final EvaluationResult result = EvaluationGetValuesCommand
					.getFullValue(state,
							((StateTreeExpression) element).getStaticElement());
			return result == null || !result.isActive() ? null : result
					.getText();
		} else if (element instanceof StateTreeVariable) {
			// variable values are not truncated by ProB
			final StateDependendElement sde = element.getValue(state);
			return sde == null ? null : sde.getValue();
		} else {
			return null;
		}
	}

	private static class FullValueDialog extends Dialog {
		private final String label;
		private final String value;

		FullValueDialog(final Shell parent, final String label,
				final String value) {
			super(parent);
			this.label = label;
			this.value = value;
		}

		@Override
		protected boolean isResizable() {
			return true;
		}

		@Override
		protected void configureShell(final Shell shell) {
			super.configureShell(shell);
			shell.setText("Value of " + label);
		}

		@Override
		protected Control createDialogArea(final Composite parent) {
			final Composite area = (Composite) super.createDialogArea(parent);
			final Text text = new Text(area, SWT.MULTI | SWT.WRAP
					| SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER);
			text.setFont(JFaceResources.getFontRegistry().get(RODIN_FONT_KEY));
			text.setText(value);
			final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true,
					true);
			layoutData.widthHint = 600;
			layoutData.heightHint = 300;
			text.setLayoutData(layoutData);
			return area;
		}

		@Override
		protected void createButtonsForButtonBar(final Composite parent) {
			createButton(parent, IDialogConstants.CLOSE_ID,
					IDialogConstants.CLOSE_LABEL, true);
		}

		@Override
		protected void buttonPressed(final int buttonId) {
			if (buttonId == IDialogConstants.CLOSE_ID)
				close();
			else
				super.buttonPressed(buttonId);
		}
	}
}
