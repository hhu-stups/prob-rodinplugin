/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.prob.logging.Logger;

public class PerspectiveFactory implements IPerspectiveFactory {

	private static final String PROB_PERSPECTIVE = "de.prob.ui.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// Place the project explorer to left of editor area.
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.30f, editorArea);
		left.addView("de.prob.ui.OperationView");

		IFolderLayout leftb = layout.createFolder("leftb", IPageLayout.BOTTOM,
				0.50f, "left");
		leftb.addView("fr.systerel.explorer.navigator.view");
		leftb.addView("org.eventb.ui.views.RodinProblemView");

		// Place the outline to right of editor area.
		IFolderLayout right1 = layout.createFolder("right1", IPageLayout.RIGHT,
				0.6f, editorArea);
		right1.addView("de.prob.ui.StateView");
		right1.addView("de.prob.ui.ltl.CounterExampleView");
		IFolderLayout right2 = layout.createFolder("right2", IPageLayout.RIGHT,
				0.6f, "right1");
		right2.addView("de.prob.ui.HistoryView");
		right2.addView("de.prob.ui.EventErrorView");
	}

	public static void openPerspective() {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.showPerspective(PROB_PERSPECTIVE,
					workbench.getActiveWorkbenchWindow());
			workbench.getActiveWorkbenchWindow().addPerspectiveListener(new PerspectiveListener());
		} catch (WorkbenchException e) {
			final String message = "Error opening ProB perspective.";
			Logger.notifyUser(message, e);
		}
	}

}
