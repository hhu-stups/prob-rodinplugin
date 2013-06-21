/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.core.Animator;
import de.prob.core.command.ConsistencyCheckingCommand;
import de.prob.core.command.ConsistencyCheckingSearchOption;
import de.prob.core.command.SetPreferenceCommand;
import de.prob.core.command.ConsistencyCheckingCommand.Result;
import de.prob.exceptions.ProBException;

public class ConsistencyCheckingJob extends Job {

	private final Animator animator = Animator.getAnimator();
	private boolean abort = false;
	private final List<String> options;
	private final String symmetryOption;
	private Result modelCheckingResult = null;

	public ConsistencyCheckingJob(final String name,
			final Set<ConsistencyCheckingSearchOption> options,
			final String symmetryOption) {
		super(name);
		List<String> optlist = new ArrayList<String>();
		for (ConsistencyCheckingSearchOption modelCheckingOption : options) {
			optlist.add(modelCheckingOption.name());
		}
		this.options = optlist;
		this.symmetryOption = symmetryOption;
	}

	public Result getModelCheckingResult() {
		return modelCheckingResult;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (!setSymmetry()) {
			return Status.CANCEL_STATUS;
		}

		monitor.beginTask("Model checking", IProgressMonitor.UNKNOWN);
		while (!abort) {
			try {
				modelCheckingResult = doSomeModelchecking();
				options.remove("inspect_existing_nodes");
				monitor.worked(500);
			} catch (ProBException e) {
				return Status.CANCEL_STATUS; // Failed
			}
			abort = modelCheckingResult.isAbort() || monitor.isCanceled();
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

	private Result doSomeModelchecking() throws ProBException {
		return ConsistencyCheckingCommand.modelcheck(animator, 500, options)
				.getResult();
	}

}
