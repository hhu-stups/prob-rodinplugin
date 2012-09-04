/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.service;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BShape;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.BShapePart;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BRectangleService extends AbstractBControlService implements
		IBControlService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.bmotionstudio.gef.editor.IBControlService#createControl(de.bmotionstudio
	 * .gef.editor.model.Visualization)
	 */
	@Override
	public BControl createControl(Visualization visualization) {
		return new BShape(visualization);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.IBControlService#createEditPart()
	 */
	@Override
	public BMSAbstractEditPart createEditPart() {
		return new BShapePart();
	}

}
