package de.prob.ui.ltl;

import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.command.LtlCheckingCommand.Result;
import de.prob.prolog.term.ListPrologTerm;

public final class LtlMultiCheckingFinishedListener extends
		LtlCheckingFinishedListener {
	private final List<String> formulas;

	private int formulaCount = 0;
	private boolean counterExampleFound = false;

	public LtlMultiCheckingFinishedListener(final Shell shell,
			List<String> formulas) {
		super(shell);
		this.formulas = formulas;
	}

	@Override
	public synchronized void done(final IJobChangeEvent event) {
		Job job = event.getJob();

		if (job instanceof LtlCheckingJob) {
			LtlCheckingJob ltlJob = (LtlCheckingJob) job;

			if (!ltlJob.isAnErrorOccurred()) {
				final Result result = ltlJob.getModelCheckingResult();
				formulaCount += 1;

				switch (result.getStatus()) {
				case counterexample:
					counterExampleFound = true;

					final Runnable runnable = new Runnable() {
						@Override
						public void run() {
							ListPrologTerm counterExample = result
									.getCounterexample();
							if (counterExample != null) {
								showCounterexampleInView(result);
							}
						}
					};

					shell.getDisplay().asyncExec(runnable);

					break;
				}

				if (formulaCount == formulas.size()) {
					final Runnable runnable = new Runnable() {
						@Override
						public void run() {
							final String message;
							final String title;
							final int displayType;

							if (counterExampleFound) {
								title = LtlStrings.ltlResultCounterexampleTitle;
								message = LtlStrings.ltlMultiResultCounterexampleMessage;
								displayType = MessageDialog.ERROR;
							} else {
								title = LtlStrings.ltlResultOkTitle;
								message = LtlStrings.ltlMultiResultOkMessage;
								displayType = MessageDialog.INFORMATION;
							}

							MessageDialog.open(displayType, shell, title,
									message, SWT.NONE);
						}
					};

					shell.getDisplay().asyncExec(runnable);
				}
			}
		}
	}
}