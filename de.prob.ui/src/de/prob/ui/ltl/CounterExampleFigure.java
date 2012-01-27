package de.prob.ui.ltl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import de.prob.core.domainobjects.ltl.CounterExample;

public final class CounterExampleFigure extends Figure {
	private final CounterExample model;
	private final List<RectangleFigure> states = new ArrayList<RectangleFigure>();

	public CounterExampleFigure(CounterExample model) {
		this.model = model;

		setLayoutManager(new XYLayout());

		final AbstractLabeledBorder border = new GroupBoxBorder();
		border.setTextColor(ColorConstants.lightBlue);

		final Font font = new Font(Display.getDefault(), "Arial", 10, SWT.BOLD);
		border.setFont(font);
		border.setLabel(model.getPropositionRoot() + ", PathType - "
				+ model.getPathType().name());
		setBorder(border);

		for (int i = 0; i < model.getStates().size(); i++) {
			final RectangleFigure state = new RectangleFigure();
			states.add(state);
		}
	}

	public List<RectangleFigure> getStates() {
		return states;
	}

	public CounterExample getModel() {
		return model;
	}

	public void update() {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		final CounterExampleViewPart counterExampleView = (CounterExampleViewPart) workbenchPage
				.findView(CounterExampleViewPart.ID);

		if (counterExampleView != null) {
			final int currentIndex = counterExampleView.getCurrentIndex();

			for (int i = 0; i < states.size(); i++) {
				states.get(i).setVisible(false);

				if (i == currentIndex) {
					states.get(i).setVisible(true);
				}
			}
		}
	}
}
