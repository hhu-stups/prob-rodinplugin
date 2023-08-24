package de.prob.ui.eventb;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;

import de.prob.core.Animator;
import de.prob.core.LimitedLogger;
import de.prob.core.command.CommandException;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.logging.Logger;
import de.prob.prolog.term.PrologTerm;

public class StartDistributedModelcheckHandler extends AbstractHandler implements IHandler {

	public static class ModificationListener implements IResourceChangeListener {

		private final IPath path;

		public ModificationListener(final IFile resource) {
			if (resource == null) {
				path = null;
			} else {
				this.path = resource.getProject().getFullPath();
			}
		}

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

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		// Get the Selection
		final IEventBRoot rootElement = getRootElement();
		final IFile resource = extractResource(rootElement);

		if (!checkErrorMarkers(resource)) {
			Logger.notifyUser("A model/context in your project contains Errors or Warnings. This can lead to unexpected behavior (e.g. missing variables) when animating.");
		}
		;

		if (resource != null) {
			LimitedLogger.getLogger().log("user started distributed modelcheck",
					rootElement.getElementName(), null);
			try {
                PrologTerm output = LoadEventBModelCommand.toPrologTerm(rootElement);
                Socket s = null;
                try {
                    s = new Socket("localhost", 4444);
                    new ObjectOutputStream(s.getOutputStream()).writeObject(output);
                } catch (IOException e) {
                    Logger.notifyUser("unable to connect to master", e);
                } finally {
                    if (s != null) {
                        try {
                            s.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            } catch (CommandException e) {
                throw new ExecutionException("unable to translate model", e);
            }
		}
		return null;
	}

	private boolean checkErrorMarkers(final IFile resource) {
		IProject project = resource.getProject();
		try {
			IMarker[] markers = project.findMarkers(
					"org.eclipse.core.resources.problemmarker", true,
					IResource.DEPTH_INFINITE);
			return markers.length == 0;
		} catch (CoreException e1) {

		}
		return false;
	}

	private IEventBRoot getRootElement() {
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				if (element instanceof IEventBRoot) {
					root = (IEventBRoot) element;
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

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		fSelection = selection;
	}

}
