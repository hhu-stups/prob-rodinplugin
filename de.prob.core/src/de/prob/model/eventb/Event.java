package de.prob.model.eventb;

import java.util.List;
import java.util.Set;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Action;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Guard;

public class Event extends BEvent {

	private final EventType type;

	public enum EventType {
		ORDINARY, CONVERGENT, ANTICIPATED
	}

	public Event(final String name, final EventType type) {
		super(name);
		this.type = type;
	}

	public void addRefines(final List<Event> refines) {
		put(Event.class, refines);
	}

	public void addGuards(final List<EventBGuard> guards) {
		put(Guard.class, guards);
	}

	public void addActions(final List<EventBAction> actions) {
		put(Action.class, actions);
	}

	public void addWitness(final List<Witness> witness) {
		put(Witness.class, witness);
	}

	public void addParameters(final List<EventParameter> parameters) {
		put(EventParameter.class, parameters);
	}

	public EventType getType() {
		return type;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public String print() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + getName() + "\n");
		sb.append("Type: " + type.toString() + "\n");
		addChildren("Refines", getChildrenOfType(Event.class), sb);
		addChildren("Any", getChildrenOfType(EventParameter.class), sb);
		addChildren("Where", getChildrenOfType(Guard.class), sb);
		addChildren("With", getChildrenOfType(Witness.class), sb);
		addChildren("Then", getChildrenOfType(Action.class), sb);
		return sb.toString();
	}

	private void addChildren(final String name,
			final Set<? extends AbstractElement> childrenOfType,
			final StringBuilder sb) {
		if (!childrenOfType.isEmpty()) {
			sb.append(name + ": \n");
			for (AbstractElement abstractElement : childrenOfType) {
				sb.append("\t" + abstractElement.toString() + "\n");
			}
		}
	}
}
