/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AttributeTrackDirection;
import de.bmotionstudio.gef.editor.command.CreateCommand;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Signal extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.signal";

	public Signal(Visualization visualization) {

		super(visualization);

		int numberOfLights = 2;

		CreateCommand cmd;
		for (int i = 0; i < numberOfLights; i++) {
			Light light = new Light(visualization);
			cmd = new CreateCommand(light, this);
			cmd.setLayout(new Rectangle(0, 0, 12, 12));
			cmd.execute();
		}

	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
		// initAttribute(AttributeConstants.ATTRIBUTE_SIGNAL_COLOR,
		// AttributeSignalColor.NO_COLOR);
		initAttribute(AttributeConstants.ATTRIBUTE_TRACK_DIRECTION,
				AttributeTrackDirection.RIGHT);
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 48, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_LIGHTS, 2);
	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

}
