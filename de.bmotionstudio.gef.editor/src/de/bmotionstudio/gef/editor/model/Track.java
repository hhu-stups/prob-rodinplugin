/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeConnectionSourceDecoration;
import de.bmotionstudio.gef.editor.attribute.BAttributeLineStyle;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Track extends BControl {

	public static transient String TYPE = "de.bmotionstudio.addon.industry.track";

	/** True, if the connection is attached to its endpoints. */
	private boolean isConnected;
	/** Track's source endpoint. */
	private TrackNode source;
	/** Track's target endpoint. */
	private TrackNode target;

	public Track(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {

		initAttribute(AttributeConstants.ATTRIBUTE_LINEWIDTH, 1);
		initAttribute(AttributeConstants.ATTRIBUTE_LINESTYLE,
				BAttributeLineStyle.SOLID_CONNECTION);
		initAttribute(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR,
				ColorConstants.black.getRGB());
		initAttribute(
				AttributeConstants.ATTRIBUTE_CONNECTION_SOURCE_DECORATION,
				BAttributeConnectionSourceDecoration.DECORATION_NONE);
		initAttribute(
				AttributeConstants.ATTRIBUTE_CONNECTION_TARGET_DECORATION,
				BAttributeConnectionSourceDecoration.DECORATION_NONE);
		initAttribute(AttributeConstants.ATTRIBUTE_LABEL, "Label...");

	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public TrackNode getSource() {
		return source;
	}

	public void setSource(TrackNode source) {
		this.source = source;
	}

	public TrackNode getTarget() {
		return target;
	}

	public void setTarget(TrackNode target) {
		this.target = target;
	}

	/**
	 * Reconnect this connection. The connection will reconnect with the shapes
	 * it was previously attached to.
	 */
	public void reconnect() {
		if (!isConnected) {
			source.addTrack(this);
			target.addTrack(this);
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
	public void reconnect(TrackNode newSource, TrackNode newTarget) {
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}

	/**
	 * Disconnect this connection from the shapes it is attached to.
	 */
	public void disconnect() {
		if (isConnected) {
			source.removeTrack(this);
			target.removeTrack(this);
			isConnected = false;
		}
	}

}
