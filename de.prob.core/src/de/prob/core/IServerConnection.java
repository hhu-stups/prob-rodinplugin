/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core;

import java.io.File;

import de.prob.cli.CliException;
import de.prob.exceptions.ProBException;

/**
 * This interface can be used to mock up a connection to ProB for testing. Don't
 * use this in productive code.
 * 
 * An example of such a mock is
 * 
 * <pre>
 * IServerConnection mock = EasyMock.createMock(IServerConnection.class);
 * mock.startup();
 * EasyMock.expect(mock.sendCommand(COMMAND)).andReturn(
 * 		ProBResultParser.parse(EXPECTED_ANSWER));
 * GetErrorsHelper.expectGetErrors(mock);
 * EasyMock.replay(mock);
 * Animator animator = new AnimatorImpl(mock);
 * EasyMock.verify(mock);
 * // [ ... Assertions ...]
 * </pre>
 * 
 * @author Jens Bendisposto
 * 
 */
public interface IServerConnection {

	public abstract int getCliPortNumber();

	public abstract String sendCommand(final String commandString)
			throws ProBException;

	public abstract void shutdown();

	public abstract void startup(File file) throws CliException;

	String getDebuggingKey();

	void sendUserInterruptSignal();
}