package de.prob.model.eventb;

import java.util.List;

import de.prob.model.representation.BEvent;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;

public class EventBMachine extends Machine {
	public EventBMachine(final String name) {
		super(name);
	}

	public void addRefines(final List<EventBMachine> refines) {
		put(Machine.class, refines);
	}

	public void addSees(final List<Context> sees) {
		put(Context.class, sees);
	}

	public void addVariables(final List<EventBVariable> variables) {
		put(Variable.class, variables);
	}

	public void addInvariants(final List<EventBInvariant> invariants) {
		put(Invariant.class, invariants);
	}

	public void addVariant(final List<Variant> variant) {
		put(Variant.class, variant);
	}

	public void addEvents(final List<Event> events) {
		put(BEvent.class, events);
	}
}
