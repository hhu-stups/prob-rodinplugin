/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.internal.Activator;
import de.prob.core.internal.AnimatorImpl;
import de.prob.core.internal.ServerConnection;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

import org.osgi.service.prefs.Preferences;

/**
 * Animator is a singleton Proxy used to communicate with ProB. The method
 * {@link Animator#getAnimator()} returns the current instance, that can be used
 * to communicate with the ProB core. {@link Animator#execute(Command)} should
 * be used by commands (see {@link Command} for an example), not directly by
 * clients.
 * 
 * @author Jens Bendisposto
 * 
 */

public final class Animator {

	private static Animator animator = new Animator();
	private static Animator auxanimator = null;
	private static final Object AUX_LOCK = new Object();

	// threads waiting in execute() for the animator lock
	private final AtomicInteger waitingCommands = new AtomicInteger();
	private static final long MAX_YIELD_MS = 5000;

	private volatile boolean dirty;
	private volatile boolean rodinProjectHasErrorsOrWarnings;
	// own lock: must be usable while a (long) command holds the animator lock
	private final Map<Object, Object> dataStore = Collections
			.synchronizedMap(new HashMap<Object, Object>());

	// volatile: read without the lock by isMachineLoaded()
	private volatile AnimatorImpl implementation;

	// ------------------ Container Lifecycle

	/**
	 * Used to obtain a reference to the animator object. This can be kept
	 * without problem. If a different animation was started, the reference will
	 * change accordingly.
	 * 
	 * <p>
	 * Note: There is only <b>one</b> model loaded at a time.
	 * </p>
	 * 
	 * @return Reference to the singleton instance of Animator
	 */
	public final static Animator getAnimator() {
		return animator;
	}

	public final static Animator getAuxAnimator() {
		// may be called concurrently, e.g. by the auto prover and an
		// interactive disprover call; each instance starts its own probcli
		synchronized (AUX_LOCK) {
			if (auxanimator == null) {
				auxanimator = new Animator();
			}
			return auxanimator;
		}
	}

	/**
	 * Stops the probcli instance of the auxiliary animator, if one has been
	 * started (e.g. when the plug-in is stopped)
	 */
	public final static void shutdownAuxAnimator() {
		final Animator aux;
		synchronized (AUX_LOCK) {
			aux = auxanimator;
		}
		if (aux != null) {
			aux.sendUserInterruptSignal();
			aux.shutdown();
		}
	}


	/**
	 * Terminates the current animation (forcefully!) and restarts the ProB
	 * core. After calling this method, all information from the current
	 * animation will be lost.
	 * 
	 * Note: This will be automatically called by the ClearLoadedMachine
	 * command. Typically there is no need to call this method from other
	 * places.
	 */
	public final static void killAndReload() {
		killAndLoad(null);
	}

	public final static void killAndLoad(final File file) {
		synchronized (animator) {
			animator.killImplementation();
			animator.createNewImplementation(file);
		}

	}

	private final synchronized void createNewImplementation(final File file) {
		final AnimatorImpl impl = new AnimatorImpl(new ServerConnection(), file);
		setImplementation(impl);
		StaticListenerRegistry.registerComputationListener(getHistory());
	}

	private synchronized void setImplementation(final AnimatorImpl impl) {
		implementation = impl;
	}

	private final synchronized void killImplementation() {
		if (implementation != null) {
			// use this animator's history (this may be the aux animator)
			final History history = implementation.getHistoryImpl();
			StaticListenerRegistry.unregisterComputationListener(history);
			implementation.shutdownImplementation();
			implementation = null;
		}
	}

	// ------------------ Container Lifecycle

	private Animator() {
	}

	/**
	 * This constructor is only for Unit-Tests. Use the
	 * AnimatorTestFactory#create method to create new Instance instrumented
	 * with a particular Implementation. Note, that this actually creates a new
	 * Animator instance, that is different from the one that is received from
	 * {@link Animator#getAnimator()}
	 * 
	 * @param impl
	 */
	private Animator(final AnimatorImpl impl) {
		setImplementation(impl);
	}

	/**
	 * Kills the implementation, i.e. the underlying instance of ProB
	 */
	public final synchronized void shutdown() {
		if (implementation != null) {
			implementation.shutdownImplementation();
		}
	}

