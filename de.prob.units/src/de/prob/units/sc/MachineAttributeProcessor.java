package de.prob.units.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCVariable;
import org.eventb.core.IVariable;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

import de.prob.units.Activator;
import de.prob.units.pragmas.UnitPragmaAttribute;

public class MachineAttributeProcessor extends SCProcessorModule {
	public static final IModuleType<MachineAttributeProcessor> MODULE_TYPE = SCCore
			.getModuleType(Activator.PLUGIN_ID + ".machineAttributeProcessor"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		assert (element instanceof IRodinFile);
		assert (target instanceof ISCMachineRoot);

		// get all variables and copy over the attributes
		IRodinFile machineFile = (IRodinFile) element;
		IMachineRoot machineRoot = (IMachineRoot) machineFile.getRoot();

		ISCMachineRoot scMachineRoot = (ISCMachineRoot) target;

		IVariable[] variables = machineRoot.getVariables();

		if (variables.length == 0)
			return;

		for (IVariable var : variables) {
			String identifier = var.getIdentifierString();
			ISCVariable scVar = scMachineRoot.getSCVariable(identifier);

			// might have been filtered out by previous modules
			if (scVar.exists()) {
				// original might not contain the attribute
				if (var.hasAttribute(UnitPragmaAttribute.ATTRIBUTE)) {
					String attribute = var
							.getAttributeValue(UnitPragmaAttribute.ATTRIBUTE);

					scVar.setAttributeValue(UnitPragmaAttribute.ATTRIBUTE,
							attribute, monitor);
				}
			}
		}

	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
