package de.bmotionstudio.gef.editor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.bmotionstudio.gef.editor.controlpanel.ControlPanel;
import de.bmotionstudio.gef.editor.library.LibraryView;
import de.bmotionstudio.gef.editor.observer.view.ObserverView;

public class BMSPerspectiveFactory implements IPerspectiveFactory {

	public static String ID = "de.bmotionstudio.gef.editor.perspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {

		layout.setEditorAreaVisible(false);
		layout.getViewLayout(VisualizationViewPart.ID).setCloseable(false);
		
		String editorArea = layout.getEditorArea();

		// ProB Event View
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.15f, editorArea);
		left.addView("de.prob.ui.OperationView");
		left.addView("de.prob.ui.HistoryView");
		left.addView("de.prob.ui.EventErrorView");

		// Event-B Explorer
		IFolderLayout leftb = layout.createFolder("leftb", IPageLayout.BOTTOM,
				0.65f, "left");
		leftb.addView("fr.systerel.explorer.navigator.view");

		// State View
		IFolderLayout main1 = layout.createFolder("main1", IPageLayout.BOTTOM,
				0.20f, editorArea);
		main1.addView("de.prob.ui.StateView");

		// Outline View + Palette View + Library View (Right)
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				0.80f, "main1");
		right.addView(BMSPaletteView.ID);
		right.addView(BMotionOutlineView.ID);

		// Library View
		IFolderLayout rightb = layout.createFolder("rightb",
				IPageLayout.BOTTOM, 0.65f, "right");
		rightb.addView(LibraryView.ID);

		// Properties view + observer view + control panel
		IFolderLayout bottom1 = layout.createFolder("bottom1",
				IPageLayout.BOTTOM, 0.65f, "main1");
		bottom1.addView(ControlPanel.ID);
		bottom1.addView(IPageLayout.ID_PROP_SHEET);
		bottom1.addView(ObserverView.ID);

		// Placeholder for new visualization views
		IFolderLayout main2 = layout.createFolder("main2", IPageLayout.RIGHT,
				0.50f, "main1");
		main2.addPlaceholder(VisualizationViewPart.ID + ":*");
		
	}

}
