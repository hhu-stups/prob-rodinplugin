/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.internal;

import de.prob.core.IConnectionProvider;
import de.prob.core.IServerConnection;

/**
 * Provides a new {@link ServerTraceConnection}
 */
public class TraceConnectionProvider implements IConnectionProvider {

	/**
	 * 
	 * @return new {@link ServerTraceConnection}
	 */
	public final IServerConnection getISeverConnection() {
		ServerTraceConnection conn = new ServerTraceConnection();
		return conn;
	}

}
