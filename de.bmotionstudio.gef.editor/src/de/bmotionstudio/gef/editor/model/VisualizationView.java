package de.bmotionstudio.gef.editor.model;

public class VisualizationView {

	private String name;
	private Visualization visualization;

	public VisualizationView(String name, Visualization visualization) {
		this.name = name;
		this.visualization = visualization;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Visualization getVisualization() {
		return visualization;
	}

	public void setVisualization(Visualization visualization) {
		this.visualization = visualization;
	}

}
