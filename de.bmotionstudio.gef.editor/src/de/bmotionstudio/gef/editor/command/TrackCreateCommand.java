/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.Track;
import de.bmotionstudio.gef.editor.model.TrackNode;

public class TrackCreateCommand extends Command {

	/** The track instance. */
	private Track track;

	/** Start endpoint for the track. */
	private final TrackNode source;
	/** Target endpoint for the track. */
	private TrackNode target;

	/**
	 * Instantiate a command that can create a {@link Track} between two
	 * {@link TrackNode}s.
	 * 
	 * @param source
	 *            the source {@link TrackNode}
	 * @param lineStyle
	 *            the desired line style. See Connection#setLineStyle(int) for
	 *            details
	 * @throws IllegalArgumentException
	 *             if source is null
	 * @see Connection#setLineStyle(int)
	 */
	public TrackCreateCommand(TrackNode source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		setLabel("track creation");
		this.source = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		// disallow source -> source connections
		if (source.equals(target)) {
			return false;
		}
		// return false, if the source -> target connection exists already
		for (Iterator<Track> iter = source.getSourceTracks().iterator(); iter
				.hasNext();) {
			Track conn = (Track) iter.next();
			if (conn.getTarget().equals(target)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// create a new connection between source and target
		track.setSource(source);
		track.setTarget(target);
		track.reconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		track.reconnect();
	}

	/**
	 * Set the target endpoint for the connection.
	 * 
	 * @param target
	 *            that target endpoint (a non-null Shape instance)
	 * @throws IllegalArgumentException
	 *             if target is null
	 */
	public void setTarget(TrackNode target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		track.disconnect();
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	public Track getTrack() {
		return this.track;
	}

	public TrackNode getSource() {
		return this.source;
	}

	public TrackNode getTarget() {
		return this.target;
	}

}
