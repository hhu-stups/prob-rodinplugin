/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.attribute.AttributeLights;
import de.bmotionstudio.gef.editor.attribute.AttributeTrackDirection;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeLabel;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
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

		BAttributeHeight aHeight = new BAttributeHeight(48);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		initAttribute(new AttributeTrackDirection(AttributeTrackDirection.RIGHT));
		initAttribute(new AttributeLights(2));

		BAttributeLabel aLabel = new BAttributeLabel("Signal");
		initAttribute(aLabel);

	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

}
