/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;

import de.bmotionstudio.gef.editor.figure.UnknownBControl;
import de.bmotionstudio.gef.editor.model.BControl;

public class UnknownPart extends AppAbstractEditPart {

	public static String ID = "de.bmotionstudio.gef.editor.unknown";

	private String type;

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new UnknownBControl();
		return figure;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {
		((UnknownBControl) figure).setMessage(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void prepareEditPolicies() {
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
