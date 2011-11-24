/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public final class DialogHelpers {

	private DialogHelpers() {
	}

	public static GridLayout createSimpleGroupLayout() {
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 1;
		return groupLayout;
	}

	public static Group createGroup(final Composite c, final String title) {
		Group group = new Group(c, SWT.NONE);
		group.setLayout(DialogHelpers.createSimpleGroupLayout());
		group.setText(title);
		return group;
	}

}
