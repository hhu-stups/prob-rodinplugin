/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class BMotionStudioImage {

	private static ImageRegistry imageReg = new ImageRegistry();
	private static boolean isInit = false;

	public static final String IMG_LOGO_BMOTION = "logo_bmotion";
	public static final String IMG_LOGO_BMOTION64 = "logo_bmotion64";

	public static ImageDescriptor getImageDescriptor(final String path) {
		return getImageDescriptor(BMotionEditorPlugin.PLUGIN_ID, path);
	}

	public static ImageDescriptor getImageDescriptor(final String pluginID,
			final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path);
	}

	public static void registerImage(final String key, final String path) {
		ImageDescriptor desc = getImageDescriptor(path);
		imageReg.put(key, desc);
	}

	public static void registerImage(final String key, final String pluginID,
			final String path) {
		ImageDescriptor desc = getImageDescriptor(pluginID, path);
		imageReg.put(key, desc);
	}

	public static Image getImage(final String key) {
		if (!isInit)
			initializeImageRegistry();
		return imageReg.get(key);
	}

	public static Image getBControlImage(final String bcontrolID) {
		if (!isInit)
			initializeImageRegistry();
		return getImage("icon_control_" + bcontrolID);
	}

	private static void initializeImageRegistry() {
		registerImage(IMG_LOGO_BMOTION, "icons/logo_bmotion.png");
		registerImage(IMG_LOGO_BMOTION64, "icons/logo_bmotion_64.png");

		registerBControlImages();

		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint extensionPoint = reg
				.getExtensionPoint("de.bmotionstudio.gef.editor.registerImages");

		for (final IExtension extension : extensionPoint.getExtensions()) {
			for (final IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("registerImages".equals(configurationElement.getName())) {

					try {

						IBMotionStudioImageRegistry imageReg = (IBMotionStudioImageRegistry) configurationElement
								.createExecutableExtension("class");

						imageReg.registerImages();

					} catch (CoreException e) {
						e.printStackTrace();
					}

				}

			}

		}

		isInit = true;

	}

	private static void registerBControlImages() {

		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.gef.editor.control");

		for (final IExtension extension : extensionPoint.getExtensions()) {

			for (final IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("control".equals(configurationElement.getName())) {

					final String icon = configurationElement
							.getAttribute("icon");
					final String ID = configurationElement.getAttribute("id");
					final String sourcePluginID = configurationElement
							.getContributor().getName();

					final String key = "icon_control_" + ID;

					registerImage(key, sourcePluginID, icon);

				}

			}

		}

	}

}
