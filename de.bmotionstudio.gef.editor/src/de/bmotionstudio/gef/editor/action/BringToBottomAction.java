/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.command.BringToBottomCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.VisualizationPart;

public class BringToBottomAction extends SelectionAction {

	public final static String ID = "de.bmotionstudio.gef.editor.action.bringToBottom";

	public BringToBottomAction(final IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	protected void init() {
		setText("Bring to bottom");
		setToolTipText("Bring to bottom");
		setId(ID);
		ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_bringtobottom.gif");
		if (icon != null) {
			setImageDescriptor(icon);
		}
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		List<?> selectedObjects = getSelectedObjects();
		if (selectedObjects.size() == 1) {
			if (selectedObjects.get(0) instanceof VisualizationPart) {
				return false;
			}
		}
		return true;
	}

	public BringToBottomCommand createBringToBottomCommand(
			List<BControl> modelList) {
		BringToBottomCommand command = new BringToBottomCommand();
		command.setControlList(modelList);
		return command;
	}

	public void run() {

		List<BControl> modelList = new ArrayList<BControl>();

		List<?> selectedObjects = getSelectedObjects();

		for (Object obj : selectedObjects) {
			if (obj instanceof BMSAbstractEditPart) {
				modelList
						.add((BControl) ((BMSAbstractEditPart) obj).getModel());
			}
		}

		execute(createBringToBottomCommand(modelList));

	}

}
