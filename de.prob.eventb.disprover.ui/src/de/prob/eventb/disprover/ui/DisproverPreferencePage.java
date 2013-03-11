package de.prob.eventb.disprover.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.prob.eventb.disprover.core.DisproverReasonerInput.HypothesesSource;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class DisproverPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public DisproverPreferencePage() {
		super(GRID);
		setPreferenceStore(DisproverActivator.getDefault().getPreferenceStore());
		setTitle("Disprover (only applies in detailed config Mode)");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new ComboFieldEditor(PreferenceConstants.P_HYPOTHESES,
				"Hypotheses to be included", new String[][] {
						{ "All", HypothesesSource.ALL.toString() },
						{ "Relevant", HypothesesSource.RELEVANT.toString() },
						{ "Selected", HypothesesSource.SELECTED.toString() } },
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_INCLUDE_CONTEXT,
				"Include the referenced Contexts", getFieldEditorParent()));

		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(
				PreferenceConstants.P_MIN_INT, "MinInt", getFieldEditorParent());
		integerFieldEditor.setValidRange(Integer.MIN_VALUE, 0);
		addField(integerFieldEditor);
		addField(new IntegerFieldEditor(PreferenceConstants.P_MAX_INT,
				"MaxInt", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.P_SET_SIZE,
				"Size of unspecified deferred sets", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.P_TIMEOUT,
				"Time out (ms)", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}