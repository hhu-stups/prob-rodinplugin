/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.attribute.BAttributeConnection;
import de.bmotionstudio.gef.editor.attribute.BAttributeConnectionSourceDecoration;
import de.bmotionstudio.gef.editor.attribute.BAttributeConnectionTargetDecoration;
import de.bmotionstudio.gef.editor.attribute.BAttributeForegroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeLabel;
import de.bmotionstudio.gef.editor.attribute.BAttributeLineStyle;
import de.bmotionstudio.gef.editor.attribute.BAttributeLineWidth;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Track extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.track";

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

		BAttributeConnection aConnection = new BAttributeConnection(null);
		initAttribute(aConnection);

		BAttributeLineWidth aLineWidth = new BAttributeLineWidth(1);
		aLineWidth.setGroup(aConnection);
		initAttribute(aLineWidth);

		BAttributeLineStyle aLineStyle = new BAttributeLineStyle(
				BAttributeLineStyle.SOLID_CONNECTION);
		aLineStyle.setGroup(aConnection);
		initAttribute(aLineStyle);

		BAttributeForegroundColor aForegroundColor = new BAttributeForegroundColor(
				new RGB(0, 0, 0));
		aForegroundColor.setGroup(aConnection);
		initAttribute(aForegroundColor);

		BAttributeConnectionSourceDecoration aSourceDeco = new BAttributeConnectionSourceDecoration(
				BAttributeConnectionSourceDecoration.DECORATION_NONE);
		aSourceDeco.setGroup(aConnection);
		initAttribute(aSourceDeco);

		BAttributeConnectionTargetDecoration aTargetDeco = new BAttributeConnectionTargetDecoration(
				BAttributeConnectionSourceDecoration.DECORATION_NONE);
		aTargetDeco.setGroup(aConnection);
		initAttribute(aTargetDeco);

		BAttributeLabel aLabel = new BAttributeLabel("Label ...");
		aLabel.setGroup(aConnection);
		initAttribute(aLabel);

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
