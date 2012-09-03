package de.bmotionstudio.gef.editor.service;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.attribute.BAttributeShape;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BShape;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.BShapePart;

public class BEllipseService extends AbstractBControlService implements
		IBControlService {

	public BEllipseService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public BControl createControl(Visualization visualization) {
		BShape ellipse = new BShape(visualization);
		ellipse.setAttributeValue(AttributeConstants.ATTRIBUTE_SHAPE,
				BAttributeShape.SHAPE_OVAL);
		return ellipse;
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new BShapePart();
	}

}
