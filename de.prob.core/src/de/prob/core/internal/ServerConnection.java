/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import de.prob.cli.CliException;
import de.prob.cli.CliStarter;
import de.prob.core.IServerConnection;
import de.prob.core.ProblemHandler;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class ServerConnection implements IServerConnection {

	private Socket socket = null;
	private BufferedInputStream inputStream = null;
	private PrintStream outputStream = null;

	private CliStarter cli = null;

	private String lastCommand;

	private volatile boolean shutdown = true;

	// private static final ScheduledExecutorService exec = new
	// ScheduledThreadPoolExecutor(
	// 1);

	private void startcli(final File file) throws CliException {
		cli = new CliStarter(file);
	}

	public String getLastCommand() {
		return lastCommand;
	}

	private void establishConnection(final int port) throws CliException {
		try {
			socket = new Socket(InetAddress.getByName(null), port);
			inputStream = new BufferedInputStream(socket.getInputStream());
			outputStream = new PrintStream(socket.getOutputStream());
		} catch (final IOException e) {
			if (socket != null) {
				try {
					socket.close();
				} catch (final IOException e2) {
					Logger.info(e.getLocalizedMessage());
				} finally {
					socket = null;
					inputStream = null;
					outputStream = null;
				}
			}
			ProblemHandler.handleCliException(
					"Opening connection to ProB server failed", e);
		}
	}

	@Override
	public String sendCommand(final String commandString) throws ProBException {
		if (shutdown) {
			final String message = "probcli is currently shutting down";
			ProblemHandler.raiseCliException(message);
		}
		checkState();
		sendQuery(commandString);
		return getAnswer();
	}

	private void sendQuery(final String commandString) throws ProBException {
		lastCommand = commandString;
		Logger.assertProB("commandString.trim().endsWith(\".\")", commandString
				.trim().endsWith("."));

		outputStream.println(commandString);

		outputStream.flush();
	}

	private String getAnswer() throws ProBException {
		String input = null;
		try {
			input = readAnswer();
			if (input == null)
				throw new IOException(
						"ProB binary returned nothing - it might have crashed");
		} catch (final IOException e) {
			shutdown();
			String message = "Exception while reading from socket";
			ProblemHandler.handleCliException(message, e);
		}
		return input;
	}

	// private String timedRun(final Callable<String> r, final long timeOut,
	// final TimeUnit unit) throws InterruptedException, ProBException {
	// String s = null;
	// Future<String> task = exec.submit(r);
	// try {
	// s = task.get(timeOut, unit);
	// } catch (TimeoutException e) {
	// final String message = "Timeout while waiting for ProB's answer";
	// ProblemHandler.handleCliException(message, e);
	// } catch (ExecutionException e) {
	// final String message = e.getCause().getLocalizedMessage();
	// ProblemHandler.handleCliException(message, e.getCause());
	// } finally {
	// task.cancel(true);
	// }
	// return s;
	// }

	protected String readAnswer() throws IOException {
		final StringBuilder result = new StringBuilder();
		final byte[] buffer = new byte[1024];
		boolean done = false;

		while (!done) {
			/*
			 * It might be necessary to check for inputStream.available() > 0.
			 * Or add some kind of timer to prevent the thread blocks forever.
			 * See task#102
			 */
			int count = inputStream.read(buffer);

			if (count > 0) {
				final byte length = 1;

				// check for end of transmission (i.e. last byte is 1)
				if (buffer[count - length] == 1) {
					done = true;
					count--; // remove end of transmission marker
				}

				// trim white spaces and append
				// instead of removing the last byte trim is used, because on
				// windows prob uses \r\n as new line.
				String s = new String(buffer, 0, count, Charset
						.defaultCharset().name());
				result.append(s.replace("\r", "").replace("\n", ""));
			} else {
				done = true;
			}
		}

		return result.length() > 0 ? result.toString() : null;
	}

	@Override
	public void startup(final File file) throws CliException {
		if (shutdown) {
			startcli(file);
			establishConnection(cli.getPort());
			shutdown = false;
		} else {
			// This should never happen
			final String message = "Tried to start a server that is already running";
			Logger.notifyUserWithoutBugreport(message);
		}
	}

	@Override
	public void shutdown() {
		if (!shutdown) {
			if (socket != null) {
				try {
					socket.close();
				} catch (final IOException e) {
					Logger.info(e.getLocalizedMessage());
				}
			}

			socket = null;
			inputStream = null;
			outputStream = null;

			cli.shutdown();
			cli = null;
			shutdown = true;
		}
	}

	private void checkState() throws CliException {
		if (inputStream == null || outputStream == null) {
			ProblemHandler.raiseCliException("Stream to ProB server not ready");
		}
	}

	@Override
	public int getCliPortNumber() {
		return cli.getPort();
	}

	@Override
	public void sendUserInterruptSignal() {
		if (cli != null) {
			cli.sendUserInterruptReference();
		}
	}
}
