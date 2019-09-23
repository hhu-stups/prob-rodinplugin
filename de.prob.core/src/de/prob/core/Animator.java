/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.prefs.Preferences;

import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.RandomSeed;
import de.prob.core.domainobjects.State;
import de.prob.core.internal.Activator;
import de.prob.core.internal.AnimatorImpl;
import de.prob.core.internal.ServerTraceConnection;
import de.prob.core.internal.TraceConnectionProvider;
import de.prob.exceptions.ProBException;


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

	/**
	 * 
	 */
	private IConnectionProvider connectionProvider = null;
	private volatile boolean dirty;
	private volatile boolean rodinProjectHasErrorsOrWarnings;
	private final Map<Object, Object> dataStore = new HashMap<Object, Object>();

	private AnimatorImpl implementation;

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
		if (auxanimator == null) {
			auxanimator = new Animator();
		}
		return auxanimator;
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
		final AnimatorImpl impl = new AnimatorImpl(getIServerConnection(), file);
		setImplementation(impl);
		StaticListenerRegistry.registerComputationListener(getHistory());
	}

	private synchronized void setImplementation(final AnimatorImpl impl) {
		implementation = impl;
	}

	private final synchronized void killImplementation() {
		if (implementation != null) {
			final History history = animator.getHistory();
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

	public final synchronized void execute(final IComposableCommand command)
			throws ProBException {
		LimitedLogger.getLogger().log("execute command", command, null);
		getImplementation().execute(command);
		LimitedLogger.getLogger().log("command executed", command, null);
	}

	/**
	 * @return The model's current state
	 */
	public final synchronized State getCurrentState() {
		return getImplementation().getCurrentStateImpl();
	}

	/**
	 * @return The animation history, i.e. a trace from the root state to the
	 *         current state
	 */
	public final synchronized History getHistory() {
		return getImplementation().getHistoryImpl();
	}

	/**
	 * @return true, iff there is an animation running
	 */
	public synchronized boolean isRunning() {
		return (implementation != null);
	}

	private synchronized AnimatorImpl getImplementation() {
		if (implementation == null) {
			createNewImplementation(null);
		}
		return implementation;
	}

	/**
	 * 
	 * @param {@link IConnectionProvider} provider
	 */
	public final synchronized void setConnectionProvider(
			final IConnectionProvider provider) {
		connectionProvider = provider;
	}

	/**
	 * @return {@link IServerConnection}, by default (that means
	 *         <code>connectionProvider == null</code>) the
	 *         {@link ServerTraceConnection}. If a {@link IConnectionProvider}
	 *         is set, it is asked to provide a new IServerConnection
	 * 
	 */
	private final synchronized IServerConnection getIServerConnection() {
		if (connectionProvider == null) {
			connectionProvider = new TraceConnectionProvider();
		}
		return connectionProvider.getISeverConnection();
	}

	/**
	 * 
	 * @return {@link ITrace}, or null if the IServerConnection is not a
	 *         {@link ServerTraceConnection}
	 */
	public final synchronized ITrace getTrace() {
		if (implementation != null)
			return implementation.getTraceImpl();
		return null;
	}

	public final synchronized RandomSeed getRandomSeed() {
		return getImplementation().getSeed();
	}

	public final synchronized void setRandomSeed(final RandomSeed seed) {
		getImplementation().setSeed(seed);
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
		return getImplementation().getMachineDescription();
	}

	public boolean isMachineLoaded() {
		return getImplementation().isMachineLoaded();
	}

	public String getDebuggingKey() {
		return getImplementation().getDebuggingKey();
	}

	public LanguageDependendAnimationPart getLanguageDependendPart() {
		return getImplementation().getLangdep();
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
	public synchronized void setData(final Object key, final Object data) {
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
	public synchronized Object getData(final Object key) {
		return dataStore.get(key);
	}

	// if synchronized this will produce a deadlock. Ignore findbugs here
	public  void sendUserInterruptSignal() {
		if (implementation != null) implementation.sendUserInterruptSignal();
	}
	

}
