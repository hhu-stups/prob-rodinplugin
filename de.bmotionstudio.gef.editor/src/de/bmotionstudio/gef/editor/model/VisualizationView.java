package de.bmotionstudio.gef.editor.model;

public class VisualizationView {

	private String name;
	private Visualization visualization;
	private String viewId;
	
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
		this.name = name;
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

}
