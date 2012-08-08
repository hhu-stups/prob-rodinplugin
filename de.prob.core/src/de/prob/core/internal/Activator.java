/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.prob.MainModule;
import de.prob.core.Animator;
import de.prob.core.IAnimationListener;
import de.prob.core.IComputationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.LimitedLogger;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.logging.Logger;
import de.prob.statespace.History;
import de.prob.webconsole.WebConsole;
import de.prob.webconsole.WebModule;

public final class Activator extends Plugin {
	private static History history;
	private final static Injector INJECTOR = Guice.createInjector(
			new MainModule(), new WebModule());

	// The plug-in ID
	public static final String PLUGIN_ID = "de.prob.core";
	private static final String ANIMATION_EXTENSION_POINT = PLUGIN_ID
			+ ".animation";
	private static final String COMPUTATION_EXTENSION_POINT = PLUGIN_ID
			+ ".computation";
	private static final String LIFECYCLE_EXTENSION_POINT = PLUGIN_ID
			+ ".lifecycle";

	private final static Set<ILifecycleListener> lifeCycleListeners = initLifeCycleListeners();
	private final static Set<IComputationListener> computationListeners = initComputationListeners();
	private final static Set<IAnimationListener> animationListeners = initAnimationListeners();

	private static Set<ILifecycleListener> initLifeCycleListeners() {
		HashSet<ILifecycleListener> result = new HashSet<ILifecycleListener>();
		final IExtensionRegistry extensionRegistry = Platform
				.getExtensionRegistry();
		final IExtensionPoint extensionPoint = extensionRegistry
				.getExtensionPoint(LIFECYCLE_EXTENSION_POINT);
		for (final IExtension extension : extensionPoint.getExtensions()) {
			for (final IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				result.add(new LifeCycleListenerProxy(configurationElement));
			}
		}
		return result;
	}

	private static Set<IComputationListener> initComputationListeners() {
		HashSet<IComputationListener> result = new HashSet<IComputationListener>();
		final IExtensionRegistry extensionRegistry = Platform
				.getExtensionRegistry();
		final IExtensionPoint extensionPoint = extensionRegistry
				.getExtensionPoint(COMPUTATION_EXTENSION_POINT);
		for (final IExtension extension : extensionPoint.getExtensions()) {
			for (final IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				result.add(new ComputationListenerProxy(configurationElement));
			}
		}
		return result;
	}

	private static Set<IAnimationListener> initAnimationListeners() {
		HashSet<IAnimationListener> result = new HashSet<IAnimationListener>();
		final IExtensionRegistry extensionRegistry = Platform
				.getExtensionRegistry();
		final IExtensionPoint extensionPoint = extensionRegistry
				.getExtensionPoint(ANIMATION_EXTENSION_POINT);
		for (final IExtension extension : extensionPoint.getExtensions()) {
			for (final IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				result.add(new AnimationListenerProxy(configurationElement));
			}
		}
		return result;
	}

	public static void reset() {
		synchronized (lifeCycleListeners) {
			final LimitedLogger logger = LimitedLogger.getLogger();
			logger.log("lifecycle", "start announcing reset", null);
			for (final ILifecycleListener l : lifeCycleListeners) {
				try {
					l.reset();
				} catch (final RuntimeException e) {
					final String message = "Runtime Exception thrown in bad behaving listener class "
							+ l.getClass() + " while sending reset event";
					Logger.notifyUser(message, e);
				}
			}
			logger.log("lifecycle", "finished announcing reset", null);
		}
	}

	public static void computedState(final State state) {
		synchronized (computationListeners) {
			final String stateId = state == null ? null : state.getId();
			final LimitedLogger logger = LimitedLogger.getLogger();
			logger.log("lifecycle", "start announcing computed state "
					+ stateId, null);
			for (final IComputationListener l : computationListeners) {
				try {
					l.computedState(state);
				} catch (final RuntimeException e) {
					final String message = "Runtime Exception thrown in bad behaving listener class "
							+ l.getClass()
							+ " while sending computation event.";
					Logger.notifyUser(message, e);
				}
			}
			logger.log("lifecycle", "finished announcing computed state "
					+ stateId, null);
		}
	}

	public static void currentStateChanged(final State currentState,
			final Operation operation) {
		synchronized (animationListeners) {
			final String stateId = currentState == null ? null : currentState
					.getId();
			final LimitedLogger logger = LimitedLogger.getLogger();
			logger.log("lifecycle",
					"start announcing current state " + stateId, null);
			for (final IAnimationListener l : animationListeners) {
				try {
					l.currentStateChanged(currentState, operation);
				} catch (final RuntimeException e) {
					final String message = "Runtime Exception thrown in bad behaving listener class "
							+ l.getClass()
							+ " while sending state change event";
					Logger.notifyUser(message, e);
				}
			}
			logger.log("lifecycle", "finished announcing current state "
					+ stateId, null);
		}
	}

	// The shared instance
	private static Activator plugin = null;

	private final Collection<Job> jobs = new ArrayList<Job>();
	private final IJobChangeListener jobFinishedListener = new JobFinishedListener();

	/**
	 * The constructor
	 */
	public Activator() {
		super();
		setInstance(this);
	}

	private static void setInstance(final Activator p) {
		plugin = p;
	}

	@Override
	public void stop(final BundleContext context) throws Exception { // NOPMD
		// by Jens Bendisposto on 7/20/07 12:19 PM
		// Method is declared in org.eclipse.core.runtime.Plugin

		cancelAllJobs();
		final Animator animator = Animator.getAnimator();
		if (animator != null) {
			animator.sendUserInterruptSignal();
			animator.shutdown();
		}

		plugin = null;
		super.stop(context);
	}

	private void cancelAllJobs() {
		synchronized (jobs) {
			for (final Job job : jobs) {
				job.cancel();
			}
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		if (plugin == null) {
			new Activator();
		}
		return plugin;
	}

	public void registerJob(final Job job) {
		synchronized (jobs) {
			jobs.add(job);
			job.addJobChangeListener(jobFinishedListener);
		}
	}

	public static Injector getInjector() {
		return INJECTOR;
	}

	public static History getHistory() {
		return history;
	}

	public static void setHistory(History history) {
		Activator.history = history;
	}

	private class JobFinishedListener extends JobChangeAdapter {
		@Override
		public void done(final IJobChangeEvent event) {
			super.done(event);
			synchronized (jobs) {
				jobs.remove(event.getJob());
			}
		}
	}

}
