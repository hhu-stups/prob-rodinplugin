/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.command.CreateCommand;

public class BTable extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.table";

	public BTable(Visualization visualization) {

		super(visualization);

		int numberOfColumns = 1;
		int numberOfRows = 1;

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
		initAttribute(AttributeConstants.ATTRIBUTE_SIZE, null, false, false,
				AbstractAttribute.ROOT);
		initAttribute(AttributeConstants.ATTRIBUTE_WIDTH, 0, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 0, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR,
				ColorConstants.black.getRGB());
		initAttribute(AttributeConstants.ATTRIBUTE_COLUMNS, 1);
		initAttribute(AttributeConstants.ATTRIBUTE_ROWS, 1);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
