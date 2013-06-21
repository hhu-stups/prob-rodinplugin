package de.bmotionstudio.gef.editor.model.service;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BTable;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.BTablePart;

public class BTableService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl(Visualization visualization) {
		return new BTable(visualization);
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new BTablePart();
	}

}
