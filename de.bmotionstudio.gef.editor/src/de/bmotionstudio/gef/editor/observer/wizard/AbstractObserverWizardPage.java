/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.gef.editor.observer.Observer;

public class AbstractObserverWizardPage extends WizardPage {

	private Observer observer;

	protected AbstractObserverWizardPage(String pageName, Observer observer) {
		super(pageName);
		this.observer = observer;
	}

	@Override
	public void createControl(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(parent, observer.getClass().getName());
	}

}
