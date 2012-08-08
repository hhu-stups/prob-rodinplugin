package de.prob.ui.eventb;

import groovy.lang.Binding;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.emf.core.Project;
import org.eventb.emf.persistence.ProjectResource;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import com.google.inject.Injector;

import de.prob.animator.command.LoadEventBCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.core.Animator;
import de.prob.core.internal.Activator;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.EventBFactory;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ServletContextListener;

public class StartAnimationHandler extends AbstractHandler implements IHandler {

	public static class ModificationListener implements IResourceChangeListener {

		private final IPath path;

		public ModificationListener(final IFile resource) {
			if (resource == null) {
				path = null;
			} else {
				this.path = resource.getProject().getFullPath();
			}
		}

		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			if (path != null) {
				final IResourceDelta delta = event.getDelta();
				IResourceDelta member = delta.findMember(path);
				if (member != null) {
					Animator.getAnimator().setDirty();
				}
			}
		}
	}

	private ISelection fSelection;
	private ModificationListener listener;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		final IEventBRoot rootElement = getRootElement();

		Injector injector = ServletContextListener.INJECTOR;

		final EventBFactory instance = injector
				.getInstance(EventBFactory.class);

		IRodinProject rodinProject = rootElement.getRodinProject();
		ProjectResource resource = new ProjectResource(rodinProject);
		try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Project project = (Project) resource.getContents().get(0);
		String serialized = serialize(project);
		EventBModel model = null;
		try {
			model = instance.load(serialized, rootElement.getComponentName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringWriter writer = new StringWriter();
		PrintWriter pto = new PrintWriter(writer);
		try {
			TranslatorFactory.translate(rootElement, pto);
		} catch (TranslationFailedException e) {
			e.printStackTrace();
		}

		StateSpace s = model.getStatespace();

		Pattern p2 = Pattern.compile("^package\\((.*?)\\)\\.");
		Matcher m2 = p2.matcher(writer.toString());
		m2.find();
		String cmd = m2.group(1);

		s.execute(new LoadEventBCommand(cmd));
		s.execute(new StartAnimationCommand());

		History h = new History(s);
		Activator.setHistory(h);
		final GroovyExecution ge = injector.getInstance(GroovyExecution.class);
		Binding bindings = ge.getBindings();
		bindings.setVariable("defaultStateSpace", s);
		bindings.setVariable("defaultHistory", h);
		System.gc();
		return null;
	}

	private static String serialize(Project project) {

		StringWriter sw = new StringWriter();
		Map<Object, Object> options = new HashMap<Object, Object>();
		options.put(XMLResource.OPTION_ROOT_OBJECTS,
				Collections.singletonList(project));
		options.put(XMLResource.OPTION_FORMATTED, false);
		XMLResourceImpl ri = new XMLResourceImpl();
		try {
			ri.save(sw, options);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String xml = Base64.encodeBase64String(sw.toString().getBytes());
		return xml;
	}

	private boolean checkErrorMarkers(final IFile resource, List<String> errors) {
		boolean result = false;
		IProject project = resource.getProject();
		try {
			IMarker[] markers = project.findMarkers(
					"org.eclipse.core.resources.problemmarker", true,
					IResource.DEPTH_INFINITE);
			for (IMarker iMarker : markers) {
				errors.add(iMarker.getResource().getName()
						+ ": "
						+ iMarker
								.getAttribute(IMarker.MESSAGE, "unknown Error"));
				result = result
						|| (Integer) iMarker.getAttribute(IMarker.SEVERITY) == IMarker.SEVERITY_ERROR;
			}

		} catch (CoreException e1) {
		}
		return result;
	}

	private IEventBRoot getRootElement() {
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				if (element instanceof IEventBRoot) {
					root = (IEventBRoot) element;
				} else if (element instanceof IFile) {
					IRodinFile rodinFile = RodinCore.valueOf((IFile) element);
					if (rodinFile != null) {
						root = (IEventBRoot) rodinFile.getRoot();
					}
				}
			}
		}
		return root;
	}

	private IFile extractResource(final IEventBRoot rootElement) {
		IFile resource = null;
		if (rootElement == null) {
			resource = null;
		} else if (rootElement instanceof IMachineRoot) {
			resource = ((IMachineRoot) rootElement).getSCMachineRoot()
					.getResource();
		} else if (rootElement instanceof IContextRoot) {
			resource = ((IContextRoot) rootElement).getSCContextRoot()
					.getResource();
		}
		return resource;
	}

	private void registerModificationListener(final IFile resource) {
		if (listener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(
					listener);
		}
		listener = new ModificationListener(resource);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
	}

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		fSelection = selection;
	}

}
