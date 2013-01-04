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
		
		String editorArea = layout.getEditorArea();

		// ProB Event View (Top-Left)
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.15f, editorArea);
		left.addView("de.prob.ui.OperationView");

		IFolderLayout leftb = layout.createFolder("leftb", IPageLayout.BOTTOM,
				0.4f, "left");
		leftb.addView("de.prob.ui.StateView");
		leftb.addView("de.prob.ui.HistoryView");
		leftb.addView("de.prob.ui.EventErrorView");

		// Outline View + Palette View + Library View (Right)
		IFolderLayout right1 = layout.createFolder("right1", IPageLayout.RIGHT,
				0.80f, editorArea);
		right1.addView(BMSPaletteView.ID);
		right1.addView(BMotionOutlineView.ID);

		// Library View + Event-B Explorer
		IFolderLayout right2 = layout.createFolder("right2",
				IPageLayout.BOTTOM, 0.60f, "right1");
		right2.addView(LibraryView.ID);
		right2.addView("fr.systerel.explorer.navigator.view");

		// Placeholder for new visualization views
		IFolderLayout top = layout.createFolder("bottom1", IPageLayout.BOTTOM,
				0.20f, IPageLayout.ID_EDITOR_AREA);
		top.addPlaceholder(VisualizationViewPart.ID + ":*");
		layout.getViewLayout(VisualizationViewPart.ID).setCloseable(false);

		// Properties view + observer view + control panel
		IFolderLayout bottom = layout.createFolder("bottom2",
				IPageLayout.BOTTOM, 0.65f, "bottom1");
		bottom.addView(ControlPanel.ID);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(ObserverView.ID);
		
	}

}