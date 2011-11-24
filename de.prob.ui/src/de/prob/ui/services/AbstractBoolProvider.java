package de.prob.ui.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public abstract class AbstractBoolProvider extends AbstractSourceProvider {

	private final String service;

	public static final String ENABLED = "enabled";
	public static final String DISABLED = "disabled";
	private boolean enabled = false;

	public AbstractBoolProvider(final String service) {
		this.service = service;
	}

	public void dispose() {

	}

	public Map<String, String> getCurrentState() {
		Map<String, String> currentState = new HashMap<String, String>(1);
		String current = enabled ? ENABLED : DISABLED;
		currentState.put(service, current);
		return currentState;
	}

	public String[] getProvidedSourceNames() {
		return new String[] { service };
	}

	public void setEnabled(final boolean enabled) {
		if (nochange(enabled))
			return;
		this.enabled = enabled;
		fireSourceChanged(ISources.WORKBENCH, getCurrentState());
	}

	public boolean isEnabled() {
		return enabled;
	}

	private boolean nochange(final boolean enabled) {
		return this.enabled == enabled;
	}

}
