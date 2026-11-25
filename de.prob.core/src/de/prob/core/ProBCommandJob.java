/**
 * 
 */
package de.prob.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.core.command.IComposableCommand;
import de.prob.core.internal.Activator;
import de.prob.exceptions.ProBException;

/**
 * This jobs takes a command as argument and executes its during the run. If the
 * user selects cancel, the animator is asked to send an user interruption
 * signal to the Prolog core.
 * 
 * @author plagge
 */
public class ProBCommandJob extends Job {
	private final Animator animator;
	private final IComposableCommand command;

	/**
	 * if the user is not to be notified about a failure or ProB's errors (in
	 * a dialog); the caller gets them with {@link #getFailure()}
	 */
	private final boolean quiet;

	private boolean commandFailed = false;
	private volatile ProBException failure;

	public ProBCommandJob(final String name, final Animator animator,
			final IComposableCommand command) {
		this(name, animator, command, false);
	}

	/**
	 * @param quiet
	 *            if the user is not to be notified about a failure or ProB's
	 *            errors, e.g. for the disprover run by Rodin's auto-prover;
	 *            see {@link #getFailure()}
	 */
	public ProBCommandJob(final String name, final Animator animator,
			final IComposableCommand command, final boolean quiet) {
		super(name);
		this.animator = animator;
		this.command = command;
		this.quiet = quiet;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		final Activator plugin = Activator.getRunningInstance();
		if (plugin == null) {
			// the plug-in has been stopped, probcli is shut down
			return Status.CANCEL_STATUS;
		}
		plugin.registerJob(this);
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		commandFailed = false;
		failure = null;
		try {
			if (quiet)
				animator.executeQuietly(command);
			else
				animator.execute(command);
		} catch (ProBException e) {
			commandFailed = true;
			failure = e;
			if (!quiet)
				e.notifyUserOnce();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	@Override
	protected void canceling() {
		animator.sendUserInterruptSignal();
	}

	public IComposableCommand getCommand() {
		return command;
	}

	public Animator getAnimator() {
		return animator;
	}

	public boolean isCommandFailed() {
		return commandFailed;
	}

	/**
	 * @return why the command failed, e.g. a {@link PrologException} with
	 *         ProB's error messages; <code>null</code> if it succeeded. For a
	 *         quiet job, also ProB's errors of a command that otherwise
	 *         succeeded (only its warnings are just logged).
	 */
	public ProBException getFailure() {
		return failure;
	}

}
