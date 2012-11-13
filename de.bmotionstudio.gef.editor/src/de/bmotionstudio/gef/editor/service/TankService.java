/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.service;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Tank;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.TankPart;

public class TankService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl(Visualization visualization) {
		return new Tank(visualization);
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new TankPart();
	}

}
