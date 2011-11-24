/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import de.bmotionstudio.gef.editor.command.PasteCommand;

public class PasteAction extends SelectionAction {

	public PasteAction(IWorkbenchPart part) {
		super(part);
		// force calculateEnabled() to be called in every context
		setLazyEnablementCalculation(true);
	}

	protected void init() {
		super.init();
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setText("Paste");
		setId(ActionFactory.PASTE.getId());
		setHoverImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setEnabled(false);
	}

	private PasteCommand createPasteCommand() {
		return new PasteCommand();
	}

	@Override
	protected boolean calculateEnabled() {
		Command command = createPasteCommand();
		return command != null && command.canExecute();
	}

	@Override
	public void run() {
		PasteCommand command = createPasteCommand();
		if (command != null && command.canExecute())
			execute(command);
	}

}
