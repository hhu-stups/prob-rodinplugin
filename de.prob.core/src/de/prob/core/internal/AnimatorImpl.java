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
import de.prob.core.command.CommandException;
import de.prob.core.command.ComposedCommand;
import de.prob.core.command.GetErrorsCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryItem;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.State;
import de.prob.core.sablecc.node.ACallBackResult;
import de.prob.core.sablecc.node.AInterruptedResult;
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
		checkConnector(command.getClass().getName());

		final GetErrorsCommand getErrors = new GetErrorsCommand();
		final ComposedCommand cmds = new ComposedCommand(command, getErrors);
		SimplifiedROMap<String, PrologTerm> bindings = null;
		ProBException failure = null;
		try {
			bindings = sendCommand(cmds);
			cmds.processResult(bindings);
		} catch (ProBException e) {
			failure = e;
		} catch (RuntimeException e) {
			// a bug while processing the result must not make the command
			// look successful (its result would be missing)
			final String message = "Processing the result of "
					+ command.getClass().getSimpleName() + " failed: " + e;
			Logger.notifyUser(message, e);
			failure = new CommandException(message, e);
		}

		final List<String> errors = retrieveErrors(getErrors, cmds, bindings,
				failure);
		if (errors != null && !errors.isEmpty()) {
			ProblemHandler.raisePrologException(errors,
					getErrors.onlyWarningsOccurred());
		}
		if (failure != null) {
			throw failure;
		}
	}

	private List<String> retrieveErrors(final GetErrorsCommand getErrors,
			final ComposedCommand cmds,
			final SimplifiedROMap<String, PrologTerm> bindings,
			final ProBException failure) throws CommandException {
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
			getErrors.processResult(sendCommand(getErrors));
			return getErrors.getErrors();
		} catch (ProBException e) {
			Logger.info("Could not retrieve errors from ProB: "
					+ e.getMessage());
			return null;
		}
	}

	private SimplifiedROMap<String, PrologTerm> sendCommand(
			final IComposableCommand command) throws ProBException,
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
			if (!(topnode instanceof AYesResult)) {
				String queryForMessage = query;
				if (queryForMessage.length() > 400) {
					queryForMessage = queryForMessage.substring(0, 400) + "...";
				}
				throw new ResultParserException("Prolog query failed - received " + topnode.getClass().getSimpleName() + " in response to query: " + queryForMessage, null);
			}
			bindings = BindingGenerator.createBinding(PrologTermGenerator.toPrologTerm(topnode));
		} catch (ResultParserException e) {
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
