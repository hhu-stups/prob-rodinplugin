package de.prob.model.serialize;

import java.io.File;
import java.util.Collection;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.representation.AbstractElement;

public class ModelObject {
	final Collection<EventBMachine> machines;
	final Collection<Context> contexts;
	final File modelFile;
	final AbstractElement mainComponent;
	
	public ModelObject(Collection<EventBMachine> machines, Collection<Context> contexts, File modelFile, AbstractElement mainComponent) {
		this.machines = machines;
		this.contexts = contexts;
		this.modelFile = modelFile;
		this.mainComponent = mainComponent;
	}

	public Collection<EventBMachine> getMachines() {
		return machines;
	}

	public Collection<Context> getContexts() {
		return contexts;
	}

	public File getModelFile() {
		return modelFile;
	}

	public AbstractElement getMainComponent() {
		return mainComponent;
	}
}
