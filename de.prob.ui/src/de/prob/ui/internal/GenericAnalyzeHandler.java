package de.prob.ui.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.ProBCommandJob;
import de.prob.core.ProBJobFinishedListener;
import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.core.command.ISimpleTextCommand;
import de.prob.logging.Logger;

public abstract class GenericAnalyzeHandler extends AbstractHandler {

	public class GenericAnalyzeFinishedHandler extends ProBJobFinishedListener {

		private final Shell shell;

		public GenericAnalyzeFinishedHandler(Shell shell) {
			this.shell = shell;
		}

		@Override
		protected void showResult(IComposableCommand command, Animator animator) {
			if (command instanceof ISimpleTextCommand) {
				ISimpleTextCommand textcommand = (ISimpleTextCommand) command;
				String text = textcommand.getResultingText();
				display(text);
			} else {
				Logger.notifyUser(
						"The invoked command did not implement the correct interface. Please report this bug.",
						new CommandException(
								"Error in "
										+ command.getClass()
										+ ". Class should implement ISimpleTextCommand."));
			}
		}

		private void display(final String text) {

			final TitleAreaDialog titleAreaDialog = new TitleAreaDialog(shell) {

				@Override
				protected Control createDialogArea(Composite parent) {

					setTitle(name);

					Composite composite = (Composite) super
							.createDialogArea(parent);
					Text t = new Text(composite, SWT.WRAP | SWT.MULTI | SWT.BORDER
							| SWT.H_SCROLL | SWT.V_SCROLL);
					GridData gridData =
					      new GridData(
					        GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
					    gridData.grabExcessVerticalSpace = true;

					    t.setLayoutData(gridData);


					t.append(text);
					return composite;
				}

			};

			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					titleAreaDialog.open();
				}
			};
			shell.getDisplay().asyncExec(runnable);
		}
	}

	private final ISimpleTextCommand command;
	private final String name;

	public GenericAnalyzeHandler(ISimpleTextCommand command, String name) {
		this.command = command;
		this.name = name;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		Animator animator = Animator.getAnimator();
		if (animator.isMachineLoaded()) {
			final ProBCommandJob job = new ProBCommandJob(name, animator,
					command);
			GenericAnalyzeFinishedHandler finishedHandler = new GenericAnalyzeFinishedHandler(
					shell);
			job.setUser(true);
			job.addJobChangeListener(finishedHandler);
			job.schedule();

		} else {
			Logger.notifyUser("No ProB animation running. This is a bug. Please submit a report. Error in declaraion of class "+this.getClass());
		}
		return null;
	}

}
