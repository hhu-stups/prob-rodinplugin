package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeColumns;
import de.bmotionstudio.gef.editor.attribute.BAttributeForegroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeRows;
import de.bmotionstudio.gef.editor.command.CreateCommand;

public class BTable extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.table";

	public BTable(Visualization visualization) {

		super(visualization);

		int numberOfColumns = 2;
		int numberOfRows = 2;

		CreateCommand cmd;
		for (int i = 0; i < numberOfColumns; i++) {
			BTableColumn bTableColumn = new BTableColumn(visualization);
			cmd = new CreateCommand(bTableColumn, this);
			cmd.setLayout(new Rectangle(0, 0, 50, 25));
			cmd.execute();
			for (int z = 0; z < numberOfRows; z++) {
				cmd = new CreateCommand(new BTableCell(getVisualization()),
						bTableColumn);
				cmd.setLayout(new Rectangle(0, 0, 50, 20));
				cmd.execute();
			}
		}

	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

	@Override
	protected void initAttributes() {
		initAttribute(new BAttributeForegroundColor(
				ColorConstants.black.getRGB()));
		initAttribute(new BAttributeColumns(2));
		initAttribute(new BAttributeRows(2));
		getAttributes().get(AttributeConstants.ATTRIBUTE_SIZE).setShow(false);
		getAttributes().get(AttributeConstants.ATTRIBUTE_COORDINATES).setShow(
				false);
		getAttributes().get(AttributeConstants.ATTRIBUTE_HEIGHT).setEditable(
				false);
		getAttributes().get(AttributeConstants.ATTRIBUTE_WIDTH).setEditable(
				false);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
