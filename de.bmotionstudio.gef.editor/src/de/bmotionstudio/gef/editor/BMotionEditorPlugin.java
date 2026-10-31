/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.gef.editor.internal.BControlListConverter;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BControlList;
import de.bmotionstudio.gef.editor.model.BMotionGuide;
import de.bmotionstudio.gef.editor.model.Visualization;

/**
 * The activator class controls the plug-in life cycle
 */
public class BMotionEditorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.bmotionstudio.gef.editor";

	public static final String FILEEXT_STUDIO = "bmso";

	// The shared instance
	private static BMotionEditorPlugin plugin;

	private static HashMap<String, IConfigurationElement> controlExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<String, IConfigurationElement> observerExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<String, IConfigurationElement> schedulerExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<String, IConfigurationElement> controlServices = new HashMap<String, IConfigurationElement>();

	IExtensionRegistry registry = Platform.getExtensionRegistry();

	static BMotionStudioEditorPage activeBMotionStudioEditor = null;

	/**
	 * The constructor
	 */
	public BMotionEditorPlugin() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		initExtensionClasses();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static BMotionEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Get the active workbench page.
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	/**
	 * Get the active editor.
	 * 
	 * @return current active editor
	 */
	public static BMotionStudioEditor getActiveEditor() {
		if (getActivePage() != null) {
			if (getActivePage().getActiveEditor() instanceof BMotionStudioEditor)
				return (BMotionStudioEditor) getActivePage().getActiveEditor();
		}
		return null;
	}

	/**
	 * Getting the current active page from the active workbench window.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	private void initExtensionClass(String extensionPointID,
			ArrayList<String> elementIDs, String getAttribute,
			HashMap<String, IConfigurationElement> hashMap) {

		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(extensionPointID);
		for (IExtension extension : extensionPoint.getExtensions()) {

			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if (elementIDs.contains(configurationElement.getName())) {

					String atr = configurationElement
							.getAttribute(getAttribute);

					hashMap.put(atr, configurationElement);

				}

			}

		}

	}

	private void initExtensionClasses() {

		ArrayList<String> elementIDs = new ArrayList<String>();
		elementIDs.add("control");
		initExtensionClass("de.bmotionstudio.gef.editor.control", elementIDs,
				"id", controlExtensions);

		elementIDs.clear();
		elementIDs.add("control");
		initExtensionClass("de.bmotionstudio.gef.editor.control", elementIDs,
				"id", controlServices);

		elementIDs.clear();
		elementIDs.add("observer");
		initExtensionClass("de.bmotionstudio.gef.editor.observer", elementIDs,
				"class", observerExtensions);

		elementIDs.clear();
		elementIDs.add("schedulerEvent");
		initExtensionClass("de.bmotionstudio.gef.editor.schedulerEvent",
				elementIDs, "class", schedulerExtensions);

	}

	public static IConfigurationElement getControlExtension(String ident) {
		return controlExtensions.get(ident);
	}

	public static IConfigurationElement getObserverExtension(String ident) {
		return observerExtensions.get(ident);
	}

	public static IConfigurationElement getSchedulerExtension(String ident) {
		return schedulerExtensions.get(ident);
	}

	public static HashMap<String, IConfigurationElement> getSchedulerExtensions() {
		return schedulerExtensions;
	}

	public static HashMap<String, IConfigurationElement> getControlServices() {
		return controlServices;
	}

	/**
	 * Creates a {@link DocumentBuilderFactory} that does not process DTDs or
	 * external entities, so that opening a crafted file cannot read local
	 * files or access the network (XXE).
	 */
	public static DocumentBuilderFactory createSecureDocumentBuilderFactory() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// BMotion Studio files never contain a DOCTYPE
		setFeatureIfSupported(factory,
				"http://apache.org/xml/features/disallow-doctype-decl", true);
		setFeatureIfSupported(factory,
				"http://xml.org/sax/features/external-general-entities", false);
		setFeatureIfSupported(factory,
				"http://xml.org/sax/features/external-parameter-entities", false);
		setFeatureIfSupported(factory,
				"http://apache.org/xml/features/nonvalidating/load-external-dtd",
				false);
		setFeatureIfSupported(factory, XMLConstants.FEATURE_SECURE_PROCESSING,
				true);
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		return factory;
	}

	private static void setFeatureIfSupported(DocumentBuilderFactory factory,
			String feature, boolean value) {
		try {
			factory.setFeature(feature, value);
		} catch (ParserConfigurationException e) {
			// feature not supported by this parser implementation
		}
	}

	public static void allowTypes(XStream xstream) {
		xstream.allowTypesByWildcard(new String[] {
			"de.bmotionstudio.gef.editor.**",
			"org.eclipse.draw2d.geometry.Point",
			"org.eclipse.swt.graphics.RGB",
		});
	}

	public static void setAliases(XStream xstream) {
		xstream.registerConverter(new BControlListConverter());
		xstream.alias("control", BControl.class);
		xstream.alias("visualization", Visualization.class);
		xstream.alias("guide", BMotionGuide.class);
		xstream.alias("connection", BConnection.class);
		xstream.alias("children", BControlList.class);
		allowTypes(xstream);
	}

}
