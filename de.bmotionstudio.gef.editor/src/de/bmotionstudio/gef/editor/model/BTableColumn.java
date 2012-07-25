package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeForegroundColor;

public class BTableColumn extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tablecolumn";

	public BTableColumn(Visualization visualization) {
		super(visualization);
	}

	@Override
	protected void initAttributes() {

		// initAttribute(new BAttributeBackgroundColor(
		// ColorConstants.white.getRGB()));
		BAttributeForegroundColor bAttributeForegroundColor = new BAttributeForegroundColor(
				ColorConstants.black.getRGB());
		bAttributeForegroundColor.setShow(false);
		initAttribute(bAttributeForegroundColor);
		// initAttribute(new
		// BAttributeTextColor(ColorConstants.black.getRGB()));
		getAttributes().get(AttributeConstants.ATTRIBUTE_HEIGHT).setEditable(
				false);
		getAttributes().get(AttributeConstants.ATTRIBUTE_HEIGHT).setShow(false);
		getAttributes().get(AttributeConstants.ATTRIBUTE_COORDINATES).setShow(
				false);

		// Background Color
		// Font
		// Foreground Color

		// Width
		// Height (not editable) --> determined by number of cells

	}

	@Override
	public String getType() {
		return TYPE;
	}

}