/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.properties.UndoablePropertySheetPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetSorter;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionPropertyPage extends UndoablePropertySheetPage {

	public BMotionPropertyPage(CommandStack commandStack, IAction undoAction,
			IAction redoAction) {
		super(commandStack, undoAction, redoAction);
		setSorter(new PropertySheetSorter() {
			@Override
			public int compare(IPropertySheetEntry entryA,
					IPropertySheetEntry entryB) {
				return getCollator()
						.compare(
								((CustomSortPropertySheetEntry) entryA)
										.getFullDisplayName(),
								((CustomSortPropertySheetEntry) entryB)
										.getFullDisplayName());
			}
		});
	}

}
