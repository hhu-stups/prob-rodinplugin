package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

import de.prob.core.Animator;
import de.prob.core.LimitedLogger;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.PerspectiveFactory;

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
			final IResourceDelta delta = event.getDelta();
			// PRE_CLOSE and PRE_DELETE events have no delta; closing or
			// deleting the project is reported by the following POST_CHANGE
			if (path != null && delta != null) {
				IResourceDelta member = delta.findMember(path);
				if (member != null && changesModel(member)) {
					Animator.getAnimator().setDirty();
				}
			}
		}

		/**
		 * whether the project's change is not only inside its folders: the
		 * machines and contexts are files directly in the project, the
		 * folders hold e.g. saved traces
		 */
		private static boolean changesModel(final IResourceDelta project) {
			final IResourceDelta[] children = project.getAffectedChildren();
			if (children.length == 0)
				return true; // e.g. the project itself was closed or deleted
			for (IResourceDelta child : children)
				// also when a folder is created, e.g. for the first trace
				if (child.getResource().getType() != IResource.FOLDER)
					return true;
			return false;
		}
	}

	private ISelection fSelection;
	// shared by the handlers of the explorer and of the editors (see
	// StartEditorAnimationHandler): only the animated model is watched
	private static ModificationListener listener;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		final IEventBRoot rootElement = findRootElement(event);
		final IFile resource = extractResource(rootElement);
		if (resource == null) {
			// e.g. several elements or no Event-B component selected
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"ProB", "Please select a single machine or context to animate.");
			return null;
		}

		try {
			startAnimation(rootElement);
		} catch (ProBException e) {
			e.notifyUserOnce();
			throw new ExecutionException("Loading the machine failed", e);
		}
		return null;
	}

	/**
	 * Starts the animation of a machine or context: loads it into ProB and
	 * opens the ProB perspective. Errors and warnings of the project are
	 * logged.
	 */
	public static void startAnimation(final IEventBRoot rootElement)
			throws ProBException {
		final IFile resource = extractResource(rootElement);
		ArrayList<String> errors = new ArrayList<String>();
		boolean realError = checkErrorMarkers(resource, errors);
		if (!errors.isEmpty()) {
		    Animator.getAnimator().setRodinProjectHasErrorsOrWarnings();
			String message = "Some components in your project contain "
					+ (realError ? "errors" : "warnings")
					+ "!\n This can lead to unexpected behavior (e.g. missing variables) when animating with ProB.\nDetails:\n";
			StringBuffer stringBuffer = new StringBuffer(message);
			for (String string : errors) {
				stringBuffer.append(string);
				stringBuffer.append('\n');
			}
			if (true) {
			   // TO DO: use preference whether to pop-up such errors or simply log them
			   // Unfortunately the GetPreferencesCommand gets ProBPreference object which do not contain the current value
			   // we could also change the label in StateViewPart.java, e.g. modelchangeProvider or add a new label
			   Logger.info(stringBuffer.toString());
			   LimitedLogger.getLogger().log(stringBuffer.toString(),rootElement.getElementName(), null);
			} else if (realError)
				Logger.notifyUser(stringBuffer.toString());
			else
				Logger.log(IStatus.WARNING, stringBuffer.toString(), null);
		} else {
		     Animator.getAnimator().resetRodinProjectHasErrorsOrWarnings();
		}

		LimitedLogger.getLogger().log("user started animation",
				rootElement.getElementName(), null);
		registerModificationListener(resource);
		PerspectiveFactory.openPerspective();

		LoadEventBModelCommand.load(Animator.getAnimator(), rootElement);
	}

	private static boolean checkErrorMarkers(final IFile resource, List<String> errors) {
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
						|| iMarker.getAttribute(IMarker.SEVERITY,
								IMarker.SEVERITY_INFO) == IMarker.SEVERITY_ERROR;
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
					if (rodinFile != null)
						root = (IEventBRoot) rodinFile.getRoot();
				}
			}
		}
		return root;
	}

	/**
	 * @return the machine or context to animate, null if there is none
	 */
	protected IEventBRoot findRootElement(final ExecutionEvent event) {
		return getRootElement();
	}

	/**
	 * @return the machine or context of the active editor, null if there is
	 *         none
	 */
	protected static IEventBRoot getEditorRootElement(
			final ExecutionEvent event) {
		final IEditorInput input = HandlerUtil.getActiveEditorInput(event);
		final IFile file = input == null ? null : Adapters.adapt(input,
				IFile.class);
		if (file == null)
			return null;
		final IRodinFile rodinFile = RodinCore.valueOf(file);
		if (rodinFile == null || !(rodinFile.getRoot() instanceof IEventBRoot))
			return null;
		return (IEventBRoot) rodinFile.getRoot();
	}

	private static IFile extractResource(final IEventBRoot rootElement) {
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

	private static void registerModificationListener(final IFile resource) {
		if (listener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(
					listener);
		}
		listener = new ModificationListener(resource);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
				IResourceChangeEvent.POST_CHANGE);
	}

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		fSelection = selection;
	}

}
