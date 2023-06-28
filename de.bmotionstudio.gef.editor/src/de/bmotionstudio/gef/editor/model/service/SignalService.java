/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.model.service;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Signal;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.SignalPart;

/**
 * @author Lukas Ladenberger
 * 
 */
public class SignalService extends AbstractBControlService implements
		IBControlService {
	@Override
	public BControl createControl(Visualization visualization) {
		return new Signal(visualization);
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new SignalPart();
	}

}