	// ------------------ Announce Methods
	/**
	 * Announces the reset event to all registered {@link ILifecycleListener}.
	 */
	public void announceReset() {
		getHistory().reset();
		Activator.reset();
	}

	/**
	 * Announces the change of ProB's current state to all registered
	 * {@link IAnimationListener}. For instance after executing an
	 * operation/event from the UI or replaying a trace. If the model checking
	 * mode is enabled, the animator does not propagate the information to all
	 * listeners but only to its own implementation (to prevent the GUI from
	 * displaying all changes during model checking).
	 */
	public synchronized void announceCurrentStateChanged(final State state,
			final Operation operation) {
		Activator.currentStateChanged(state, operation);
	}

	/**
	 * Announces that a new state has been discovered to all registered
	 * {@link IComputationListener}. Note that this does not mean, that ProB
	 * changed its state.
	 */
	public void announceComputedState(final State state) {
		Activator.computedState(state);
	}

	// ------------------ Delegates

	public final void execute(final IComposableCommand command)
			throws ProBException {
		execute(command, QUIET.get());
	}

	/** whether the commands of a thread are executed quietly */
	private static final ThreadLocal<Boolean> QUIET = ThreadLocal
			.withInitial(() -> Boolean.FALSE);

	/** a computation that executes ProB commands, see {@link #callQuietly} */
	public interface ProBCall<T> {
		T call() throws ProBException;
	}

	/**
	 * Runs a computation whose ProB commands, also those executed by helper
	 * methods, are executed quietly (see {@link #executeQuietly}): the user
	 * is not notified, e.g. by a pop-up dialog, as the caller shows the
	 * errors itself.
	 */
	public static <T> T callQuietly(final ProBCall<T> call)
			throws ProBException {
		final Boolean before = QUIET.get();
		QUIET.set(Boolean.TRUE);
		try {
			return call.call();
		} finally {
			QUIET.set(before);
		}
	}

	/**
	 * Like {@link #execute(IComposableCommand)}, but does not notify the user
	 * (e.g. by a pop-up dialog) if the command fails or ProB reports errors.
	 * Instead, the thrown exception's message has ProB's error messages, for
	 * a view to show them itself. Warnings of a successful command are only
	 * logged.
	 */
	public final void executeQuietly(final IComposableCommand command)
			throws ProBException {
		execute(command, true);
	}

	private void execute(final IComposableCommand command, final boolean quiet)
			throws ProBException {
		waitingCommands.incrementAndGet();
		boolean waiting = true;
		try {
			synchronized (this) {
				waitingCommands.decrementAndGet();
				waiting = false;
				restartIfConnectionLost();
				LimitedLogger.getLogger().log("execute command", command, null);
				getImplementation().execute(command, quiet);
				LimitedLogger.getLogger().log("command executed", command, null);
			}
		} finally {
			if (waiting) {
				waitingCommands.decrementAndGet();
			}
		}
	}

	/**
	 * To be called between the steps of a long-running loop of commands (e.g.
	 * model checking in chunks). Java monitors are not fair: without this, the
	 * loop would take the lock again right away and a command of another
	 * thread, e.g. the UI thread repainting a view, could wait until the whole
	 * loop is finished. Meanwhile the UI, including the progress dialog with
	 * its cancel button, would not be painted.
	 * 
	 * Must not be called while holding the animator lock.
	 */
	public void letWaitingCommandsRun() throws InterruptedException {
		// the waiting threads decrement the counter when they got the lock
		final long deadline = System.currentTimeMillis() + MAX_YIELD_MS;
		while (waitingCommands.get() > 0
				&& System.currentTimeMillis() < deadline) {
			Thread.sleep(5);
		}
	}

	/**
	 * @return The model's current state
	 */
	public final State getCurrentState() {
		return currentImplementation().getCurrentStateImpl();
	}

	/**
	 * @return The animation history, i.e. a trace from the root state to the
	 *         current state
	 */
	public final History getHistory() {
		return currentImplementation().getHistoryImpl();
	}

	/**
	 * @return true, iff there is an animation running
	 */
	public boolean isRunning() {
		return (implementation != null);
	}

