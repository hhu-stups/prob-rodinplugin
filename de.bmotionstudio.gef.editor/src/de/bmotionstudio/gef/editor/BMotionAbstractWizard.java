/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.jface.wizard.Wizard;

import de.bmotionstudio.gef.editor.model.BControl;

public abstract class BMotionAbstractWizard extends Wizard {

	private BControl control;
	
	@Override
	public boolean performFinish() {
		return prepareToFinish();
	}
	
	public abstract String getName();

	protected abstract Boolean prepareToFinish();
	
}
