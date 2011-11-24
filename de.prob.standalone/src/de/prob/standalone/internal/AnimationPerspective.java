package de.prob.standalone.internal;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

import de.prob.ui.PerspectiveFactory;

public class AnimationPerspective extends PerspectiveFactory {

	public void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// Place the project explorer to left of editor area.
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.30f, editorArea);
		left.addView("de.prob.ui.OperationView");

		IFolderLayout leftb = layout.createFolder("leftb", IPageLayout.BOTTOM,
				0.50f, "left");
		leftb.addView("de.prob.standalone.navigatorview");

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

}
