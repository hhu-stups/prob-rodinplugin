/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.standalone.internal;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class VisualizationRunPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();

		// ProB Event View (Top-Left)
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.30f, editorArea);
		left.addView("de.prob.ui.OperationView");

		// ProB State View (Bottom)
		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.35f, "left");
		bottom.addView("de.prob.ui.StateView");
		bottom.addView("de.prob.ui.HistoryView");
		bottom.addView("de.prob.ui.EventErrorView");
		bottom.addView("de.prob.standalone.navigatorview");

	}

}
