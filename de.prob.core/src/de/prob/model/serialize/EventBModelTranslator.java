package de.prob.model.serialize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import com.thoughtworks.xstream.XStream;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Event.EventType;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.eventb.EventParameter;
import de.prob.model.eventb.Variant;
import de.prob.model.eventb.Witness;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BSet;

public class EventBModelTranslator {
	//
	// Map<String, ISCMachineRoot> machines = new HashMap<String,
	// ISCMachineRoot>();
	// Map<String, ISCContextRoot> contexts = new HashMap<String,
	// ISCContextRoot>();

	Map<String, EventBMachine> machines = new HashMap<String, EventBMachine>();
	Map<String, Context> contexts = new HashMap<String, Context>();
	File modelFile;

	AbstractElement mainComponent;
	private final IEventBProject eventBProject;

	public EventBModelTranslator(final IEventBRoot root) {
		modelFile = root.getUnderlyingResource().getRawLocation().toFile();
		eventBProject = root.getEventBProject();
		IInternalElementType<? extends IInternalElement> elementType = root
				.getElementType();
		String id = elementType.getId();
		if (id.equals("org.eventb.core.machineFile")) {
			ISCMachineRoot scMachineRoot = eventBProject.getSCMachineRoot(root
					.getElementName());
			mainComponent = translateMachine(scMachineRoot);
		}
		if (id.equals("org.eventb.core.contextFile")) {
			ISCContextRoot scContextRoot = eventBProject.getSCContextRoot(root
					.getElementName());
			mainComponent = translateContext(scContextRoot);
		}
	}

