/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveRunFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();

		// ProB Event View (Top-Left)
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.15f, editorArea);
		left.addView("de.prob.ui.OperationView");

		// Navigator + Rodin Problem View (Bottom-Left)
		IFolderLayout leftb = layout.createFolder("leftb", IPageLayout.BOTTOM,
				0.6f, "left");
		leftb.addView("fr.systerel.explorer.navigator.view");
		// leftb.addView("org.eventb.ui.views.RodinProblemView");

		// ProB State View (Right)
		IFolderLayout right1 = layout.createFolder("right1", IPageLayout.RIGHT,
				0.7f, editorArea);
		right1.addView("de.prob.ui.StateView");

		// ProB History + ProB Event Error View (Right)
		IFolderLayout right2 = layout.createFolder("right2", IPageLayout.RIGHT,
				0.6f, "right1");
		right2.addView("de.prob.ui.HistoryView");
		right2.addView("de.prob.ui.EventErrorView");

	}

}
