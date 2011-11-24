/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.attribute.BAttributeMisc;
import de.bmotionstudio.gef.editor.model.BControl;

public class BControlPropertySource extends AbstractAttribute {

	private BControl control;

	private BAttributeMisc miscAttribute;

	public BControlPropertySource(BControl control) {
		super(null);
		this.control = control;
		this.miscAttribute = new BAttributeMisc(null);
		addChild(this.miscAttribute);
		init();
	}

	private void init() {

		for (AbstractAttribute atr : control.getAttributes().values()) {

			atr.setControl(control);

			String group = atr.getGroup();

			if (group != null) {

				// If group is root node --> add to root
				if (group.equals(ROOT)) {
					addChild(atr);
				} else {
					AbstractAttribute groupAtr = control.getAttribute(group);
					if (groupAtr != null) {
						groupAtr.addChild(atr);
					} else {
						miscAttribute.addChild(atr);
					}
				}

			} else {
				// No group, add to misc attribute node
				miscAttribute.addChild(atr);
			}

		}

	}

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return "RootProperties";
	}

}
