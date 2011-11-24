/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.internal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

import de.prob.cli.CliException;
import de.prob.core.ITrace;
import de.prob.core.internal.Message.Type;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class ServerTraceConnection extends ServerConnection implements
		ILogListener {

	private static final int TRACE_SIZE_LIMIT = 100;

	private Trace trace;

	@Override
	public void startup(final File file) throws CliException {
		super.startup(file);
		trace = new Trace(TRACE_SIZE_LIMIT);
		logToTrace("ServerTraceConnection.startup(): " + getCurrentTime());
		Logger.addListener(this);
	}

	@Override
	public void shutdown() {
		super.shutdown();
		logToTrace("ServerTraceConnection.shutdown(): " + getCurrentTime());
	}

	@Override
	public String sendCommand(final String commandString) throws ProBException {
		sendMessage(commandString);
		return super.sendCommand(commandString);
	}

	@Override
	protected String readAnswer() throws IOException {
		String answer = super.readAnswer();
		receiveMessage(answer);
		return answer;

	}

	private void sendMessage(final String content) {
		trace.addMessage(Type.QUERY, content);
	}

	private void receiveMessage(final String content) {
		trace.addMessage(Type.ANSWER, content);
	}

	private final String getCurrentTime() {
		SimpleDateFormat date = new SimpleDateFormat(
				"yyyy.MM.dd ' - ' HH:mm:ss");
		Long timemillis = System.currentTimeMillis();

		return "Time: " + Long.toString(timemillis) + " - "
				+ date.format(timemillis);
	}

	/**
	 * Adds a {@link Message} with the Type = LOG to the actual Tracing
	 * {@link ITrace}
	 * 
	 * @param String
	 *            message
	 */
	public final void logToTrace(final String message) {
		trace.addMessage(Type.LOG, message);
	}

	/**
	 * Adds a {@link Message} with the Type = PREFERENCE to the actual Tracing
	 * {@link ITrace}
	 * 
	 * @param String
	 *            message
	 */
	public final void preferenceToTrace(final String message) {
		trace.addMessageOnTop(Type.PREFERENCE, message);
	}

	/**
	 * 
	 * @return {@link ITrace}, containing all Logs, Queries and Answers that
	 *         passed this ServerTraceConnection
	 */

	public final ITrace getTrace() {
		logToTrace(getCurrentTime());
		return trace;
	}

	/**
	 * Adds a Error-Log-Message to the trace
	 */
	public void logging(final IStatus status, final String plugin) {
		if (status.getSeverity() == IStatus.ERROR) {
			StringBuffer message = new StringBuffer();
			message.append(status.getMessage());

			if (status.getException() != null) {
				message.append("\n");
				message.append(status.getException().getLocalizedMessage());
			}
			logToTrace(message.toString());

		}

	}
}
