/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;

public class BControlTreeEditPart extends AppAbstractTreeEditPart {

	@Override
	protected List<BControl> getModelChildren() {
		Set<BControl> toShowElements = new HashSet<BControl>();
		for (BControl control : ((BControl) getModel()).getChildrenArray()) {
			if (control.showInOutlineView())
				toShowElements.add(control);
			List<BConnection> sourceConnections = control
					.getSourceConnections();
			for (BConnection con : sourceConnections) {
				if (con.showInOutlineView())
					toShowElements.add(con);
			}
			List<BConnection> targetConnections = control
					.getTargetConnections();
			for (BConnection con : targetConnections) {
				if (con.showInOutlineView())
					toShowElements.add(con);
			}
		}
		return new ArrayList<BControl>(toShowElements);
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BControl.PROPERTY_ADD)
				|| evt.getPropertyName().equals(BControl.PROPERTY_REMOVE)) {
			refreshChildren();
		}
		if (evt.getPropertyName().equals(AttributeConstants.ATTRIBUTE_ID))
			refreshVisuals();
	}

	@Override
	public void refreshVisuals() {
		BControl bcontrol = (BControl) getModel();
		if (!(bcontrol instanceof Visualization)) {
			setWidgetText(bcontrol.getAttributeValue(
					AttributeConstants.ATTRIBUTE_ID).toString());
			setWidgetImage(BMotionStudioImage.getBControlImage(bcontrol
					.getType()));
		}
	}

}
