package de.bmotionstudio.gef.editor.service;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BTableCell;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BControlTreeEditPart;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.BMSAbstractTreeEditPart;
import de.bmotionstudio.gef.editor.part.BTableCellPart;

public class BTableCellService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl(Visualization visualization) {
		return new BTableCell(visualization);
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new BTableCellPart();
	}

	@Override
	public boolean showInPalette() {
		return false;
	}

	@Override
	public BMSAbstractTreeEditPart createTreeEditPart() {
		return new BControlTreeEditPart() {
			@Override
			protected void createEditPolicies() {
			}
		};
	}

}
