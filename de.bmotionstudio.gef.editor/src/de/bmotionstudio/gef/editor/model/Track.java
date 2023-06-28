/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
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
public class Track extends BConnection {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.track";

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
		aConnection.setGroup(AbstractAttribute.ROOT);
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

}
