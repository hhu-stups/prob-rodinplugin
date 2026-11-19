/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.internal;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.prob.cli.CliException;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.ProblemHandler;
import de.prob.core.PrologException;
import de.prob.core.UnsatAxiomsNotice;
import de.prob.core.command.CommandException;
import de.prob.core.command.ComposedCommand;
import de.prob.core.command.GetErrorsCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryItem;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.State;
import de.prob.core.sablecc.node.ACallBackResult;
import de.prob.core.sablecc.node.AExceptionResult;
import de.prob.core.sablecc.node.AInterruptedResult;
import de.prob.core.sablecc.node.ANoResult;
import de.prob.core.sablecc.node.AProgressResult;
import de.prob.core.sablecc.node.AYesResult;
import de.prob.core.sablecc.node.PResult;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.BindingGenerator;
import de.prob.parser.PrologTermGenerator;
import de.prob.parser.ResultParserException;
import de.prob.parser.SimplifiedROMap;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * @author Jens Bendisposto
 * 
 */

public class AnimatorImpl {

	private final History history = new History();

	private volatile ServerConnection connector;

	private volatile MachineDescription description;

	private final File file;

	// read without lock, e.g. from the UI thread
	private volatile LanguageDependendAnimationPart langdep;

	/**
	 * if probcli has getErrorMessagesWithSources/2, null while unknown; see
	 * {@link #checkErrorSources()}
	 */
	private volatile Boolean errorSources;

	public AnimatorImpl(final ServerConnection serverConnection, final File file) {
		this.file = file;
		setConnector(serverConnection);
	}

	public synchronized void shutdownImplementation() {
		if (connector != null) {
			connector.shutdown();
		}
		connector = null;
		history.reset();
		description = null;
	}

	private void checkConnector(final String command) throws CliException {
		if (connector == null) {
			final String message = "The connection to the ProB instance has not been established. Tried to execute "
					+ command;
			ProblemHandler.raiseCliException(message);
		}
	}

	public State getCurrentStateImpl() {
		HistoryItem item = history.getCurrent();
		return item == null ? null : item.getState();
	}

	public History getHistoryImpl() {
		return history;
	}

	private synchronized void setConnector(final ServerConnection serverConnection) {
		this.connector = serverConnection;
		try {
			connector.startup(file);
		} catch (CliException e) {
			e.notifyUserOnce();
			connector = null;
		}
	}

	public boolean isRunning() {
		return true;
	}

	// not synchronized: the field is volatile, and the lock is held while
	// waiting for probcli
	public void setMachineDescription(
			final MachineDescription machineDescription) {
		this.description = machineDescription;
	}

	public MachineDescription getMachineDescription() {
		return this.description;
	}

	public boolean isMachineLoaded() {
		return this.description != null;
	}

	/**
	 * @return true if probcli could not be started or the connection to it
	 *         was lost (e.g. because it crashed)
	 */
	public boolean isConnectionLost() {
		final ServerConnection c = connector;
		return c == null || c.isShutdown();
	}

	public void execute(final IComposableCommand command) throws ProBException {
		execute(command, false);
	}

	/**
	 * @param quiet
	 *            if the user is not to be notified about a failure or ProB's
	 *            errors; the thrown exception has ProB's error messages
	 */
	public void execute(final IComposableCommand command, final boolean quiet)
			throws ProBException {
		checkConnector(command.getClass().getName());

		UnsatAxiomsNotice.discardPending();
		final GetErrorsCommand getErrors = new GetErrorsCommand(
				Boolean.TRUE.equals(errorSources));
		final ComposedCommand cmds = new ComposedCommand(command, getErrors);
		SimplifiedROMap<String, PrologTerm> bindings = null;
		ProBException failure = null;
		try {
			bindings = sendCommand(cmds, quiet);
			cmds.processResult(bindings);
		} catch (ProBException e) {
			failure = e;
		} catch (RuntimeException e) {
			// a bug while processing the result must not make the command
			// look successful (its result would be missing)
			final String message = "Processing the result of "
					+ command.getClass().getSimpleName() + " failed: " + e;
			if (quiet)
				Logger.info(message);
			else
				Logger.notifyUser(message, e);
			failure = new CommandException(message, e);
		}

		final List<String> retrieved = retrieveErrors(getErrors, cmds,
				bindings, failure, quiet);
		if (errorSources == null)
			checkErrorSources();
		final List<String> errors = UnsatAxiomsNotice.handlePending(
				retrieved, retrieved == null ? null : getErrors.getSources(),
				getErrors.onlyWarningsOccurred(), quiet);
		if (quiet) {
			throwQuietly(errors, getErrors.onlyWarningsOccurred(), failure);
			return;
		}
		if (errors != null && !errors.isEmpty()) {
			ProblemHandler.raisePrologException(errors,
					getErrors.onlyWarningsOccurred());
		}
		if (failure != null) {
			throw failure;
		}
	}

