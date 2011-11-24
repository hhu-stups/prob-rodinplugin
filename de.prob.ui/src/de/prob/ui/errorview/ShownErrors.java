package de.prob.ui.errorview;

import java.util.*;
import java.util.Map.Entry;

import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.StateError;

public class ShownErrors {
	private MachineDescription description;
	private State state;
	private Collection<ErrorEvent> errorEvents;

	@SuppressWarnings("unchecked")
	public void update(MachineDescription description, State state) {
		Collection<StateError> errors = state == null ? Collections.EMPTY_LIST
				: state.getStateBasedErrors();
		Map<String, Collection<StateError>> emap = new HashMap<String, Collection<StateError>>();
		for (StateError error : errors) {
			final String event = error.getEvent();
			Collection<StateError> evErrors = emap.get(event);
			if (evErrors == null) {
				evErrors = new ArrayList<StateError>();
				emap.put(event, evErrors);
			}
			evErrors.add(error);
		}
		Collection<ErrorEvent> errorEvents = new ArrayList<ErrorEvent>();
		for (String event : description.getEventNames()) {
			Collection<StateError> eventErrors = emap.get(event);
			if (eventErrors != null && !eventErrors.isEmpty()) {
				ErrorEvent ee = new ErrorEvent(event, this, eventErrors);
				errorEvents.add(ee);
			}
			emap.remove(event);
		}
		
		for (Entry<String, Collection<StateError>> entry : emap.entrySet()) {
			Collection<StateError> eventErrors = entry.getValue();
			if (eventErrors != null && !eventErrors.isEmpty()) {
				ErrorEvent ee = new ErrorEvent(entry.getKey(), this, eventErrors);
				errorEvents.add(ee);
			}
		}
		
		
		this.errorEvents = errorEvents;
		this.description = description;
		this.state = state;
	}

	public MachineDescription getDescription() {
		return description;
	}

	public State getState() {
		return state;
	}

	@SuppressWarnings("unchecked")
	public Collection<ErrorEvent> getErrorEvents() {
		return errorEvents == null ? Collections.EMPTY_LIST : errorEvents;
	}

	public static class ErrorEvent {
		public static final ErrorEvent[] EMPTY_ARRAY = new ErrorEvent[0];

		private final String event;
		private final ShownErrors shown;
		private final Collection<ShownStateError> errors;

		public ErrorEvent(String event, ShownErrors shown,
				Collection<StateError> errors) {
			this.event = event;
			this.shown = shown;
			Collection<ShownStateError> es = new ArrayList<ShownStateError>();
			for (StateError se : errors) {
				es.add(new ShownStateError(this, se));
			}
			this.errors = Collections.unmodifiableCollection(es);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (o == this)
				return true;
			if (o instanceof ErrorEvent) {
				ErrorEvent other = (ErrorEvent) o;
				return this.event.equals(other.event)
						&& this.shown.equals(other.shown);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return event.hashCode();
		}

		public String getEvent() {
			return event;
		}

		public Collection<ShownStateError> getErrors() {
			return errors;
		}

		public ShownErrors getShownErrors() {
			return shown;
		}
	}

	public static class ShownStateError {
		public static final ShownStateError[] EMPTY_ARRAY = new ShownStateError[0];
		private final ErrorEvent event;
		private final StateError error;

		public ShownStateError(ErrorEvent event, StateError error) {
			this.event = event;
			this.error = error;
		}

		public ErrorEvent getEvent() {
			return event;
		}

		public StateError getError() {
			return error;
		}

	}
}
