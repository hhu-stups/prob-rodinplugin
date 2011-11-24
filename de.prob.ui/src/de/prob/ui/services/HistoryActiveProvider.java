package de.prob.ui.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;

public class HistoryActiveProvider extends AbstractSourceProvider {

	public static final String ENABLED = "enabled";
	public static final String DISABLED = "disabled";
	public static final String FORWARD_SERVICE = "de.prob.core.history.forward_service";
	public static final String BACKWARD_SERVICE = "de.prob.core.history.backward_service";

	private boolean forward = false;
	private boolean backward = false;

	public Map<String, String> getCurrentState() {
		Map<String, String> state = new HashMap<String, String>(2);
		addToState(state, backward, BACKWARD_SERVICE);
		addToState(state, forward, FORWARD_SERVICE);
		return state;
	}

	private void addToState(final Map<String, String> state,
			final boolean flag, final String service) {
		String f = flag ? ENABLED : DISABLED;
		state.put(service, f);
	}

	public String[] getProvidedSourceNames() {
		return new String[] { FORWARD_SERVICE, BACKWARD_SERVICE };
	}

	public boolean isBackwardEnabled() {
		return backward;
	}

	public boolean isForwardEnabled() {
		return forward;
	}

	public void historyChange() {
		History history = Animator.getAnimator().getHistory();
		int current = history.getCurrentPosition();
		boolean notEmpty = !history.isEmpty();
		boolean notFirst = current != 0;
		boolean notLast = current != history.size() - 1;

		backward = notEmpty && notFirst;
		forward = notEmpty && notLast;
		fireSourceChanged(ISources.WORKBENCH, getCurrentState());
	}

	public void dispose() {

	}
}
