/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.property;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.graphics.Image;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;

public class CheckboxCellEditorHelper {

	public CheckboxCellEditorHelper() {
		super();
	}

	/**
	 * To be used by LabelProviders that whant to display a checked/unchecked
	 * icon for the CheckboxCellEditor that does not have a Control.
	 * 
	 * @param cellModifier
	 *            The ICellModifier for the CellEditor to provide the value
	 * @param element
	 *            The current element
	 * @param property
	 *            The property the cellModifier should return the value from
	 */
	public static Image getCellEditorImage(ICellModifier cellModifier,
			Object element, String property) {
		Boolean value = (Boolean) cellModifier.getValue(element, property);
		return getCellEditorImage(value);
	}

	/**
	 * returns an checked checkbox image if value if true and an unchecked
	 * checkbox image if false
	 * 
	 * @param value
	 *            the value to get the cooresponding image for
	 * @param disabled
	 *            determines if the image should be disabled or not
	 * @return an checked checkbox image if value if true and an unchecked
	 *         checkbox image if false
	 * 
	 */
	public static Image getCellEditorImage(boolean value) {
		Image image = null;
		if (value)
			image = BMotionStudioImage
					.getImage(EditorImageRegistry.IMG_ICON_CHECKED);
		else
			image = BMotionStudioImage
					.getImage(EditorImageRegistry.IMG_ICON_UNCHECKED);

		return image;

	}

}
