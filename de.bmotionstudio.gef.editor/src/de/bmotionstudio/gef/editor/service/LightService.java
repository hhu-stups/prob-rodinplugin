package de.bmotionstudio.gef.editor.service;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Light;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.LightPart;

public class LightService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl(Visualization visualization) {
		return new Light(visualization);
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new LightPart();
	}

	@Override
	public boolean showInPalette() {
		return false;
	}

}
