/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.ArrayList;
import java.util.Collection;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BRadioButton;
import de.bmotionstudio.gef.editor.model.Visualization;

/**
 * Finds the radio buttons of a button group. The group is computed from the
 * visualization each time: a global registry was shared by all open
 * visualizations, cleared when another one was opened and never updated when
 * a button was deleted or moved to another group.
 */
public final class ButtonGroupHelper {

	private ButtonGroupHelper() {
	}

	/**
	 * @return the radio buttons of the visualization that belong to the given
	 *         group, never <code>null</code>
	 */
	public static Collection<BControl> getButtonGroup(
			final Visualization visualization, final String buttonGroupID) {
		final Collection<BControl> group = new ArrayList<BControl>();
		if (visualization != null && buttonGroupID != null
				&& !buttonGroupID.trim().isEmpty()) {
			collect(visualization, buttonGroupID, group);
		}
		return group;
	}

	private static void collect(final BControl parent,
			final String buttonGroupID, final Collection<BControl> group) {
		for (BControl control : parent.getChildrenArray()) {
			if (control instanceof BRadioButton) {
				final Object id = control
						.getAttributeValue(AttributeConstants.ATTRIBUTE_BUTTONGROUP);
				if (id != null && buttonGroupID.equals(id.toString()))
					group.add(control);
			}
			collect(control, buttonGroupID, group);
		}
	}

}
