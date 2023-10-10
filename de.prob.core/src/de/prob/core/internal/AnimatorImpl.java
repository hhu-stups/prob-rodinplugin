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
import de.prob.core.sablecc.node.Start;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ProBResultParser;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * @author Jens Bendisposto
 * 
 */

public class AnimatorImpl {

	private final History history = new History();

	private ServerConnection connector;

	private MachineDescription description;

	private final File file;

	private LanguageDependendAnimationPart langdep;

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

	private Start parseResult(final String input) {
		if (input == null)
			return null;
		else
			return ProBResultParser.parse(input);
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

	public synchronized void setMachineDescription(
			final MachineDescription machineDescription) {
		this.description = machineDescription;
	}

	public synchronized MachineDescription getMachineDescription() {
		return this.description;
	}

	public boolean isMachineLoaded() {
		return this.description != null;
	}

	public void execute(final IComposableCommand command) throws ProBException {
		checkConnector(command.getClass().getName());

		final GetErrorsCommand getErrors = new GetErrorsCommand();
		final ComposedCommand cmds = new ComposedCommand(command, getErrors);
		SimplifiedROMap<String, PrologTerm> bindings = null;
		List<String> errors = null;
		try {
			bindings = sendCommand(cmds);
			cmds.processResult(bindings);
			errors = getErrors.getErrors();
		} catch (RuntimeException e) {
			Logger.notifyUser(e.getLocalizedMessage(), e);
		} finally {
			if (errors == null) {
				if (bindings == null) {
					// the exception occurred while sending the commands
					// launch another query to get errors
					bindings = sendCommand(getErrors);
					getErrors.processResult(bindings);
				} else {
					// we cannot call getErrors.processResult directly because
					// the wrapping ComposedCommand may have mapped the bindings
					cmds.reprocessResult(getErrors, bindings);
				}
				errors = getErrors.getErrors();
			}
			if (errors != null && !errors.isEmpty()) {
				ProblemHandler.raisePrologException(errors,
						getErrors.onlyWarningsOccurred());
			}
		}
	}

	private SimplifiedROMap<String, PrologTerm> sendCommand(
			final IComposableCommand command) throws ProBException,
			CommandException {

		PrologTermStringOutput pto = new PrologTermStringOutput();
		command.writeCommand(pto);
		final String query = pto.fullstop().toString();
		String input;
		synchronized (this) {
			input = connector.sendCommand(query);
		}
		Map<String, PrologTerm> bindings;
		try {
			final Start ast = parseResult(input);
			bindings = BindingGenerator.createBindingMustNotFail(query, ast);
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
		if (connector != null) {
			connector.sendUserInterruptSignal();
		}
	}
}
