package de.prob.ui.eventb;

import static de.prob.model.representation.RefType.ERefType.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EList;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.Project;
import org.eventb.emf.core.context.Axiom;
import org.eventb.emf.core.context.CarrierSet;
import org.eventb.emf.core.context.Constant;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Action;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Guard;
import org.eventb.emf.core.machine.Invariant;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.Parameter;
import org.eventb.emf.core.machine.Variable;
import org.eventb.emf.core.machine.Variant;
import org.eventb.emf.core.machine.Witness;

import de.prob.model.eventb.EBContext;
import de.prob.model.eventb.EBEvent;
import de.prob.model.eventb.EBMachine;
import de.prob.model.eventb.Model;
import de.prob.model.representation.Label;

public class NewCoreModelTranslation {

	private HashMap<String, Label> components;

	public Model translate(final Project p, final String mainComponent) {
		this.components = new HashMap<String, Label>();

		Model model = new Model(mainComponent);

		final Map<String, EventBNamedCommentedComponentElement> allComponents = new HashMap<String, EventBNamedCommentedComponentElement>();

		for (final EventBNamedCommentedComponentElement cmpt : p
				.getComponents()) {
			final String name = cmpt.doGetName();
			allComponents.put(name, cmpt);
			if (cmpt instanceof Context) {
				components.put(name, createContext((Context) cmpt));

			} else if (cmpt instanceof Machine) {
				components.put(name, createMachine((Machine) cmpt));
			}
		}

		for (Entry<String, EventBNamedCommentedComponentElement> entry : allComponents
				.entrySet()) {

			EventBNamedCommentedComponentElement element = entry.getValue();

			final String name = element.doGetName();
			final Label from = components.get(name);
			Label to = null;

			if (element instanceof Context) {
				for (final Context context : ((Context) element).getExtends()) {
					final String ctxName = context.doGetName();
					to = components.get(ctxName);
					model.addRelationship(from, to, EXTENDS);
				}
			}

			if (element instanceof Machine) {
				for (final Context context : ((Machine) element).getSees()) {
					final String ctxName = context.doGetName();
					to = components.get(ctxName);
					model.addRelationship(from, to, SEES);
				}
				for (final Machine machine : ((Machine) element).getRefines()) {
					final String mName = machine.doGetName();
					to = components.get(mName);
					model.addRelationship(from, to, REFINES);
				}
			}
		}
		return model;
	}

	private Label createMachine(Machine machine) {
		EBMachine m = new EBMachine(machine.getName());
		for (Event event : machine.getEvents()) {
			m.addEvent(createEvent(event));
		}

		for (Variable variable : machine.getVariables()) {
			m.addVariable(variable.getName());
		}

		Variant variant = machine.getVariant();
		if (variant != null)
			m.addVariant(variant.getExpression());

		for (Invariant invariant : machine.getInvariants()) {
			m.addInvariant(invariant.getPredicate(), invariant.getName());
		}

		return m;
	}

	private EBEvent createEvent(Event event) {

		EBEvent e = new EBEvent(event.getName());

		for (Witness witness : event.getWitnesses()) {
			e.addWitness(witness.getPredicate(), witness.getName());
		}

		for (Guard guard : event.getGuards()) {
			e.addGuard(guard.getPredicate(), guard.getName());
		}
		for (Parameter parameter : event.getParameters()) {
			e.addParameter(parameter.getName());
		}

		for (String string : event.getRefinesNames()) {
			e.addParameter(string);
		}
		EList<Action> actions = event.getActions();
		for (Action action : actions) {
			e.addAction(action.getAction(), action.getName());
		}

		return e;
	}

	private Label createContext(Context c) {
		String name = c.getName();
		EBContext context = new EBContext(name);
		EList<Axiom> axioms = c.getAxioms();
		for (Axiom axiom : axioms) {
			context.addAxiom(axiom.getPredicate(), axiom.doGetName());
		}

		for (Constant constant : c.getConstants()) {
			context.addConstant(constant.doGetName());
		}

		for (CarrierSet carrierSet : c.getSets()) {
			context.addSet(carrierSet.doGetName());
		}

		return context;
	}

}
