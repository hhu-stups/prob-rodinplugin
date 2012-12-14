package de.bmotionstudio.gef.editor.model;

import java.util.HashMap;
import java.util.Map;

public class Simulation {

	private Map<String, VisualizationView> views;

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

}
