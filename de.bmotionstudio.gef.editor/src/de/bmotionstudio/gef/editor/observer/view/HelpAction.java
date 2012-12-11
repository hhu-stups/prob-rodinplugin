package de.bmotionstudio.gef.editor.observer.view;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.gef.editor.BMotionStudioImage;

public class HelpAction extends Action {

	private String observerID;

	public HelpAction() {
		setText("Show help...");
		setImageDescriptor(BMotionStudioImage
				.getImageDescriptor("icons/eclipse16/linkto_help.gif"));
		setEnabled(false);
	}

	@Override
	public void run() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(observerID);
	}

	public void setObserverID(String observerID) {
		this.observerID = observerID;
	}

}
