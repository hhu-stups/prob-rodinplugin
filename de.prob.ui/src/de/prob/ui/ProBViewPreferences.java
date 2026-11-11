/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences of ProB for Rodin's views (Window › Preferences › ProB ›
 * Views), stored by the plug-in, not by ProB.
 */
public class ProBViewPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ProBViewPreferences() {
		super(GRID);
		setPreferenceStore(ProbUiPlugin.getDefault().getPreferenceStore());
		setDescription("Settings of ProB for Rodin's views.");
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(DetachedViews.PREFERENCE,
				"Open the ProB Console, Graph and Table views in a separate window",
				getFieldEditorParent()));
	}

	@Override
	public void init(final IWorkbench workbench) {
	}
}
