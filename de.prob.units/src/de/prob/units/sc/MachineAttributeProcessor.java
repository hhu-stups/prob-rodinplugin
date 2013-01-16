package de.prob.units.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

import de.prob.units.Activator;

public class MachineAttributeProcessor extends SCProcessorModule {
	public static final IModuleType<MachineAttributeProcessor> MODULE_TYPE = SCCore
			.getModuleType(Activator.PLUGIN_ID + ".machineAttributeProcessor"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("blah");

	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
