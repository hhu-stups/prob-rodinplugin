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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
 * view in the current or previous state, in one line or nested over several
 * lines. The view itself shows long values in one line and ProB truncates
 * long formula values.
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

		final Animator animator = Animator.getAnimator();
		final State state = animator.getCurrentState();
		if (state == null) {
			MessageDialog.openInformation(shell, "ProB",
					"There is no current state.");
			return null;
		}
		// the state before the last operation, as in the state view's
		// "Previous State" column; null in the first state
		final State previous = animator.getHistory().getState(-1);

		final String value, previousValue;
		try {
			value = getFullValue(sse, state);
			previousValue = previous == null ? null : getFullValue(sse,
					previous);
		} catch (ProBException e) {
			e.notifyUserOnce();
			return null;
		}

		new FullValueDialog(shell, sse.getLabel(), value, previous != null,
				previousValue).open();
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
		private final boolean hasPrevious;
		private final String previousValue;

		private Text text;
		private Button previousButton;
		private Button nestedButton;

		FullValueDialog(final Shell parent, final String label,
				final String value, final boolean hasPrevious,
				final String previousValue) {
			super(parent);
			this.label = label;
			this.value = value;
			this.hasPrevious = hasPrevious;
			this.previousValue = previousValue;
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

			final SelectionAdapter update = new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					// radio buttons also report being deselected
					if (((Button) e.widget).getSelection())
						updateText();
				}
			};

			final Composite stateRow = createRadioRow(area, "State:");
			final Button currentButton = createRadio(stateRow, "Current",
					update);
			currentButton.setSelection(true);
			previousButton = createRadio(stateRow, previousLabel(), update);
			previousButton.setEnabled(hasPrevious);

			final Composite layoutRow = createRadioRow(area, "Layout:");
			createRadio(layoutRow, "One line", update).setSelection(true);
			nestedButton = createRadio(layoutRow, "Nested", update);

			text = new Text(area, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL
					| SWT.READ_ONLY | SWT.BORDER);
			text.setFont(JFaceResources.getFontRegistry().get(RODIN_FONT_KEY));
			final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true,
					true);
			layoutData.widthHint = 600;
			layoutData.heightHint = 300;
			text.setLayoutData(layoutData);
			text.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent e) {
					// the nested layout depends on the width
					if (nestedButton.getSelection())
						updateText();
				}
			});
			updateText();
			return area;
		}

		private String previousLabel() {
			if (!hasPrevious)
				return "Previous (none)";
			return equal(value, previousValue) ? "Previous (unchanged)"
					: "Previous";
		}

		private static boolean equal(final String a, final String b) {
			return a == null ? b == null : a.equals(b);
		}

		private static Composite createRadioRow(final Composite parent,
				final String title) {
			// each row is its own composite, so its radio buttons form a
			// separate group
			final Composite row = new Composite(parent, SWT.NONE);
			final RowLayout layout = new RowLayout(SWT.HORIZONTAL);
			layout.center = true;
			layout.marginLeft = 0;
			row.setLayout(layout);
			new Label(row, SWT.NONE).setText(title);
			return row;
		}

		private static Button createRadio(final Composite row,
				final String title, final SelectionAdapter listener) {
			final Button button = new Button(row, SWT.RADIO);
			button.setText(title);
			button.addSelectionListener(listener);
			return button;
		}

		private void updateText() {
			final boolean showPrevious = previousButton.getSelection();
			final String shown = showPrevious ? previousValue : value;
			String result;
			if (shown == null) {
				result = showPrevious ? "(no value in the previous state)"
						: "(no value in the current state)";
			} else if (nestedButton.getSelection()) {
				result = ValuePrettyPrinter.prettyPrint(shown,
						charactersPerLine());
			} else {
				result = shown;
			}
			text.setText(result);
		}

		/**
		 * @return how many characters fit into a line of the text field
		 */
		private int charactersPerLine() {
			final GC gc = new GC(text);
			final double charWidth;
			try {
				charWidth = gc.getFontMetrics().getAverageCharacterWidth();
			} finally {
				gc.dispose();
			}
			int width = text.getClientArea().width;
			if (width <= 0)
				width = 600; // not laid out yet, the width hint
			// some margin for the scroll bar and wider symbols
			return Math.max(20, (int) (width / Math.max(1, charWidth)) - 4);
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
