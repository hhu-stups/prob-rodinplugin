/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import java.util.HashMap;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.model.BControl;

public class RenameAction extends SelectionAction {

	public final static String ID = ActionFactory.RENAME.getId();

	public RenameAction(final IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	protected void init() {
		setText("Rename...");
		setToolTipText("Rename");
		setId(ID);
		ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_rename.png");
		if (icon != null) {
			setImageDescriptor(icon);
		}
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createRenameCommand("");
		if (cmd == null) {
			return false;
		}
		return true;
	}

	public Command createRenameCommand(final String name) {
		Request renameReq = new Request("rename");

		HashMap<String, String> reqData = new HashMap<String, String>();
		reqData.put("newName", name);
		renameReq.setExtendedData(reqData);

		if (getSelectedObjects().size() > 0) {
			if (getSelectedObjects().get(0) instanceof EditPart) {
				EditPart object = (EditPart) getSelectedObjects().get(0);
				Command cmd = object.getCommand(renameReq);
				return cmd;
			}
		}

		return null;
	}

	public void run() {
		BControl bcontrol = getSelectedBControl();
		RenameWizard wizard = new RenameWizard(bcontrol.getAttributeValue(
				AttributeConstants.ATTRIBUTE_TEXT).toString());
		WizardDialog dialog = new WizardDialog(getWorkbenchPart().getSite()
				.getShell(), wizard);
		dialog.create();
		dialog.getShell().setSize(400, 315);
		dialog.getShell().setText("BMotion Studio Rename Wizard");

		if (dialog.open() == WizardDialog.OK) {
			String name = wizard.getRenameValue();
			execute(createRenameCommand(name));
		}
	}

	private BControl getSelectedBControl() {
		List<?> objects = getSelectedObjects();
		if (objects.isEmpty()) {
			return null;
		}
		if (!(objects.get(0) instanceof EditPart)) {
			return null;
		}
		EditPart part = (EditPart) objects.get(0);
		return (BControl) part.getModel();
	}

}
