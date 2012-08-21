package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeForegroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeText;
import de.bmotionstudio.gef.editor.attribute.BAttributeTextColor;

public class BTableCell extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tablecell";

	public BTableCell(Visualization visualization) {
		super(visualization);
	}

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeBackgroundColor(
				ColorConstants.white.getRGB()));
		BAttributeForegroundColor bAttributeForegroundColor = new BAttributeForegroundColor(
				ColorConstants.black.getRGB());
		bAttributeForegroundColor.setShow(false);
		initAttribute(bAttributeForegroundColor);
		initAttribute(new BAttributeTextColor(ColorConstants.black.getRGB()));
		initAttribute(new BAttributeText(""));
		setAttributeValue(AttributeConstants.ATTRIBUTE_HEIGHT, 20);
		setAttributeValue(AttributeConstants.ATTRIBUTE_WIDTH, 50);
		getAttributes().get(AttributeConstants.ATTRIBUTE_HEIGHT).setEditable(
				false);
		getAttributes().get(AttributeConstants.ATTRIBUTE_WIDTH).setEditable(
				true);
		getAttributes().get(AttributeConstants.ATTRIBUTE_HEIGHT).setShow(false);
		getAttributes().get(AttributeConstants.ATTRIBUTE_COORDINATES).setShow(
				false);

	}

	@Override
	public String getType() {
		return TYPE;
	}

}
