package de.bmotionstudio.gef.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

public class Simulation {

	private transient PropertyChangeSupport listeners;
	
	private Map<String, VisualizationView> views;

	private transient IFile projectFile;

	private transient boolean dirty;

	public Simulation() {
		this.views = new HashMap<String, VisualizationView>();
	}

	public Map<String, VisualizationView> getVisualizationViews() {
		return views;
	}

	public void setVisualizationViews(
			Map<String, VisualizationView> visualizationViews) {
		this.views = visualizationViews;
	}

	public IFile getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(IFile projectFile) {
		this.projectFile = projectFile;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		boolean oldVal = this.dirty;
		this.dirty = dirty;		
		listeners.firePropertyChange("dirty", oldVal, dirty);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getListeners().addPropertyChangeListener(listener);
	}

	public PropertyChangeSupport getListeners() {
		if (listeners == null)
			listeners = new PropertyChangeSupport(this);
		return listeners;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getListeners().removePropertyChangeListener(listener);
	}

}
