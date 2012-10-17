/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.attribute.AttributeSwitchDirection;
import de.bmotionstudio.gef.editor.attribute.AttributeSwitchPosition;
import de.bmotionstudio.gef.editor.command.CreateCommand;
import de.bmotionstudio.gef.editor.command.TrackCreateCommand;

public class Switch extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.switch";

	private transient Track track1;

	private transient Track track2;

	public Switch(Visualization visualization) {

		super(visualization);

		// Build up switch
		TrackNode tracknode1 = new TrackNode(getVisualization());
		CreateCommand cmd = new CreateCommand(tracknode1, this);
		cmd.setLayout(new Rectangle(5, 0, 50, 20));
		cmd.execute();

		TrackNode tracknode2 = new TrackNode(getVisualization());
		cmd = new CreateCommand(tracknode2, this);
		cmd.setLayout(new Rectangle(70, 0, 50, 20));
		cmd.execute();

		TrackNode tracknode3 = new TrackNode(getVisualization());
		cmd = new CreateCommand(tracknode3, this);
		cmd.setLayout(new Rectangle(70, 70, 50, 20));
		cmd.execute();

		TrackCreateCommand trackCreateCmd = new TrackCreateCommand(tracknode1);
		trackCreateCmd.setTarget(tracknode2);
		track1 = new Track(getVisualization());
		trackCreateCmd.setTrack(track1);
		trackCreateCmd.execute();

		trackCreateCmd = new TrackCreateCommand(tracknode1);
		trackCreateCmd.setTarget(tracknode3);
		track2 = new Track(getVisualization());
		trackCreateCmd.setTrack(track2);
		trackCreateCmd.execute();

		track1.setAttributeValue(AttributeConstants.ATTRIBUTE_LABEL, "");
		track2.setAttributeValue(AttributeConstants.ATTRIBUTE_LABEL, "");

		track1.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "LEFT");
		track1.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM).setEditable(
				false);

		track2.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "RIGHT");
		track2.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM).setEditable(
				false);

		tracknode1.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "1");
		tracknode1.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM)
				.setEditable(false);
		tracknode1.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES)
				.setShow(false);

		tracknode2.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "2");
		tracknode2.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM)
				.setEditable(false);
		tracknode2.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES)
				.setShow(false);

		tracknode3.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "3");
		tracknode3.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM)
				.setEditable(false);
		tracknode3.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES)
				.setShow(false);

	}

	@Override
	protected void initAttributes() {

		initAttribute(AttributeConstants.ATTRIBUTE_WIDTH, 100,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 50,
				AttributeConstants.ATTRIBUTE_SIZE);

		initAttribute(AttributeConstants.ATTRIBUTE_SWITCH_DIRECTION,
				AttributeSwitchDirection.RIGHT_SOUTH);
		initAttribute(AttributeConstants.ATTRIBUTE_SWITCH_POSITION,
				AttributeSwitchPosition.UNKNOWN);

		for (BControl c : getChildrenArray()) {
			c.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM).setShow(false);
			c.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM).setEditable(
					false);
			c.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES).setShow(
					false);
			c.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES)
					.setEditable(false);
		}

	}

	// We have to set the two tracks of the switch, since their are set to
	// transient
	protected Object readResolve() {
		super.readResolve();
		for (BControl control : getChildrenArray()) {
			for (Track n : ((TrackNode) control).getTargetTracks()) {
				AbstractAttribute a2 = n
						.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM);
				a2.setEditable(false);
				if (a2.getValue().equals("LEFT")) {
					track1 = n;
				} else if (a2.getValue().equals("RIGHT")) {
					track2 = n;
				}
			}
		}
		return this;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

	public Track getTrack1() {
		return track1;
	}

	public Track getTrack2() {
		return track2;
	}

}
