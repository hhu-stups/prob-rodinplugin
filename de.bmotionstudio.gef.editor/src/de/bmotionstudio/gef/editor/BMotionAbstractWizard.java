/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.model.BControl;

public abstract class BMotionAbstractWizard extends Window {

	private BControl control;
	
	public BMotionAbstractWizard(Shell shell, BControl control) {
		super(shell);
		this.control = control;
	}

	public BControl getBControl() {
		return this.control;
	}

	public abstract String getName();
	
}
