package de.prob.ui.ltl;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import de.prob.core.domainobjects.ltl.CounterExample;

public final class CounterExampleFigure extends Figure {
	private final CounterExample model;

	public CounterExampleFigure(CounterExample model) {
		this.model = model;
		setLayoutManager(new XYLayout());
		AbstractLabeledBorder border = new GroupBoxBorder();
		border.setTextColor(ColorConstants.lightBlue);
		Font font = new Font(null, "Arial", 10, SWT.BOLD);
		border.setFont(font);
		border.setLabel(model.getPropositionRoot() + ", PathType - "
				+ model.getPathType().name());
		setBorder(border);
	}

	public CounterExample getModel() {
		return model;
	}

	public void update() {
	}
}