	/**
	 * If probcli crashed (or could not be started), start a new instance so
	 * that the animator becomes usable again without restarting Rodin. The
	 * loaded model is lost in that case, so the main animator announces a
	 * reset to the UI.
	 */
	private synchronized void restartIfConnectionLost() {
		if (implementation != null && implementation.isConnectionLost()) {
			Logger.info("Connection to probcli lost, starting a new instance");
			killImplementation();
			createNewImplementation(null);
			if (this == animator) {
				announceReset();
			}
		}
	}

	private synchronized AnimatorImpl getImplementation() {
		if (implementation == null) {
			createNewImplementation(null);
		}
		return implementation;
	}

	/**
	 * Like {@link #getImplementation()}, but does not wait for a running
	 * command (which holds the lock, maybe for minutes during model checking)
	 * if there is an implementation. Used by getters called from the UI
	 * thread; the history and the implementation are thread-safe on their
	 * own.
	 */
	private AnimatorImpl currentImplementation() {
		final AnimatorImpl impl = implementation;
		return impl != null ? impl : getImplementation();
	}

	// just for testing
	private Preferences customConfiguration;

	public void setCustomConfiguration(final Preferences customConfiguration) {
		if (customConfiguration != null) {
			this.customConfiguration = customConfiguration;
		}
	}

	public Preferences getCustomConfiguration() {
		return customConfiguration;
	}

	/**
	 * Marks the animation as dirty, i.e., the underlying file has been changed
	 * and thus the current animation might not reflect the current model.
	 */
	public void setDirty() {
		this.dirty = true;
	}

	/**
	 * Resets the dirty flag.
	 */
	public void resetDirty() {
		this.dirty = false;
	}

	/**
	 * The dirty flag will be set, if one of the model's resources change during
	 * an animation.
	 * 
	 * @return true if the model and the animation might be out of sync
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	
	/**
	 * Puts the information that the associated Rodin project has errors or warnings.
	 */
	public void setRodinProjectHasErrorsOrWarnings() {
		this.rodinProjectHasErrorsOrWarnings = true;
		//System.out.println("** setRodinProjectHasErrorsOrWarnings() !!");
	}
	public void resetRodinProjectHasErrorsOrWarnings() {
		this.rodinProjectHasErrorsOrWarnings = false;
	}
	public boolean isRodinProjectHasErrorsOrWarnings() {
		return rodinProjectHasErrorsOrWarnings;
	}
	
	

	public void setMachineDescription(
			final MachineDescription machineDescription) {
		getImplementation().setMachineDescription(machineDescription);
	}

	public MachineDescription getMachineDescription() {
		// called from the UI thread, must not wait for a running command
		return currentImplementation().getMachineDescription();
	}

	/**
	 * Does not start probcli and does not wait for a running command, so it
	 * can be called from the UI thread.
	 * 
	 * @return true if a model has been loaded successfully
	 */
	public boolean isMachineLoaded() {
		final AnimatorImpl impl = implementation;
		return impl != null && impl.isMachineLoaded();
	}

	public LanguageDependendAnimationPart getLanguageDependendPart() {
		return currentImplementation().getLangdep();
	}

	public void setLanguageDependendPart(
			final LanguageDependendAnimationPart ldPart) {
		getImplementation().setLangdep(ldPart);
	}

	/**
	 * Each animator instance provides a possibility to store arbitrary data
	 * related to this animator. E.g., this can be used for caching. This method
	 * sets the data using an unique key.
	 * 
	 * @param key
	 * @param data
	 */
	public void setData(final Object key, final Object data) {
		dataStore.put(key, data);
	}

	/**
	 * Each animator instance provides a possibility to store arbitrary data
	 * related to this animator. E.g., this can be used for caching.
	 * 
	 * This method returns the data that was previously set by
	 * {@link #setData(Object, Object)}.
	 * 
	 * @param key
	 * @return
	 */
	public Object getData(final Object key) {
		return dataStore.get(key);
	}

	// if synchronized this will produce a deadlock. Ignore findbugs here
	public  void sendUserInterruptSignal() {
		final AnimatorImpl impl = implementation;
		if (impl != null) impl.sendUserInterruptSignal();
	}
	

}
