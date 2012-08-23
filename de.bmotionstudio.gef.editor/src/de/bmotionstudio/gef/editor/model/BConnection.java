/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeConnectionSourceDecoration;
import de.bmotionstudio.gef.editor.attribute.BAttributeConnectionTargetDecoration;
import de.bmotionstudio.gef.editor.attribute.BAttributeLineStyle;

public class BConnection extends BControl {

	public static String TYPE = "de.bmotionstudio.gef.editor.connection";

	/** True, if the connection is attached to its endpoints. */
	private boolean isConnected;
	/** Connection's source endpoint. */
	private BControl source;
	/** Connection's target endpoint. */
	private BControl target;

	/**
	 * Create a (solid) connection between two distinct shapes.
	 * 
	 * @param source
	 *            a source endpoint for this connection (non null)
	 * @param target
	 *            a target endpoint for this connection (non null)
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null or source == target
	 * @see #setLineStyle(int)
	 */
	public BConnection(Visualization visualization) {
		super(visualization);
	}

	/**
	 * Disconnect this connection from the shapes it is attached to.
	 */
	public void disconnect() {
		if (isConnected) {
			source.removeConnection(this);
			target.removeConnection(this);
			isConnected = false;
		}
	}

	/**
	 * Returns the source endpoint of this connection.
	 * 
	 * @return a non-null Shape instance
	 */
	public BControl getSource() {
		return source;
	}

	/**
	 * Returns the target endpoint of this connection.
	 * 
	 * @return a non-null Shape instance
	 */
	public BControl getTarget() {
		return target;
	}

	public void setTarget(BControl c) {
		this.target = c;
	}

	public void setSource(BControl c) {
		this.source = c;
	}

	/**
	 * Reconnect this connection. The connection will reconnect with the shapes
	 * it was previously attached to.
	 */
	public void reconnect() {
		if (!isConnected) {
			source.addConnection(this);
			target.addConnection(this);
			isConnected = true;
		}
	}

	/**
	 * Reconnect to a different source and/or target shape. The connection will
	 * disconnect from its current attachments and reconnect to the new source
	 * and target.
	 * 
	 * @param newSource
	 *            a new source endpoint for this connection (non null)
	 * @param newTarget
	 *            a new target endpoint for this connection (non null)
	 * @throws IllegalArgumentException
	 *             if any of the paramers are null or newSource == newTarget
	 */
	public void reconnect(BControl newSource, BControl newTarget) {
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
		initAttribute(AttributeConstants.ATTRIBUTE_CONNECTION, null);
		initAttribute(AttributeConstants.ATTRIBUTE_LINEWIDTH, 1,
				AttributeConstants.ATTRIBUTE_CONNECTION);
		initAttribute(AttributeConstants.ATTRIBUTE_LINESTYLE,
				BAttributeLineStyle.SOLID_CONNECTION,
				AttributeConstants.ATTRIBUTE_CONNECTION);
		initAttribute(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR, new RGB(0,
				0, 0), AttributeConstants.ATTRIBUTE_CONNECTION);
		initAttribute(
				AttributeConstants.ATTRIBUTE_CONNECTION_SOURCE_DECORATION,
				BAttributeConnectionSourceDecoration.DECORATION_NONE,
				AttributeConstants.ATTRIBUTE_CONNECTION);
		initAttribute(
				AttributeConstants.ATTRIBUTE_CONNECTION_TARGET_DECORATION,
				BAttributeConnectionTargetDecoration.DECORATION_NONE,
				AttributeConstants.ATTRIBUTE_CONNECTION);
		initAttribute(AttributeConstants.ATTRIBUTE_LABEL, "Label...",
				AttributeConstants.ATTRIBUTE_CONNECTION);
	}

}