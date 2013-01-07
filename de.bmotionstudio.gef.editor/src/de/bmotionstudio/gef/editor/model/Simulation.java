package de.bmotionstudio.gef.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import de.bmotionstudio.gef.editor.Animation;
import de.prob.core.Animator;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;

public class Simulation {

	private transient PropertyChangeSupport listeners;
	
	private Map<String, VisualizationView> views;

	private transient IFile projectFile;

	private transient boolean dirty;

	private transient Animation animation;

	private transient boolean running;

	private String model;

	public Simulation(String model) {
		this.views = new HashMap<String, VisualizationView>();
		this.model = model;
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

	public boolean isRunning() {
		return running;
	}

	public void setDirty(boolean dirty) {
		boolean oldVal = this.dirty;
		this.dirty = dirty;		
		listeners.firePropertyChange("dirty", oldVal, dirty);
	}

	public void setRunning(boolean running) {
		boolean oldVal = this.running;
		this.running = running;
		listeners.firePropertyChange("running", oldVal, running);
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

	public void start() {

		Animator animator = Animator.getAnimator();
		animation = new Animation(animator, this);
		IEventBRoot modelRoot = getCorrespondingFile(getProjectFile(), model);
		try {
			LoadEventBModelCommand.load(animator, modelRoot);
			setRunning(true);
		} catch (ProBException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		if (animation != null)
			animation.unregister();
		setRunning(false);
	}

	private IEventBRoot getCorrespondingFile(IFile file, String machineFileName) {
		IRodinProject rProject = RodinCore.valueOf(file.getProject());
		IRodinFile rFile = rProject.getRodinFile(machineFileName);
		IEventBRoot eventbRoot = (IEventBRoot) rFile.getRoot();
		return eventbRoot;
	}

}
