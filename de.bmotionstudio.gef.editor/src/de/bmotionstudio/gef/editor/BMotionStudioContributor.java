/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

public class BMotionStudioContributor extends ActionBarContributor {

	@Override
	protected void buildActions() {
		IWorkbenchWindow iww = getPage().getWorkbenchWindow();

		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());

		addRetargetAction(new DeleteRetargetAction());

		addRetargetAction((RetargetAction) ActionFactory.COPY.create(iww));
		addRetargetAction((RetargetAction) ActionFactory.PASTE.create(iww));

		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());

		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_RULER_VISIBILITY, "Ruler",
				IAction.AS_CHECK_BOX));

		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_GRID_VISIBILITY, "Grid",
				IAction.AS_CHECK_BOX));

		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY, "Snap to Geometry",
				IAction.AS_CHECK_BOX));
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
		toolBarManager.add(getAction(ActionFactory.COPY.getId()));
		toolBarManager.add(getAction(ActionFactory.PASTE.getId()));
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
	}

	@Override
	protected void declareGlobalActionKeys() {
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager) {

		super.contributeToMenu(menuManager);

		IContributionItem bMenu = menuManager
				.find("de.bmotionstudio.gef.editor.menu");
		if (bMenu != null) {

			IMenuManager bmotionMenu = (IMenuManager) bMenu;
			MenuManager viewMenu = new MenuManager("Editor");
			viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
			viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
			viewMenu.add(new Separator());
			viewMenu.add(getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY));
			viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
			viewMenu.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
			bmotionMenu
					.insertAfter(
							"de.bmotionstudio.gef.editor.command.openBMotionStudioWebsite",
							viewMenu);

		}

	}

	public void setActiveEditor(IEditorPart editor) {
		if (editor instanceof BMotionStudioEditor) {
			super.setActiveEditor(((BMotionStudioEditor) editor).getEditPage());
		} else {
			super.setActiveEditor(editor);
		}
	}

}
