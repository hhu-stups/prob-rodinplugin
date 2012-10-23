/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.attribute.BAttributeLineStyle;

/**
 * @author Lukas Ladenberger
 * 
 */
public class TrackNode extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tracknode";

	private List<Track> sourceTracks;

	private List<Track> targetTracks;

	public TrackNode(Visualization visualization) {
		super(visualization);
		this.sourceTracks = new ArrayList<Track>();
		this.targetTracks = new ArrayList<Track>();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
		initAttribute(AttributeConstants.ATTRIBUTE_SIZE, null, false, false,
				AbstractAttribute.ROOT);
		initAttribute(AttributeConstants.ATTRIBUTE_WIDTH, 20, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 20, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR,
				ColorConstants.black.getRGB());
		initAttribute(AttributeConstants.ATTRIBUTE_LINEWIDTH, 1);
		initAttribute(AttributeConstants.ATTRIBUTE_LINESTYLE,
				BAttributeLineStyle.SOLID_CONNECTION);
	}

	public List<Track> getSourceTracks() {
		return sourceTracks;
	}

	public List<Track> getTargetTracks() {
		return targetTracks;
	}

	/**
	 * Add an incoming or outgoing connection to this shape.
	 * 
	 * @param track
	 *            a non-null connection instance
	 * @throws IllegalArgumentException
	 *             if the connection is null or has not distinct endpoints
	 */
	public void addTrack(Track track) {
		if (track == null || track.getSource() == track.getTarget()) {
			throw new IllegalArgumentException();
		}
		track.setVisualization(getVisualization());
		if (track.getSource() == this) {
			getSourceTracks().add(track);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null,
					track);
		} else if (track.getTarget() == this) {
			getTargetTracks().add(track);
			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null,
					track);
		}
	}

	/**
	 * Remove an incoming or outgoing connection from this shape.
	 * 
	 * @param track
	 *            a non-null connection instance
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	public void removeTrack(Track track) {
		if (track == null) {
			throw new IllegalArgumentException();
		}
		if (track.getSource() == this) {
			getSourceTracks().remove(track);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null,
					track);
		} else if (track.getTarget() == this) {
			getTargetTracks().remove(track);
			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null,
					track);
		}
	}

	@Override
	protected void populateVisualization(Visualization visualization) {
		for (Track t : getTargetTracks())
			t.setVisualization(visualization);
		for (Track t : getSourceTracks())
			t.setVisualization(visualization);
		super.populateVisualization(visualization);
	}

	@Override
	public void checkObserver(Animation animation) {
		super.checkObserver(animation);
		// TODO: Currently connection observer are checked twice (source +
		// target) => change this, so that observer are checked only on time per
		// state!!!
		for (Track t : getTargetTracks()) {
			t.checkObserver(animation);
		}
		for (Track t : getSourceTracks()) {
			t.checkObserver(animation);
		}
	}

}
