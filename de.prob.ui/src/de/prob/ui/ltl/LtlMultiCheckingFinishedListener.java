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

	// number of finished jobs, including failed and cancelled ones
	private int formulaCount = 0;
	private int okCount = 0;
	private int counterExampleCount = 0;
	private int incompleteCount = 0;
	// no initialisation found, errors and cancelled jobs
	private int notCheckedCount = 0;

	public LtlMultiCheckingFinishedListener(final Shell shell,
			List<String> formulas) {
		super(shell);
		this.formulas = formulas;
	}

	@Override
	public synchronized void done(final IJobChangeEvent event) {
		Job job = event.getJob();

		if (!(job instanceof LtlCheckingJob))
			return;
		LtlCheckingJob ltlJob = (LtlCheckingJob) job;
		formulaCount += 1;

		final Result result = ltlJob.isAnErrorOccurred() ? null : ltlJob
				.getModelCheckingResult();
		if (result == null) {
			// an error (the user has been informed) or a cancelled job
			notCheckedCount++;
		} else {
			switch (result.getStatus()) {
			case ok:
				okCount++;
				break;
			case incomplete:
				incompleteCount++;
				break;
			case counterexample:
				counterExampleCount++;
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						ListPrologTerm counterExample = result
								.getCounterexample();
						if (counterExample != null) {
							showCounterexampleInView(result);
						}
					}
				});
				break;
			default:
				notCheckedCount++;
				break;
			}
		}

		if (formulaCount == formulas.size()) {
			showSummary();
		}
	}

	private void showSummary() {
		final String title;
		final int displayType;
		final StringBuilder message = new StringBuilder();
		if (counterExampleCount > 0) {
			title = LtlStrings.ltlResultCounterexampleTitle;
			message.append(LtlStrings.ltlMultiResultCounterexampleMessage);
			displayType = MessageDialog.ERROR;
		} else if (okCount == formulaCount) {
			title = LtlStrings.ltlResultOkTitle;
			message.append(LtlStrings.ltlMultiResultOkMessage);
			displayType = MessageDialog.INFORMATION;
		} else {
			// no counterexample, but not every formula was fully checked
			title = LtlStrings.ltlResultIncompleteTitle;
			message.append(LtlStrings.ltlMultiResultIncompleteMessage);
			displayType = MessageDialog.WARNING;
		}
		message.append("\n\n").append(formulaCount).append(" formula(s): ")
				.append(okCount).append(" ok, ").append(counterExampleCount)
				.append(" with counterexample, ").append(incompleteCount)
				.append(" incomplete, ").append(notCheckedCount)
				.append(" not checked (error, no initialisation or cancelled).");

		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!shell.isDisposed()) {
					MessageDialog.open(displayType, shell, title,
							message.toString(), SWT.NONE);
				}
			}
		});
	}
}