	private Context translateContext(final ISCContextRoot root) {
		String name = root.getComponentName();
		if (contexts.containsKey(name)) {
			return contexts.get(name);
		}

		Context c = new Context(name);
		try {
			List<Context> exts = new ArrayList<Context>();
			for (ISCExtendsContext iscExtendsContext : root
					.getSCExtendsClauses()) {
				String componentName = iscExtendsContext.getAbstractSCContext()
						.getRodinFile().getBareName();
				exts.add(translateContext(eventBProject
						.getSCContextRoot(componentName)));
			}
			c.addExtends(exts);

			List<BSet> sets = new ArrayList<BSet>();
			for (ISCCarrierSet iscCarrierSet : root.getSCCarrierSets()) {
				sets.add(new BSet(iscCarrierSet.getIdentifierString()));
			}
			c.addSets(sets);

			List<EventBAxiom> axioms = new ArrayList<EventBAxiom>();
			for (ISCAxiom iscAxiom : root.getSCAxioms()) {
				String elementName = iscAxiom.getRodinFile().getBareName();
				String predicateString = iscAxiom.getPredicateString();
				boolean theorem = iscAxiom.isTheorem();
				axioms.add(new EventBAxiom(elementName, predicateString,
						theorem));
			}
			c.addAxioms(axioms);

			List<EventBConstant> constants = new ArrayList<EventBConstant>();
			for (ISCConstant iscConstant : root.getSCConstants()) {
				constants.add(new EventBConstant(iscConstant.getElementName()));
			}
			c.addConstants(constants);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		contexts.put(c.getName(), c);
		return c;
	}

	private EventBMachine translateMachine(final ISCMachineRoot root) {
		String name = root.getComponentName();
		if (machines.containsKey(name)) {
			return machines.get(name);
		}

		EventBMachine machine = new EventBMachine(name);

		try {
			List<EventBMachine> refines = new ArrayList<EventBMachine>();
			ISCRefinesMachine[] scRefinesClauses = root.getSCRefinesClauses();
			for (ISCRefinesMachine iscRefinesMachine : scRefinesClauses) {
				IRodinFile abstractSCMachine = iscRefinesMachine
						.getAbstractSCMachine();
				String bareName = abstractSCMachine.getBareName();
				refines.add(translateMachine(eventBProject
						.getSCMachineRoot(bareName)));
			}
			machine.addRefines(refines);

			List<Context> sees = new ArrayList<Context>();
			for (ISCInternalContext iscInternalContext : root
					.getSCSeenContexts()) {
				String componentName = iscInternalContext.getComponentName();
				sees.add(translateContext(eventBProject
						.getSCContextRoot(componentName)));
			}
			machine.addSees(sees);

			List<EventBVariable> variables = new ArrayList<EventBVariable>();
			for (ISCVariable iscVariable : root.getSCVariables()) {
				variables.add(new EventBVariable(iscVariable.getElementName()));
			}
			machine.addVariables(variables);

			List<EventBInvariant> invariants = new ArrayList<EventBInvariant>();
			for (ISCInvariant iscInvariant : root.getSCInvariants()) {
				String elementName = iscInvariant.getElementName();
				String predicateString = iscInvariant.getPredicateString();
				boolean theorem = iscInvariant.isTheorem();
				invariants.add(new EventBInvariant(elementName,
						predicateString, theorem));
			}
			machine.addInvariants(invariants);

			List<Variant> variant = new ArrayList<Variant>();
			for (ISCVariant iscVariant : root.getSCVariants()) {
				variant.add(new Variant(iscVariant.getExpressionString()));
			}
			machine.addVariant(variant);

			List<Event> events = new ArrayList<Event>();
			ISCEvent[] scEvents = root.getSCEvents();
			for (ISCEvent iscEvent : scEvents) {
				events.add(extractEvent(iscEvent));
			}
			machine.addEvents(events);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		machines.put(machine.getName(), machine);
		return machine;
	}

	private Event extractEvent(final ISCEvent iscEvent) throws RodinDBException {
		String name = iscEvent.getLabel();

		int typeId = iscEvent.getConvergence().getCode();

		Event e = new Event(name, calculateEventType(typeId));

		List<Event> refines = new ArrayList<Event>();
		for (ISCRefinesEvent iscRefinesEvent : iscEvent.getSCRefinesClauses()) {
			refines.add(extractEvent(iscRefinesEvent.getAbstractSCEvent()));
		}
		e.addRefines(refines);

		List<EventBGuard> guards = new ArrayList<EventBGuard>();
		for (ISCGuard iscGuard : iscEvent.getSCGuards()) {
			String elementName = iscGuard.getElementName();
			String predicateString = iscGuard.getPredicateString();
			boolean theorem = iscGuard.isTheorem();
			guards.add(new EventBGuard(elementName, predicateString, theorem));
		}
		e.addGuards(guards);

		List<EventBAction> actions = new ArrayList<EventBAction>();
		for (ISCAction iscAction : iscEvent.getSCActions()) {
			String elementName = iscAction.getElementName();
			String assignmentString = iscAction.getAssignmentString();
			actions.add(new EventBAction(elementName, assignmentString));
		}
		e.addActions(actions);

		List<Witness> witnesses = new ArrayList<Witness>();
		for (ISCWitness iscWitness : iscEvent.getSCWitnesses()) {
			String elementName = iscWitness.getElementName();
			String predicateString = iscWitness.getPredicateString();
			witnesses.add(new Witness(elementName, predicateString));
		}
		e.addWitness(witnesses);

		List<EventParameter> parameters = new ArrayList<EventParameter>();
		for (ISCParameter iscParameter : iscEvent.getSCParameters()) {
			parameters.add(new EventParameter(iscParameter
					.getIdentifierString()));
		}
		e.addParameters(parameters);

		return e;
	}

	private EventType calculateEventType(final int typeId) {
		Convergence valueOf = Convergence.valueOf(typeId);
		if (valueOf.equals(Convergence.ORDINARY)) {
			return EventType.ORDINARY;
		}
		if (valueOf.equals(Convergence.CONVERGENT)) {
			return EventType.CONVERGENT;
		}
		if (valueOf.equals(Convergence.ANTICIPATED)) {
			return EventType.ANTICIPATED;
		}
		return null;
	}

	public AbstractElement getMainComponent() {
		return mainComponent;
	}

	public Collection<EventBMachine> getMachines() {
		return machines.values();
	}

	public Collection<Context> getContexts() {
		return contexts.values();
	}

	public File getModelFile() {
		return modelFile;
	}
	
	public String serialized() {
		XStream xstream = new XStream();
		String xml = xstream.toXML(new ModelObject(getMachines(), getContexts(), modelFile, mainComponent));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		byte[] bytes;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(xml.getBytes());
			gzip.close();
			bytes = out.toByteArray();
		} catch (IOException e) {
			bytes = xml.getBytes();
		}
		return Base64.encodeBase64String(bytes);
	}

}
