/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.ltl;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.prob.core.command.LtlCheckingCommand.Result;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.logging.Logger;
import de.prob.prolog.term.ListPrologTerm;

public class LtlCheckingFinishedListener extends JobChangeAdapter {
	protected final Shell shell;

	public LtlCheckingFinishedListener(final Shell shell) {
		this.shell = shell;
	}

	@Override
	public void done(final IJobChangeEvent event) {
		Job job = event.getJob();
		if (job instanceof LtlCheckingJob) {
			LtlCheckingJob ltlJob = (LtlCheckingJob) job;
			if (!ltlJob.isAnErrorOccurred()) {
				showResult(ltlJob.getModelCheckingResult());
			}
		} else {
			final String message = "The job has a wrong type. Expected LtlCheckingJob but got "
					+ job.getClass();
			Logger.notifyUserWithoutBugreport(message);
		}
	}

	protected void showCounterexampleInView(final Result modelCheckingResult) {
		final CounterExample counterExample = new CounterExample(
				modelCheckingResult);
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		try {
			final CounterExampleViewPart counterExampleView = (CounterExampleViewPart) workbenchPage
					.showView(CounterExampleViewPart.ID);
			counterExampleView.addCounterExample(counterExample);
		} catch (PartInitException e) {
			Logger.notifyUser("Failed to show the LTL view.", e);
		}
	}

	private void showResult(final Result modelCheckingResult) {
		Logger.assertProB("modelCheckingResult != null",
				modelCheckingResult != null);

		final String message;
		final String title;
		final int displayType;
		switch (modelCheckingResult.getStatus()) {
		case incomplete:
			title = LtlStrings.ltlResultIncompleteTitle;
			message = LtlStrings.ltlResultIncompleteTitle;
			displayType = MessageDialog.WARNING;
			break;
		case ok:
			title = LtlStrings.ltlResultOkTitle;
			message = LtlStrings.ltlResultOkMessage;
			displayType = MessageDialog.INFORMATION;
			break;
		case counterexample:
			title = LtlStrings.ltlResultCounterexampleTitle;
			// if (modelCheckingResult.getPathType() == PathType.INFINITE) {
			// message = LtlStrings.ltlResultCounterexampleMessage
			// + LtlStrings.ltlLoopAdvice;
			// } else {
			// message = LtlStrings.ltlResultCounterexampleMessage;
			// }
			message = LtlStrings.ltlResultCounterexampleMessage;
			displayType = MessageDialog.ERROR;
			break;
		case nostart:
			title = LtlStrings.ltlResultNoStartTitle;
			message = LtlStrings.ltlResultNoStartMessage;
			displayType = MessageDialog.WARNING;
			break;
		case typeerror:
			Logger.notifyUser("Internal error: Type-Checking the LTL formula failed");
			return;
		default:
			Logger.notifyUser("Uncovered case in LTL Result Type: "
					+ modelCheckingResult);
			return;
		}

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				MessageDialog
						.open(displayType, shell, title, message, SWT.NONE);

				ListPrologTerm counterExample = modelCheckingResult
						.getCounterexample();

				if (counterExample != null) {
					showCounterexampleInView(modelCheckingResult);
				}
			}
		};

		shell.getDisplay().asyncExec(runnable);
	}
}
