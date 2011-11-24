/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

public interface IRunPageListener {

	public void runPageCreated(BMotionStudioRunPage runPage);

	public void runPageRemoved(BMotionStudioRunPage runPage);

}