	/**
	 * Finds out once whether probcli can tell the sources of its messages
	 * (getErrorMessagesWithSources/2, e.g. to recognise its messages about the
	 * axioms without relying on their wording). Called after a call, when
	 * its messages have been retrieved, so that the check takes none away.
	 */
	private void checkErrorSources() {
		if (isConnectionLost())
			return;
		final GetErrorsCommand check = new GetErrorsCommand(true);
		try {
			check.processResult(sendCommand(check, true));
			errorSources = Boolean.TRUE;
			if (check.getErrors() != null && !check.getErrors().isEmpty())
				Logger.info("ProB reported:\n"
						+ String.join("\n", check.getErrors()));
		} catch (ProBException | RuntimeException e) {
			// an older probcli, which does not know the predicate
			errorSources = Boolean.FALSE;
			Logger.info("probcli does not tell the sources of its messages (no getErrorMessagesWithSources/2): "
					+ e.getMessage());
		}
	}

	/**
	 * For a quiet command: throws an exception with ProB's error messages if
	 * the command failed or ProB reported errors; only logs warnings of a
	 * successful command.
	 */
	private static void throwQuietly(final List<String> errors,
			final boolean onlyWarnings, final ProBException failure)
			throws ProBException {
		final boolean hasMessages = errors != null && !errors.isEmpty();
		if (failure == null && (!hasMessages || onlyWarnings)) {
			if (hasMessages)
				// only logged: warnings would also be shown in a dialog
				Logger.info("ProB reported warnings:\n"
						+ String.join("\n", errors));
			return;
		}
		if (!hasMessages)
			throw failure;
		final String message = String.join("\n", errors);
		// PrologExceptions count as already handled, i.e. not to be shown
		// to the user by whoever catches it
		throw new PrologException(message, onlyWarnings);
	}

	private List<String> retrieveErrors(final GetErrorsCommand getErrors,
			final ComposedCommand cmds,
			final SimplifiedROMap<String, PrologTerm> bindings,
			final ProBException failure, final boolean quiet)
			throws CommandException {
		if (failure == null) {
			// cmds.processResult has also processed getErrors
			return getErrors.getErrors();
		}
		if (bindings != null) {
			// we cannot call getErrors.processResult directly because
			// the wrapping ComposedCommand may have mapped the bindings
			cmds.reprocessResult(getErrors, bindings);
			return getErrors.getErrors();
		}
		if (isConnectionLost()) {
			// probcli is gone, asking it for errors would only hide the
			// original failure
			return null;
		}
		// the exception occurred while sending the commands
		// launch another query to get errors
		try {
			getErrors.processResult(sendCommand(getErrors, quiet));
			return getErrors.getErrors();
		} catch (ProBException e) {
			Logger.info("Could not retrieve errors from ProB: "
					+ e.getMessage());
			return null;
		}
	}

	private SimplifiedROMap<String, PrologTerm> sendCommand(
			final IComposableCommand command, final boolean quiet)
			throws ProBException,
			CommandException {

		PrologTermStringOutput pto = new PrologTermStringOutput();
		command.writeCommand(pto);
		final String query = pto.fullstop().toString();
		Map<String, PrologTerm> bindings;
		try {
			PResult topnode;
			synchronized (this) {
				topnode = connector.sendCommand(query);
			}
			// If probcli doesn't return anything, sendCommand throws an exception.
			assert topnode != null;
			// Progress and callback results are handled inside sendCommand.
			assert !(topnode instanceof AProgressResult);
			assert !(topnode instanceof ACallBackResult);

			if (topnode instanceof AInterruptedResult) {
				// the user cancelled the command (see ProBCommandJob), not an
				// error to report; CommandExceptions count as already handled
				throw new CommandException("The command was interrupted.");
			}
			if (quiet && topnode instanceof ANoResult) {
				// the caller shows ProB's errors, or that it failed
				throw new CommandException("ProB's query failed.");
			}
			if (quiet && topnode instanceof AExceptionResult) {
				throw new CommandException("ProB raised an exception: "
						+ PrologTermGenerator
								.toPrologTerm(((AExceptionResult) topnode)
										.getTerm()));
			}
			if (!(topnode instanceof AYesResult)) {
				String queryForMessage = query;
				if (queryForMessage.length() > 400) {
					queryForMessage = queryForMessage.substring(0, 400) + "...";
				}
				throw new ResultParserException("Prolog query failed - received " + topnode.getClass().getSimpleName() + " in response to query: " + queryForMessage, null);
			}
			bindings = BindingGenerator.createBinding(PrologTermGenerator.toPrologTerm(topnode));
		} catch (ResultParserException e) {
			if (quiet)
				Logger.info(e.getMessage());
			else
				Logger.notifyUser(e.getMessage(), e);
			throw new CommandException(e.getMessage(), e);
		}
		return new SimplifiedROMap<String, PrologTerm>(bindings);
	}

	public LanguageDependendAnimationPart getLangdep() {
		return langdep;
	}

	public void setLangdep(final LanguageDependendAnimationPart langdep) {
		this.langdep = langdep;
	}

	public void sendUserInterruptSignal() {
		final ServerConnection c = connector;
		if (c != null) {
			c.sendUserInterruptSignal();
		}
	}
}
