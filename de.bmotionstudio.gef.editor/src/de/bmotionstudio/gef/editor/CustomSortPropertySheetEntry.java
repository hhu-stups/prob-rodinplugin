/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetEntry;

/**
 * Overridden to allow specific ordering of properties. Order can be forced by
 * prepending text followed by a ':' for example:
 * 
 * a1:id a2:x a3:y a4:width a5:height
 * 
 * forces the above order.
 * 
 * When the name is displayed, the getDisplayName method removes the prefix.
 * 
 * Any number of colons maybe used, for example:
 * 
 * bean:property:x bean:eventset:action bean:property:y
 * 
 * (not a real, applicable example, but it gets the idea across)
 * 
 */
public class CustomSortPropertySheetEntry extends UndoablePropertySheetEntry {

	public CustomSortPropertySheetEntry(CommandStack stack) {
		super(stack);
	}

	protected PropertySheetEntry createChildEntry() {
		return new CustomSortPropertySheetEntry(getCommandStack());
	}

	@Override
	public String getDisplayName() {
		String displayName = super.getDisplayName();
		int colon = displayName.lastIndexOf(':');
		if (colon != -1)
			displayName = displayName.substring(colon + 1);
		return displayName;
	}

	public String getFullDisplayName() {
		return getDescriptor().getDisplayName();
	}

}