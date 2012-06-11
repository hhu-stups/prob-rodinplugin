/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IActionBars;

import de.bmotionstudio.gef.editor.action.BringToBottomAction;
import de.bmotionstudio.gef.editor.action.BringToBottomStepAction;
import de.bmotionstudio.gef.editor.action.BringToTopAction;
import de.bmotionstudio.gef.editor.action.BringToTopStepAction;
import de.bmotionstudio.gef.editor.action.FitImageAction;
import de.bmotionstudio.gef.editor.action.RenameAction;

public class InstallMenu implements IInstallMenu {

	public void installMenu(IMenuManager menu, ActionRegistry regitry) {

		IAction action;

		action = regitry.getAction(FitImageAction.ID);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		action = regitry.getAction(RenameAction.ID);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		// menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator(
		// "de.bmotionstudio.gef.BringToGroup"));

		MenuManager adjustmentMenu = new MenuManager("Adjustment");
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, adjustmentMenu);

		action = regitry.getAction(BringToTopAction.ID);
		adjustmentMenu.add(action);
		action = regitry.getAction(BringToBottomAction.ID);
		adjustmentMenu.add(action);
		action = regitry.getAction(BringToTopStepAction.ID);
		adjustmentMenu.add(action);
		action = regitry.getAction(BringToBottomStepAction.ID);
		adjustmentMenu.add(action);

	}

	public void installBar(IActionBars bars, ActionRegistry regitry) {
		// bars.setGlobalActionHandler(ActionFactory.RENAME.getId(), regitry
		// .getAction(ActionFactory.RENAME.getId()));
	}

}
