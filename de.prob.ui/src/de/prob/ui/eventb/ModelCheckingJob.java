/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.core.Animator;
import de.prob.core.command.*;
import de.prob.core.command.ModelCheckingCommand.Result;
import de.prob.exceptions.ProBException;

public class ModelCheckingJob extends Job {

	private final Animator animator = Animator.getAnimator();
	private boolean abort = false;
	private final List<String> options;
	private final String symmetryOption;
	private ModelCheckingResult<Result> modelCheckingResult = null;
	private int workedSoFar = 0;

	public ModelCheckingJob(final String name,
			final Set<ModelCheckingSearchOption> options,
			final String symmetryOption) {
		super(name);
		List<String> optlist = new ArrayList<String>();
		for (ModelCheckingSearchOption modelCheckingOption : options) {
			optlist.add(modelCheckingOption.name());
		}
		this.options = optlist;
		this.symmetryOption = symmetryOption;
	}

	public Result getModelCheckingResult() {
		return modelCheckingResult.getResult();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (!setSymmetry()) {
			return Status.CANCEL_STATUS;
		}

		monitor.beginTask("Model checking", 1000);
		while (!abort) {
			try {
				modelCheckingResult = doSomeModelchecking();
				options.remove("inspect_existing_nodes");

				int difference = modelCheckingResult.getWorked() - workedSoFar;
				if (difference > 0) {
					monitor.worked(difference);
					workedSoFar = modelCheckingResult.getWorked();
				}
			} catch (ProBException e) {
				return Status.CANCEL_STATUS; // Failed
			}
			abort = getModelCheckingResult().isAbort() || monitor.isCanceled();
		}
		return Status.OK_STATUS;
	}

	private boolean setSymmetry() {
		try {
			SetPreferenceCommand.setPreference(animator, "SYMMETRY_MODE",
					symmetryOption);
		} catch (ProBException e) {
			return false; // Failed
		}
		return true;
	}

	private ModelCheckingResult<Result> doSomeModelchecking()
			throws ProBException {
		return ModelCheckingCommand.modelcheck(animator, 500, options);
	}

}
