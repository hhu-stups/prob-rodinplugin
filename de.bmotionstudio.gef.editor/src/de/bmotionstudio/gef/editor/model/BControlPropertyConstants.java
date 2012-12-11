/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

public final class BControlPropertyConstants {

	public static final transient String PROPERTY_LAYOUT = "ControlLayout";
	public static final transient String PROPERTY_LOCATION = "ControlLocation";

	public static final transient String PROPERTY_ADD_CHILD = "ControlAddChild";
	public static final transient String PROPERTY_REMOVE_CHILD = "ControlRemoveChild";

	public static final transient String PROPERTY_ADD_OBSERVER = "ControlAddObserver";
	public static final transient String PROPERTY_REMOVE_OBSERVER = "ControlRemoveObserver";

	public static final transient String PROPERTY_ADD_EVENT = "ControlAddEvent";
	public static final transient String PROPERTY_REMOVE_EVENT = "ControlRemoveEvent";

	public static final transient String PROPERTY_RENAME = "ControlRename";
	/** Property ID to use when the list of outgoing connections is modified. */
	public static final transient String SOURCE_CONNECTIONS = "ControlSourceConn";
	/** Property ID to use when the list of incoming connections is modified. */
	public static final transient String TARGET_CONNECTIONS = "ControlTargetConn";

}
