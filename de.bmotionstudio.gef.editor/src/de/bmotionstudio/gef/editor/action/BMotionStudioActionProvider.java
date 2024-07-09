/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.internal.BMotionStudioRodinFile;

public class BMotionStudioActionProvider extends CommonActionProvider {

	public static String GROUP_FILEACTIONS = "fileactionsGroup";

	// StructuredViewer viewer;

	ICommonActionExtensionSite site;

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		super.init(aSite);
		site = aSite;
		// viewer = aSite.getStructuredViewer();
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
				ActionCollection.getOpenAction(site));
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				ActionCollection.getDeleteAction(site));
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(new GroupMarker(GROUP_FILEACTIONS));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN,
				ActionCollection.getOpenAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH,
				buildOpenWithMenu());
		menu.appendToGroup(GROUP_FILEACTIONS,
				ActionCollection.getDeleteAction(site));
	}

	MenuManager buildOpenWithMenu() {
		MenuManager menu = new MenuManager("Open With",
				ICommonMenuConstants.GROUP_OPEN_WITH);
		ISelection selection = site.getStructuredViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		menu.add(new OpenWithMenu(BMotionEditorPlugin.getActivePage(),
				((BMotionStudioRodinFile) obj).getResource()));
		return menu;
	}

}
