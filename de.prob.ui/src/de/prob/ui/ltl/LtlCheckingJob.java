/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.ltl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.core.Animator;
import de.prob.core.command.LtlCheckingCommand;
import de.prob.core.command.LtlCheckingCommand.Result;
import de.prob.core.command.SetPreferenceCommand;
import de.prob.exceptions.ProBException;
import de.prob.prolog.term.PrologTerm;

public final class LtlCheckingJob extends Job {

	private final Animator animator = Animator.getAnimator();
	private final PrologTerm formula;
	private final LtlCheckingCommand.StartMode option;
	private final String symmetry;

	private boolean abort = false;
	private Result modelCheckingResult;
	private boolean anErrorOccurred = false;

	public LtlCheckingJob(final String name, final PrologTerm parsedFormula,
			final LtlCheckingCommand.StartMode option, final String symmetry) {
		super(name);
		this.formula = parsedFormula;
		this.option = option;
		this.symmetry = symmetry;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		anErrorOccurred = false;

		if (!setSymmetry())
			return Status.CANCEL_STATUS;
		monitor.beginTask("Checking LTL Formula", IProgressMonitor.UNKNOWN);

		while (!abort) {
			try {
				modelCheckingResult = doSomeModelchecking();
				monitor.worked(500);
			} catch (ProBException e) {
				// We cannot recover from this, but the user has been informed
				e.notifyUserOnce();
				anErrorOccurred = true;
				return Status.CANCEL_STATUS;
			}
			abort = modelCheckingResult.isAbort() || monitor.isCanceled();
		}

		return Status.OK_STATUS;
	}

	public Result getModelCheckingResult() {
		return modelCheckingResult;
	}

	public boolean isAnErrorOccurred() {
		return anErrorOccurred;
	}

	private Result doSomeModelchecking() throws ProBException {
		return LtlCheckingCommand.modelCheck(animator, formula, 500, option);
	}

	private boolean setSymmetry() {
		try {
			SetPreferenceCommand.setPreference(animator, "SYMMETRY_MODE",
					symmetry);
		} catch (ProBException e) {
			return false; // Failed
		}
		return true;
	}

}
