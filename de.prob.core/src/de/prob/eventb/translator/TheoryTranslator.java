package de.prob.eventb.translator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import de.prob.core.internal.AnimationListenerProxy;


public class TheoryTranslator {

	
	public void translate() {
		final IExtensionRegistry extensionRegistry = Platform
				.getExtensionRegistry();
		final IExtensionPoint extensionPoint = extensionRegistry
				.getExtensionPoint("org.eventb.theory.core.deployedElements");
		for (final IExtension extension : extensionPoint.getExtensions()) {
			for (final IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				System.out.println(configurationElement.getAttribute("name"));
			}
		}
	}
	

	
}
