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
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCVariable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.logging.Logger;

public final class EventBHelper {

	public static ISCMachineRoot getCorrespondingFile(IFile file,
			String machineFileName) {
		IRodinProject rProject = RodinCore.valueOf(file.getProject());
		ISCMachineRoot machineRoot = null;
		if (rProject != null) {
			IRodinFile rFile = rProject.getRodinFile(machineFileName);
			if (rFile != null && rFile.getRoot() instanceof IMachineRoot)
				machineRoot = ((IMachineRoot) rFile.getRoot())
						.getSCMachineRoot();
		}
		return machineRoot;
	}

	public static List<MachineOperation> getOperations(
			Visualization visualization) {

		ArrayList<MachineOperation> tmpSet = new ArrayList<MachineOperation>();

		if (visualization.getLanguage().equals("EventB")) {

			ISCMachineRoot machineRoot = null;
			machineRoot = getCorrespondingFile(visualization.getProjectFile(),
					visualization.getMachineName());

			if (machineRoot != null) {

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
								event.getLabel(), parSet, guardSet);
						tmpSet.add(op);

					}

				} catch (RodinDBException e) {
					String message = "Rodin DB Exception while getting operations: "
							+ e.getLocalizedMessage();
					Logger.notifyUser(message, e);
					return Collections
							.unmodifiableList(new ArrayList<MachineOperation>());
				}

			}

		} else if (visualization.getLanguage().equals("ClassicalB")) {
			// TODO: Implement me!!!
		}

		return tmpSet;

	}

	public static List<MachineContentObject> getVariables(
			Visualization visualization) {

		ISCMachineRoot machineRoot = null;

		machineRoot = getCorrespondingFile(visualization.getProjectFile(),
				visualization.getMachineName());

		ISCVariable[] vars = null;
		ArrayList<MachineContentObject> tmpSet = new ArrayList<MachineContentObject>();

		try {
			vars = machineRoot.getSCVariables();

			for (ISCVariable var : vars) {

				MachineContentObject machinevar = new MachineContentObject(
						var.getIdentifierString());
				tmpSet.add(machinevar);

			}
		} catch (RodinDBException e) {
			String message = "Rodin DB Exception while getting variables: "
					+ e.getLocalizedMessage();
			Logger.notifyUser(message, e);
			return Collections
					.unmodifiableList(new ArrayList<MachineContentObject>());
		}

		return tmpSet;

	}

	public static List<MachineContentObject> getInvariants(
			Visualization visualization) {

		ISCMachineRoot machineRoot = null;

		machineRoot = getCorrespondingFile(visualization.getProjectFile(),
				visualization.getMachineName());

		ISCInvariant[] invariants = null;
		ArrayList<MachineContentObject> tmpSet = new ArrayList<MachineContentObject>();

		try {
			invariants = machineRoot.getSCInvariants();

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

		return tmpSet;
	}

	public static List<MachineContentObject> getConstants(
			Visualization visualization) {

		ISCMachineRoot machineRoot = null;

		machineRoot = getCorrespondingFile(visualization.getProjectFile(),
				visualization.getMachineName());

		ArrayList<MachineContentObject> tmpSet = new ArrayList<MachineContentObject>();

		try {

			ISCInternalContext[] seenContexts = machineRoot.getSCSeenContexts();
			for (ISCInternalContext context : seenContexts) {

				for (ISCConstant constant : context.getSCConstants()) {

					MachineContentObject machineinv = new MachineContentObject(
							constant.getIdentifierString());
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

		return tmpSet;
	}

}
