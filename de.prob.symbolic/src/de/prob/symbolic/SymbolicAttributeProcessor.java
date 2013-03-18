/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.symbolic;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
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

public class SymbolicAttributeProcessor extends SCProcessorModule {
	public static final IModuleType<SymbolicAttributeProcessor> MODULE_TYPE = SCCore
			.getModuleType(Activator.PLUGIN_ID + ".symbolicAttributeProcessor"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		assert (element instanceof IRodinFile);
		assert (target instanceof ISCContextRoot);

		// get all variables and copy over the attributes
		IRodinFile contextFile = (IRodinFile) element;
		IContextRoot contextRoot = (IContextRoot) contextFile.getRoot();

		ISCContextRoot scContextRoot = (ISCContextRoot) target;

		IConstant[] constants = contextRoot.getConstants();
		ISCConstant[] scconstants = scContextRoot.getSCConstants();

		if (constants.length == 0 || scconstants.length == 0)
			return;

		for (IConstant constant : constants) {
			String identifier = constant
					.getAttributeValue(EventBAttributes.IDENTIFIER_ATTRIBUTE);
			ISCConstant scConstant = scContextRoot.getSCConstant(identifier);

			// might have been filtered out by previous modules
			if (scConstant.exists()) {
				// original might not contain the attribute
				if (constant.hasAttribute(SymbolicAttribute.ATTRIBUTE)) {
					boolean attribute = constant
							.getAttributeValue(SymbolicAttribute.ATTRIBUTE);

					scConstant.setAttributeValue(SymbolicAttribute.ATTRIBUTE,
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
