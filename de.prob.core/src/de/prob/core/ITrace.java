/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core;

import java.util.List;

import de.prob.core.internal.Message;

public interface ITrace {

	/**
	 * @return the List of logged messages as a String
	 */
	public String getTraceAsString();

	/**
	 * @return the List of logged messages as unmodifiable List of
	 *         {@link Message}
	 */
	public List<Message> getTraceAsList();

	public int size();

	void setMaximum(Integer max);

	Integer getMaximum();

}
