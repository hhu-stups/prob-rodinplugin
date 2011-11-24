/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.command.CreateCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BText;
import de.bmotionstudio.gef.editor.observer.SimpleValueDisplay;

public class LibraryVariableCommand extends AbstractLibraryCommand {

	private BControl newControl;

	public void execute() {

		attributeName = AttributeConstants.ATTRIBUTE_TEXT;
		attributeValue = transferObject.getLibraryObject().getName();
		oldAttributeValue = getCastedModel().getAttributeValue(attributeName);

		SimpleValueDisplay observer = new SimpleValueDisplay();
		observer.setEval(attributeValue.toString());
		observer.setReplacementString("%%VALUE%%");

		if (getCastedModel().canHaveChildren()) {

			newControl = new BText(getCastedModel().getVisualization());
			newControl.setAttributeValue(attributeName, attributeValue
					+ " = %%VALUE%%");
			newControl.addObserver(observer);

			CreateCommand createCommand = new CreateCommand(
					newControl, getCastedModel());

			Rectangle fRect = new Rectangle(getDropLocation().x
					- getCastedModel().getLocation().x, getDropLocation().y
					- getCastedModel().getLocation().y, 100, 100);

			createCommand.setLayout(fRect);
			createCommand.execute();

		} else {
			if (getCastedModel()
					.hasAttribute(AttributeConstants.ATTRIBUTE_TEXT)) {
				getCastedModel().setAttributeValue(attributeName,
						attributeValue + " = %%VALUE%%");
				getCastedModel().addObserver(observer);
			}
		}

	}

	public void undo() {
		super.undo();
		getCastedModel().removeChild(newControl);
	}

}
