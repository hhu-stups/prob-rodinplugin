package de.bmotionstudio.gef.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class VisualizationView {

	private String name;

	private Visualization visualization;

	private String viewId;
	
	private transient PropertyChangeSupport listeners;

	public VisualizationView(String name, Visualization visualization,
			String viewId) {
		this.name = name;
		this.visualization = visualization;
		this.viewId = viewId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldVal = this.name;
		this.name = name;
		listeners.firePropertyChange("name", oldVal, name);
	}

	public Visualization getVisualization() {
		return visualization;
	}

	public void setVisualization(Visualization visualization) {
		this.visualization = visualization;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
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
