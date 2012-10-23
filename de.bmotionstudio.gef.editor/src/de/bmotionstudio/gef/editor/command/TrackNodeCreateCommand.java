/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.model.Track;
import de.bmotionstudio.gef.editor.model.TrackNode;

public class TrackNodeCreateCommand extends TrackCreateCommand {


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
	public TrackNodeCreateCommand(TrackNode source) {
		super(source);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {

		System.out.println("EXECUTE: ");

		CreateCommand createCmd1 = new CreateCommand(getSource(), getSource()
				.getVisualization());
		createCmd1.setLayout(new Rectangle(getSource().getLocation().x - 10,
				getSource()
				.getLocation().y - 10, 50, 20));
		createCmd1.execute();

		createCmd1 = new CreateCommand(getTarget(), getTarget().getVisualization());
		createCmd1.setLayout(new Rectangle(getTarget().getLocation().x - 10, getTarget()
				.getLocation().y - 10, 50, 20));
		createCmd1.execute();

		// create a new connection between source and target
		getTrack().setSource(getSource());
		getTrack().setTarget(getTarget());
		getTrack().reconnect();

	}

}
