package de.prob.eventb.disprover.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DisproverActivator.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.P_INCLUDE_CONTEXT, true);
		store.setDefault(PreferenceConstants.P_HYPOTHESES, "all");
		store.setDefault(PreferenceConstants.P_MAX_INT, 1);
		store.setDefault(PreferenceConstants.P_MIN_INT, -1);
		store.setDefault(PreferenceConstants.P_TIMEOUT, 1000);
		store.setDefault(PreferenceConstants.P_SET_SIZE, 1);
	}

}
