/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.eventb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.basis.ContextRoot;
import org.eventb.core.basis.EventBRoot;
import org.eventb.core.basis.MachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.logging.Logger;

public final class EventBHelper {

	private static FormulaFactory formularFactory = FormulaFactory.getDefault();

	public static EventBRoot getCorrespondingFile(IFile file,
			String machineFileName) {
		IRodinProject rProject = RodinCore.valueOf(file.getProject());
		EventBRoot root = null;
		if (rProject != null) {
			IRodinFile rFile = rProject.getRodinFile(machineFileName);
			if (rFile != null && rFile.getRoot() instanceof EventBRoot)
				root = (EventBRoot) rFile.getRoot();
		}
		return root;
	}

	public static List<MachineOperation> getOperations(
			Visualization visualization) {

		ArrayList<MachineOperation> tmpSet = new ArrayList<MachineOperation>();

		if (visualization.getLanguage().equals("EventB")) {

			EventBRoot correspondingFile = getCorrespondingFile(
					visualization.getProjectFile(),
					visualization.getMachineName());

			if (correspondingFile instanceof MachineRoot
					&& correspondingFile.exists()) {

				ISCMachineRoot machineRoot = correspondingFile
						.getSCMachineRoot();

				try {

					ISCEvent[] events = machineRoot.getSCEvents();

					for (ISCEvent event : events) {

						List<String> parSet = new ArrayList<String>();
						List<String> guardSet = new ArrayList<String>();

						for (ISCParameter par : event.getSCParameters())
							parSet.add(par.getIdentifierString());

						for (ISCGuard guard : event.getSCGuards())
							guardSet.add(guard.getPredicateString());

						MachineOperation op = new MachineOperation(
								event.getLabel(), parSet, guardSet,
								renderEvent(event));
						tmpSet.add(op);

					}

				} catch (CoreException e) {
					String message = "Rodin DB Exception while getting operations: "
							+ e.getLocalizedMessage();
					Logger.notifyUser(message, e);
					return Collections
							.unmodifiableList(new ArrayList<MachineOperation>());
				}

			} else if (visualization.getLanguage().equals("ClassicalB")) {
				// TODO: Implement me!!!
			}

		}

		return tmpSet;

	}

	public static List<MachineContentObject> getVariables(
			Visualization visualization) {

		EventBRoot correspondingFile = getCorrespondingFile(
				visualization.getProjectFile(), visualization.getMachineName());

		ArrayList<MachineContentObject> tmpSet = new ArrayList<MachineContentObject>();

		if (correspondingFile instanceof MachineRoot
				&& correspondingFile.exists()) {

			ISCMachineRoot machineRoot = correspondingFile.getSCMachineRoot();

			try {
				ISCVariable[] vars = machineRoot.getSCVariables();
				for (ISCVariable var : vars) {
					MachineContentObject machinevar = new MachineContentObject(
							var.getIdentifierString());
					machinevar.setType(var.getType(formularFactory));
					tmpSet.add(machinevar);
				}
			} catch (CoreException e) {
				String message = "Rodin DB Exception while getting variables: "
						+ e.getLocalizedMessage();
				Logger.notifyUser(message, e);
				return Collections
						.unmodifiableList(new ArrayList<MachineContentObject>());
			}

		}

		return tmpSet;

	}

	public static List<MachineContentObject> getInvariants(
			Visualization visualization) {

		EventBRoot correspondingFile = getCorrespondingFile(
				visualization.getProjectFile(), visualization.getMachineName());

		ArrayList<MachineContentObject> tmpSet = new ArrayList<MachineContentObject>();

		if (correspondingFile instanceof MachineRoot
				&& correspondingFile.exists()) {

			ISCMachineRoot machineRoot = correspondingFile.getSCMachineRoot();

			try {
				ISCInvariant[] invariants = machineRoot.getSCInvariants();
				for (ISCInvariant inv : invariants) {
					MachineContentObject machineinv = new MachineContentObject(
							inv.getPredicateString());
					tmpSet.add(machineinv);
				}
			} catch (RodinDBException e) {
				String message = "Rodin DB Exception while getting invariants: "
						+ e.getLocalizedMessage();
				Logger.notifyUser(message, e);
				return Collections
						.unmodifiableList(new ArrayList<MachineContentObject>());
			}

		}

		return tmpSet;

	}

	public static List<MachineContentObject> getConstants(
			Visualization visualization) {

		EventBRoot correspondingFile = getCorrespondingFile(
				visualization.getProjectFile(), visualization.getMachineName());

		ArrayList<MachineContentObject> tmpSet = new ArrayList<MachineContentObject>();

		if (correspondingFile.exists()) {

			try {
				if (correspondingFile instanceof MachineRoot) {

					ISCMachineRoot machineRoot = correspondingFile
							.getSCMachineRoot();

					ISCInternalContext[] seenContexts = machineRoot
							.getSCSeenContexts();
					for (ISCInternalContext context : seenContexts) {

						for (ISCConstant constant : context.getSCConstants()) {
							try {
								MachineContentObject machineinv = new MachineContentObject(
										constant.getIdentifierString());
								machineinv.setType(constant
										.getType(formularFactory));
								tmpSet.add(machineinv);
							} catch (CoreException e) {
								String message = "Rodin DB Exception while getting variables: "
										+ e.getLocalizedMessage();
								Logger.notifyUser(message, e);
								return Collections
										.unmodifiableList(new ArrayList<MachineContentObject>());
							}

						}

					}

				} else if (correspondingFile instanceof ContextRoot) {

					ISCContextRoot contextRoot = correspondingFile
							.getSCContextRoot();
					for (ISCConstant constant : contextRoot.getSCConstants()) {
						MachineContentObject machineinv = new MachineContentObject(
								constant.getIdentifierString());
						try {
							machineinv.setType(constant
									.getType(formularFactory));
						} catch (CoreException e) {
							String message = "Rodin DB Exception while getting variables: "
									+ e.getLocalizedMessage();
							Logger.notifyUser(message, e);
							return Collections
									.unmodifiableList(new ArrayList<MachineContentObject>());
						}
						tmpSet.add(machineinv);
					}

				}

			} catch (RodinDBException e) {
				String message = "Rodin DB Exception while getting constants: "
						+ e.getLocalizedMessage();
				Logger.notifyUser(message, e);
				return Collections
						.unmodifiableList(new ArrayList<MachineContentObject>());
			}

		}

		return tmpSet;

	}

	// Changed RodinDBException to CoreException for Rodin 3
	// Might not be an intentional change in Rodin?
	public static String renderEvent(ISCEvent event) throws CoreException {
		StringBuffer sb = new StringBuffer();
		sb.append("event ");
		sb.append(event.getLabel());
		sb.append("\n");

		if (event.getSCParameters().length > 0) {
			sb.append("  any ");
			for (ISCParameter parameter : event.getSCParameters()) {
				sb.append(parameter.getIdentifierString());
				sb.append(" ");
			}
			sb.append("\n");

		}

		if (event.getSCGuards().length > 0) {
			sb.append("  where\n");
			for (ISCGuard guard : event.getSCGuards()) {
				sb.append("    @");
				sb.append(guard.getLabel());
				sb.append(" ");
				sb.append(guard.getPredicateString());
				sb.append("\n");
			}
		}

		if (event.getSCActions().length > 0) {
			sb.append("  then\n");
			for (ISCAction action : event.getSCActions()) {
				sb.append("    @");
				sb.append(action.getLabel());
				sb.append(" ");
				sb.append(action.getAssignmentString());
				sb.append("\n");
			}
		}
		sb.append("end");
		return sb.toString();
	}

}
