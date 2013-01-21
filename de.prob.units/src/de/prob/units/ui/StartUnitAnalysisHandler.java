/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.units.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

import de.prob.core.Animator;
import de.prob.core.LimitedLogger;
import de.prob.core.command.ActivateUnitPluginCommand;
import de.prob.core.command.ClearMachineCommand;
import de.prob.core.command.CommandException;
import de.prob.core.command.ComposedCommand;
import de.prob.core.command.GetPluginResultCommand;
import de.prob.core.command.SetPreferencesCommand;
import de.prob.core.command.StartAnimationCommand;
import de.prob.core.command.internal.InternalLoadCommand;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ResultParserException;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.units.pragmas.InferredUnitPragmaAttribute;
import de.prob.units.pragmas.UnitPragmaAttribute;
import de.prob.units.problems.IncorrectUnitDefinitionMarker;
import de.prob.units.problems.MultipleUnitsInferredMarker;
import de.prob.units.problems.NoUnitInferredMarker;

public class StartUnitAnalysisHandler extends AbstractHandler implements
		IHandler {

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

		// Get the Selection
		final IEventBRoot rootElement = getRootElement();
		final IFile resource = extractResource(rootElement);

		removeUnitErrorMarkers(resource);

		ArrayList<String> errors = new ArrayList<String>();
		boolean realError = checkErrorMarkers(resource, errors);
		if (!errors.isEmpty()) {
			String message = "Some components in your project contain "
					+ (realError ? "errors" : "warnings")
					+ ". This can lead to unexpected behavior (e.g. missing variables) when animating.\n\nDetails:\n";
			StringBuffer stringBuffer = new StringBuffer(message);
			for (String string : errors) {
				stringBuffer.append(string);
				stringBuffer.append('\n');
			}
			if (realError)
				Logger.notifyUserWithoutBugreport(stringBuffer.toString());
			else
				Logger.notifyUserAboutWarningWithoutBugreport(stringBuffer
						.toString());
		}
		;

		if (resource != null) {
			LimitedLogger.getLogger().log("user started unit analysis",
					rootElement.getElementName(), null);
			registerModificationListener(resource);

			final Animator animator = Animator.getAnimator();
			try {
				// load machine and activate plugin
				final ClearMachineCommand clear = new ClearMachineCommand();
				final SetPreferencesCommand setPrefs = SetPreferencesCommand
						.createSetPreferencesCommand(animator);
				final InternalLoadCommand load = new InternalLoadCommand(
						rootElement);
				final StartAnimationCommand start = new StartAnimationCommand();
				final ActivateUnitPluginCommand activatePlugin = new ActivateUnitPluginCommand();

				GetPluginResultCommand pluginResultCommand = new GetPluginResultCommand(
						"Grounded Result State");

				final ComposedCommand composed = new ComposedCommand(clear,
						setPrefs, load, start, activatePlugin,
						pluginResultCommand);

				animator.execute(composed);
				processResults(pluginResultCommand.getResult());
			} catch (ProBException e) {
				e.notifyUserOnce();
				throw new ExecutionException("Unit Analysis Failed", e);
			} catch (RodinDBException e) {
				throw new ExecutionException(
						"Unit Analysis Failed with a RodinDBException", e);
			}
		}
		return null;
	}

	private void removeUnitErrorMarkers(final IFile resource) {
		IProject project = resource.getProject();
		try {
			IMarker[] markers = project.findMarkers(
					"org.eclipse.core.resources.problemmarker", true,
					IResource.DEPTH_INFINITE);
			for (IMarker iMarker : markers) {
				if (iMarker.getAttribute(RodinMarkerUtil.ERROR_CODE, "")
						.equals(MultipleUnitsInferredMarker.ERROR_CODE)) {
					iMarker.delete();
				}
				if (iMarker.getAttribute(RodinMarkerUtil.ERROR_CODE, "")
						.equals(IncorrectUnitDefinitionMarker.ERROR_CODE)) {
					iMarker.delete();
				}
				if (iMarker.getAttribute(RodinMarkerUtil.ERROR_CODE, "")
						.equals(NoUnitInferredMarker.ERROR_CODE)) {
					iMarker.delete();
				}
			}

		} catch (CoreException e1) {
		}
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

	private void processResults(CompoundPrologTerm result)
			throws RodinDBException, ExecutionException {
		// preprocess the list into a map
		Map<String, String> variables = new HashMap<String, String>();
		List<String> offendingDefinitions = new ArrayList<String>();

		ListPrologTerm liste = BindingGenerator.getList(result.getArgument(1));

		for (PrologTerm term : liste) {
			if (term.isAtom()) {
				// this is an error message. do something about it.
				String offendingUnitDefinition = PrologTerm.atomicString(term)
						.replace("Incorrect unit definition: ['", "");
				offendingUnitDefinition = offendingUnitDefinition.replace("']",
						"");

				// add error to the list of incorrect definitions. error markers
				// will be attached later
				offendingDefinitions.add(offendingUnitDefinition);

			} else {
				// process inferred units and add to map
				CompoundPrologTerm compoundTerm;
				try {
					compoundTerm = BindingGenerator.getCompoundTerm(term,
							"bind", 2);

					variables.put(PrologTerm.atomicString(compoundTerm
							.getArgument(1)), PrologTerm
							.atomicString(compoundTerm.getArgument(2)));
				} catch (ResultParserException e) {
					CommandException commandException = new CommandException(
							e.getLocalizedMessage(), e);
					commandException.notifyUserOnce();
				}
			}
		}

		IEventBRoot rootElement = getRootElement();
		// look up the variables / constants of the selected machine in
		// the state
		// and set the inferredUnitPragma attribute
		if (rootElement instanceof IMachineRoot) {
			// find and update variables
			IVariable[] allVariables = rootElement.getMachineRoot()
					.getVariables();
			for (IVariable var : allVariables) {
				// reset inferred unit
				var.setAttributeValue(InferredUnitPragmaAttribute.ATTRIBUTE,
						"", new NullProgressMonitor());

				String variableName = var.getIdentifierString();
				if (variables.containsKey(variableName)) {
					var.setAttributeValue(
							InferredUnitPragmaAttribute.ATTRIBUTE,
							variables.get(variableName),
							new NullProgressMonitor());

					if (variables.get(variableName).startsWith("multiple")) {
						var.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new MultipleUnitsInferredMarker(variableName));
					}
					if (variables.get(variableName).equals("unknown")) {
						var.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new NoUnitInferredMarker(variableName));
					}
				}

				// check if the attached unit pragma (given by user) was marked
				// as offending
				if (var.hasAttribute(UnitPragmaAttribute.ATTRIBUTE)) {
					if (offendingDefinitions.contains(var
							.getAttributeValue(UnitPragmaAttribute.ATTRIBUTE))) {
						var.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new IncorrectUnitDefinitionMarker(variableName));
					}
				}
			}

		} else if (rootElement instanceof IContextRoot) {
			// find and update constants
			IConstant[] allConstants = rootElement.getContextRoot()
					.getConstants();

			for (IConstant cst : allConstants) {
				// reset inferred unit
				cst.setAttributeValue(InferredUnitPragmaAttribute.ATTRIBUTE,
						"", new NullProgressMonitor());

				String constantName = cst.getIdentifierString();
				if (variables.containsKey(constantName)) {
					cst.setAttributeValue(
							InferredUnitPragmaAttribute.ATTRIBUTE,
							variables.get(constantName),
							new NullProgressMonitor());

					if (variables.get(constantName).equals("error")) {
						cst.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new MultipleUnitsInferredMarker(constantName));
					}
					if (variables.get(constantName).equals("unknown")) {
						cst.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new IncorrectUnitDefinitionMarker(constantName));
					}
				}

				// check if the attached unit pragma (given by user) was marked
				// as offending
				if (cst.hasAttribute(UnitPragmaAttribute.ATTRIBUTE)) {
					if (offendingDefinitions.contains(cst
							.getAttributeValue(UnitPragmaAttribute.ATTRIBUTE))) {
						cst.createProblemMarker(
								InferredUnitPragmaAttribute.ATTRIBUTE,
								new MultipleUnitsInferredMarker(constantName));
					}
				}
			}
		} else {
			throw new ExecutionException(
					"Cannot execute unit analysis on this element type. Type of "
							+ rootElement.getComponentName() + " was: "
							+ rootElement.getClass());
		}
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
