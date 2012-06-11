/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.ui.part.WorkbenchPart;

import de.bmotionstudio.gef.editor.action.BringToBottomAction;
import de.bmotionstudio.gef.editor.action.BringToBottomStepAction;
import de.bmotionstudio.gef.editor.action.BringToTopAction;
import de.bmotionstudio.gef.editor.action.BringToTopStepAction;
import de.bmotionstudio.gef.editor.action.FitImageAction;
import de.bmotionstudio.gef.editor.action.RenameAction;

public class InstallActions extends AbstractInstallActions implements
		IInstallActions {

	public void installActions(final WorkbenchPart part) {
		installAction(RenameAction.ID, new RenameAction(part));
		installAction(FitImageAction.ID, new FitImageAction(part));
		installAction(BringToTopAction.ID, new BringToTopAction(part));
		installAction(BringToBottomAction.ID, new BringToBottomAction(part));
		installAction(BringToTopStepAction.ID, new BringToTopStepAction(part));
		installAction(BringToBottomStepAction.ID, new BringToBottomStepAction(
				part));
	}

}
