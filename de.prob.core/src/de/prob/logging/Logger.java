/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.logging;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import de.prob.cli.CliException;
import de.prob.core.internal.Activator;
import de.prob.exceptions.ProBAssertionFailed;
import org.osgi.framework.Bundle;
import de.prob.parser.BindingGenerator;

/**
 * Logging class, use this to notify the user about errors. It also allows to
 * register new Log-Listeners
 * 
 * @author Jens Bendisposto
 * 
 */
public final class Logger {
	/**
	 * Registers a new {@link ILogListener}. The listener's
	 * {@link ILogListener#logging(IStatus, String)} method is called, if
	 * something is written to the plug-in log
	 * 
	 * @param listener
	 */
	public static void addListener(final ILogListener listener) {
		Activator.getDefault().getLog().addLogListener(listener);
	}

	/**
	 * Notifies the User about a fatal problem by adding an error
	 * to the log. This method takes a message
	 * describing the problem as well as an exception.
	 * <p>
	 * Note: Use this only if you don't want to throw an exception. If you want
	 * to throw a {@link CliException}, use
	 * {@link #handleCliException(String, Throwable)} or
	 * {@link #createException(String)} instead
	 * 
	 * @param message
	 *            Description of the problem.
	 * 
	 * @param throwable
	 *            Causing exception
	 */
	public static void notifyUser(final String message,
			final Throwable throwable) {
		log(IStatus.ERROR, message, throwable);
	}

	public static void notifyUser(final String message) {
		notifyUser(message, null);
	}

	/**
	 * Check a property. The method notifies the user and throws an exception if
	 * the assertion fails. <br>
	 * <b>Examples:</b><br>
	 * {@code Logger.assertProB(this.getClass(),"x != null", x
	 * != null); } <br>
	 * {@code Logger.assertProB(this.getClass(),"x should not be null", x != null); }
	 * <br>
	 * <b>Note:</b> If you check PrologTerm objects for their type, you should
	 * rather use the static getXXX methods from {@link BindingGenerator}
	 * located in the de.prob.core package
	 * 
	 * @param clazz
	 *            The calling class. This is to help locating the problem
	 * @param assertion
	 *            The Property as a string, this helps to find out, what is
	 *            going wrong, this can contain arbitrary text to help. It is
	 *            recommended to include the property itself
	 * @param property
	 *            a boolean value, typically you want to use a boolean
	 *            expression
	 * @throws CliException
	 */
	public static void assertProB(final String assertion, final boolean property)
			throws ProBAssertionFailed {
		if (!property) {

			StackTraceElement caller = Thread.currentThread().getStackTrace()[2];

			notifyUser("ProB Assertion '" + assertion + "' failed in Class: "
					+ caller.getClassName() + "\n Please submit a bugreport");

			throw new ProBAssertionFailed(assertion);
		}
	}

	/**
	 * Failed assertion. The method notifies the user and throws an exception. <br>
	 * <b>Examples:</b><br>
	 * {@code if (!file.exists()) String message =
	 * "Called open on nonexisting file " + file;
	 * Logger.assertProB(EditorPlugin.class, message); }
	 * 
	 * @param clazz
	 *            The calling class. This is to help locating the problem
	 * @param assertion
	 *            The Property as a string, this helps to find out, what is
	 *            going wrong, this can contain arbitrary text to help. It is
	 *            recommended to include the property itself
	 * @throws CliException
	 */
	public static void assertFail(final String assertion)
			throws ProBAssertionFailed {
		assertProB(assertion, false);
	}

	public static void log(final int severity, final String message, final Throwable exception) {
		final IStatus status = new Status(severity, Activator.PLUGIN_ID, message, exception);
		log(status);
	}

	/** shows notices without interrupting the user, e.g. in a small popup */
	public interface NonModalNotifier {
		/**
		 * @param message
		 *            a short summary
		 * @param details
		 *            more information, e.g. a list of elements, or
		 *            <code>null</code>
		 */
		void notify(String message, String details);
	}

	private static volatile NonModalNotifier nonModalNotifier;

	/** called by the UI plug-in, which shows the notices */
	public static void setNonModalNotifier(final NonModalNotifier notifier) {
		nonModalNotifier = notifier;
	}

	/**
	 * Tells the user about a problem that needs no immediate action, without
	 * a modal dialog: it is shown by the UI's notifier (if there is one) and
	 * written to the log as information, so it opens no error dialog.
	 */
	public static void notifyUserNonModal(final String message,
			final String details) {
		info(details == null ? message : message + "\n" + details);
		final NonModalNotifier notifier = nonModalNotifier;
		if (notifier != null)
			notifier.notify(message, details);
	}

	public static void info(final String message) {
		final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
				IStatus.OK, message, null);
		log(status);
	}

	private Logger() {
		// Not intended for instantiation
	}

	private static void log(final IStatus status) {
		final Activator plugin = Activator.getRunningInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
			return;
		}
		// the plug-in has been stopped (e.g. a job finishing during
		// shutdown) or not started yet: log on behalf of its bundle
		final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		if (bundle != null) {
			Platform.getLog(bundle).log(status);
		} else {
			System.err.println(status);
		}
	}
}
