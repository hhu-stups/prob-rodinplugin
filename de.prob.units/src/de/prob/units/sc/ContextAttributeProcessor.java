package de.prob.units.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

import de.prob.units.Activator;
import de.prob.units.pragmas.UnitPragmaAttribute;

public class ContextAttributeProcessor extends SCProcessorModule {
	public static final IModuleType<ContextAttributeProcessor> MODULE_TYPE = SCCore
			.getModuleType(Activator.PLUGIN_ID + ".contextAttributeProcessor"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		assert (element instanceof IRodinFile);
		assert (target instanceof ISCContextRoot);

		// get all variables and copy over the attributes
		IRodinFile machineFile = (IRodinFile) element;
		IContextRoot constantRoot = (IContextRoot) machineFile.getRoot();

		ISCContextRoot scContextRoot = (ISCContextRoot) target;

		IConstant[] constants = constantRoot.getConstants();

		if (constants.length == 0)
			return;

		for (IConstant constant : constants) {
			ISCConstant scConstant = scContextRoot.getSCConstant(constant
					.getIdentifierString());

			// might have been filtered out by previous modules
			if (scConstant.exists()) {
				// original might not contain the attribute
				if (constant.hasAttribute(UnitPragmaAttribute.ATTRIBUTE)) {
					String attribute = constant
							.getAttributeValue(UnitPragmaAttribute.ATTRIBUTE);

					scConstant.setAttributeValue(UnitPragmaAttribute.ATTRIBUTE,
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
