/** 
 * (c) 2009-20 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.Animator;
import de.prob.core.ProblemHandler;
import de.prob.core.command.ComputeCoverageCommand;
import de.prob.core.command.ComputeCoverageCommand.ComputeCoverageResult;
import de.prob.core.command.GetTraceCommand;
import de.prob.core.command.ModelCheckingCommand.Result;
import de.prob.core.command.ReplayTraceCommand;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.ProbUiPlugin;

public class ModelCheckingFinishedListener extends JobChangeAdapter {

	private static final String SEPARATOR_LINE = "===================================================";
	private static final String PLUGIN_ID = ProbUiPlugin.PLUGIN_ID;
	private final Shell shell;
	private final Animator animator = Animator.getAnimator();

	public ModelCheckingFinishedListener(final Shell shell) {
		this.shell = shell;
	}

	@Override
	public void done(final IJobChangeEvent event) {
		super.done(event);
		Job job = event.getJob();
		if (job instanceof ModelCheckingJob) {
			ModelCheckingJob ccJob = (ModelCheckingJob) job;
			showResult(ccJob.getModelCheckingResult());
		} else {
			final String message = "The job has a wrong type. Expected ModelCheckingJob but got "
					+ job.getClass();
			Logger.notifyUserWithoutBugreport(message);
		}
	}

	private void showResult(final Result modelCheckingResult) {
		MultiStatus status = null;
		;
		try {
			status = createResult(modelCheckingResult);
		} catch (ProBException e) {
			e.notifyUserOnce();
			// No way to recover from this, but the User has been informed
		}
		displayResult(status);
	}

	private MultiStatus createResult(final Result results) throws ProBException {

		if (results == null) {
			final String message = "The last result from modelchecker was unexpectely null.";
			ProblemHandler.raiseCommandException(message);
		}

		ComputeCoverageResult coverage = computeCoverage();
		String header_text = "Model Checking finished";
		String text = "";
		// This code below is very boilerplate/clunky: replace by dictionary +
		// lookup ??
		switch (results) {
		case ok:
			text = createOkResult();
			break;
		case ok_not_all_nodes_considered:
			text = createOkButNotFinishedResult();
			break;
		case deadlock:
			List<String> traceDeadlock = GetTraceCommand.getTrace(animator);
			text = createDeadlockResult(traceDeadlock);
			replayErrorTrace();
			header_text = "Deadlock found!";
			break;
		case invariant_violation:
			List<String> trace = GetTraceCommand.getTrace(animator);
			text = createInvariantViolationResult(trace);
			replayErrorTrace();
			header_text = "Invariant Violation found!";
			break;
		case assertion_violation:
			List<String> atrace = GetTraceCommand.getTrace(animator);
			text = createAssertionViolationResult(atrace);
			replayErrorTrace();
			header_text = "Theorem Violation found!";
			break;
		case state_error:
			List<String> setrace = GetTraceCommand.getTrace(animator);
			text = createStateErrorResult(setrace);
			replayErrorTrace();
			header_text = "State Error found!";
			break;
		case well_definedness_error:
			List<String> wdtrace = GetTraceCommand.getTrace(animator);
			text = createWDErrorResult(wdtrace);
			replayErrorTrace();
			header_text = "Well-Definedness Error found!";
			break;
		case general_error:
			List<String> getrace = GetTraceCommand.getTrace(animator);
			text = createGeneralErrorResult(getrace);
			replayErrorTrace();
			header_text = "General Error found!";
			break;
		}
		MultiStatus info = new MultiStatus(ProbUiPlugin.PLUGIN_ID, 1,
				header_text, null);
		info.add(new Status(IStatus.INFO, PLUGIN_ID, 1, text, null));
		info.add(new Status(IStatus.INFO, PLUGIN_ID, 1, SEPARATOR_LINE, null));

		appendCoverageStatistics(coverage, PLUGIN_ID, info);
		return info;
	}

	private String createInvariantViolationResult(final List<String> trace) {
		StringBuffer sb = new StringBuffer();
		sb.append("Invariant violation found.\n");
		sb.append("ProB has detected a state that violates the invariant.\n");
		// no longer show trace of state ids to user; not very useful 
		//sb.append("The following is the trace that led to the violation:\n");
		//appendTrace(trace, sb);
		return sb.toString();
	}

	private String createAssertionViolationResult(final List<String> trace) {
		StringBuffer sb = new StringBuffer();
		sb.append("Theorem violation found.\n");
		sb.append("ProB has detected a state that violates the theorems.\n");
		// no longer show trace of state ids to user; not very useful
		//sb.append("The following is the trace that led to the violation:\n");
		//appendTrace(trace, sb);
		return sb.toString();
	}

	private String createStateErrorResult(final List<String> trace) {
		StringBuffer sb = new StringBuffer();
		sb.append("ProB has detected an erroneous state (e.g., witness error, guard strengthening error).\n");
		//sb.append("The following is the trace that led to the error:\n");
		//appendTrace(trace, sb);
		return sb.toString();
	}

	private String createWDErrorResult(final List<String> trace) {
		StringBuffer sb = new StringBuffer();
		sb.append("ProB has detected a state which caused a well-definedness error(e.g., division by zero).\n");
		//sb.append("The following is the trace that led to the error:\n");
		//appendTrace(trace, sb);
		return sb.toString();
	}

	private String createGeneralErrorResult(final List<String> trace) {
		StringBuffer sb = new StringBuffer();
		sb.append("ProB has detected a state which caused an internal error.\n");
		sb.append("This is probably a bug (please report).\n");
		//sb.append("The following is the trace that led to the error:\n");
		//appendTrace(trace, sb);
		return sb.toString();
	}

	private String createDeadlockResult(final List<String> trace) {
		StringBuffer sb = new StringBuffer();
		sb.append("Deadlock found.\n");
		sb.append("ProB has detected a state where all guards are false.\n");
		//sb.append("The following is the trace that led to the deadlock:\n");
		//appendTrace(trace, sb);
		return sb.toString();
	}

	private void appendTrace(final List<String> trace, final StringBuffer sb) {
		for (String s : trace) {
			sb.append(s);
			sb.append('\n');
		}
	}

	private String createOkButNotFinishedResult() {
		return "No errors found, but not all possible "
				+ "states have been visited (Due to animation parameter restrictions).";
	}

	private String createOkResult() {
		return "No errors found\n"
				+ "Model Checking finished, all states visited.\n";
	}

	private void replayErrorTrace() throws ProBException {
		ReplayTraceCommand.replay(animator);
	}

	private void displayResult(final MultiStatus info) {
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(shell, "Model Checking finished", null,
						info);
			}
		});
	}

	private ComputeCoverageResult computeCoverage() throws ProBException {
		return ComputeCoverageCommand.getCoverage(animator);
	}

	private void appendCoverageStatistics(final ComputeCoverageResult coverage,
			final String PID, final MultiStatus info) {
		info.add(new Status(IStatus.INFO, PID, 1, "Coverage statistics:", null));
		info.add(new Status(IStatus.INFO, PID, 1, "Total Number of States:"
				+ coverage.getTotalNumberOfNodes(), null));
		info.add(new Status(IStatus.INFO, PID, 1,
				"Total Number of Transitions:"
						+ coverage.getTotalNumberOfTransitions(), null));
		info.add(new Status(IStatus.INFO, PID, 1, "Node Statistics:", null));
		for (String s : coverage.getNodes()) {
			info.add(new Status(IStatus.INFO, PID, 1, s, null));
		}
		info.add(new Status(IStatus.INFO, PID, 1, "Operations Statistics:",
				null));
		for (String s : coverage.getOps()) {
			info.add(new Status(IStatus.INFO, PID, 1, s, null));
		}
		info.add(new Status(IStatus.INFO, PID, 1, "Uncovered Operations:", null));
		for (String s : coverage.getUncovered()) {
			info.add(new Status(IStatus.INFO, PID, 1, s, null));
		}
	}

}