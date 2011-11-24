/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import static org.eventb.core.IConvergenceElement.Convergence.*;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IConvergenceElement.Convergence;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.RodinDBException;

public class CreateRefinement implements IWorkspaceRunnable {
	private final IMachineRoot abs;
	private final IMachineRoot con;
	private final IContextRoot ctx;

	public CreateRefinement(final IMachineRoot abs, final IMachineRoot con,
			final IContextRoot ctx) {
		this.abs = abs;
		this.con = con;
		this.ctx = ctx;
	}

	public void run(final IProgressMonitor monitor) throws RodinDBException {
		con.getRodinFile().create(false, monitor);
		con.setConfiguration(abs.getConfiguration(), null);
		createRefinesMachineClause(monitor);
		copyChildrenOfType(con, abs, ISeesContext.ELEMENT_TYPE, null, monitor);
		ISeesContext seesClause = con.getSeesClause(ctx.getComponentName());
		seesClause.create(null, monitor);
		copyChildrenOfType(con, abs, IVariable.ELEMENT_TYPE, null, monitor);
		createEvents(monitor);
		con.getRodinFile().save(null, false);
	}

	private void createRefinesMachineClause(final IProgressMonitor monitor)
			throws RodinDBException {
		final IRefinesMachine refines = con.createChild(
				IRefinesMachine.ELEMENT_TYPE, null, monitor);
		refines.setAbstractMachineName(abs.getComponentName(), monitor);
	}

	@SuppressWarnings("unchecked")
	private <T extends IInternalElement> void copyChildrenOfType(
			final IEventBRoot destination, final IEventBRoot original,
			final IInternalElementType<T> type, final T additional,
			final IProgressMonitor monitor) throws RodinDBException {

		T[] elements = original.getChildrenOfType(type);
		if (elements.length == 0) {
			return;
		}
		if (additional != null) {
			List<T> list = Arrays.asList(elements);
			list.add(additional);
			elements = (T[]) list.toArray();
		}
		final IEventBRoot[] containers = new IEventBRoot[] { destination };
		final IRodinDB rodinDB = destination.getRodinDB();
		rodinDB.copy(elements, containers, null, null, false, monitor);
	}

	private void createEvents(final IProgressMonitor monitor)
			throws RodinDBException {
		final IEvent[] absEvts = abs.getChildrenOfType(IEvent.ELEMENT_TYPE);
		for (IEvent absEvt : absEvts) {
			createEvent(absEvt, monitor);
		}
	}

	private void createEvent(final IEvent absEvt, final IProgressMonitor monitor)
			throws RodinDBException {
		final String name = absEvt.getElementName();
		final String label = absEvt.getLabel();
		final IEvent conEvt = con.getEvent(name);
		conEvt.create(null, monitor);
		conEvt.setLabel(label, monitor);
		conEvt.setExtended(true, monitor);
		createRefinesEventClause(conEvt, label, monitor);
		if (absEvt.hasComment()) {
			conEvt.setComment(absEvt.getComment(), monitor);
		}
		setConvergence(conEvt, absEvt, monitor);
	}

	private void createRefinesEventClause(final IEvent conEvt,
			final String label, final IProgressMonitor monitor)
			throws RodinDBException {
		if (!label.equals(IEvent.INITIALISATION)) {
			final IRefinesEvent refines = conEvt.createChild(
					IRefinesEvent.ELEMENT_TYPE, null, monitor);
			refines.setAbstractEventLabel(label, monitor);
		}
	}

	private void setConvergence(final IEvent conEvt, final IEvent absEvt,
			final IProgressMonitor monitor) throws RodinDBException {
		final Convergence absCvg = absEvt.getConvergence();
		final Convergence conCvg = computeRefinementConvergence(absCvg);
		conEvt.setConvergence(conCvg, monitor);
	}

	private Convergence computeRefinementConvergence(final Convergence absCvg) {
		switch (absCvg) {
		case ANTICIPATED:
			return ANTICIPATED;
		case CONVERGENT:
		case ORDINARY:
			return ORDINARY;
		}
		return ORDINARY;
	}

}
