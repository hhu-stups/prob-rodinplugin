/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core;

import java.util.List;

import de.prob.cli.CliException;
import de.prob.core.command.CommandException;
import de.prob.core.internal.ResultParserException;
import de.prob.logging.Logger;

public class ProblemHandler {

	/**
	 * Notifies the User about a fatal problem inside a command by adding an error
	 * to the log. This method takes a message
	 * describing the problem.<br>
	 * 
	 * <b>Note:</b> Calling this method logs the problem and throws a new
	 * {@link CommandException} that uses the message given <br>
	 * <b>Note:</b> If the problem is related to the content of a returned
	 * answer, such as an unexpectedly failed query, use
	 * {@link #raiseCommandException(String)(String)}
	 * 
	 * @param message
	 *            Description of the problem
	 * @throws CommandException
	 */
	public static void raiseCliException(final String message)
			throws CliException {
		Logger.notifyUser(message);
		throw new CliException(message, true);
	}

	/**
	 * Notifies the User about a fatal problem inside a command by adding an error
	 * to the log. This method takes a message
	 * describing the problem.<br>
	 * 
	 * <b>Note:</b> Calling this method logs the problem and throws a new
	 * {@link CliException} that uses the message given <br>
	 * <b>Note:</b> If the problem is related to a "low level" construct, such
	 * as exceptions while sending or receiving messages, use
	 * {@link #raiseCliException(String)}
	 * 
	 * @param message
	 *            Description of the problem
	 * @throws CommandException
	 */
	public static void raiseCommandException(final String message)
			throws CommandException {
		Logger.notifyUser(message);
		throw new CommandException(message);
	}

	/**
	 * Notifies the user, that ProB raised some error messages
	 * 
	 * @param errors
	 *            The List of Error Messages from ProB
	 * @throws PrologException
	 */
	public static void raisePrologException(final List<String> errors,
			final boolean onlyWarnings) throws PrologException {
		final String message = "ProB reported errors:\n"
				+ String.join("\n", errors);
		Logger.notifyUser(message);
		throw new PrologException(message, onlyWarnings);
	}

	/**
	 * 
	 * Notifies the User about a fatal problem by adding an error
	 * to the log. This method takes a message
	 * describing the problem and the causing exception.
	 * 
	 * Note: Calling this method logs the problem and throws a CliException that
	 * wraps the original problem
	 * 
	 * @param message
	 *            Description of the problem
	 * @param throwable
	 *            Causing exception
	 * @throws CommandException
	 */
	public static void handleCommandException(final String message,
			final Throwable t) throws CommandException {
		Logger.notifyUser(message, t);
		throw new CommandException(message, t);
	}

	/**
	 * 
	 * Notifies the User about a fatal problem by adding an error
	 * to the log. This method takes a message
	 * describing the problem and the causing exception.
	 * 
	 * Note: Calling this method logs the problem and throws a CliException that
	 * wraps the original problem
	 * 
	 * @param message
	 *            Description of the problem
	 * @param throwable
	 *            Causing exception
	 * @throws CliException
	 */
	public static void handleCliException(final String message,
			final Throwable t) throws CliException {
		Logger.notifyUser(message, t);
		throw new CliException(message, t, true);
	}

	/**
	 * 
	 * Notifies the User about a fatal problem by adding a
	 * {@link Logger#FATALERROR} to the log. This method takes a message
	 * describing the problem and the causing exception.
	 * 
	 * Note: Calling this method logs the problem and throws a CliException that
	 * wraps the original problem
	 * 
	 * @param message
	 *            Description of the problem
	 * @param throwable
	 *            Causing exception
	 * @throws ResultParserException
	 */
	public static void handleResultPareserException(final String message,
			final Throwable t) throws ResultParserException {
		Logger.notifyUser(message, t);
		throw new ResultParserException(message, t);
	}

}
