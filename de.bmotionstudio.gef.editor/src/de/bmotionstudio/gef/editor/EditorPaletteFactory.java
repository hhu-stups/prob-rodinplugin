/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;

import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.Visualization;

public class EditorPaletteFactory {

	private HashMap<String, PaletteDrawer> groupMap = new HashMap<String, PaletteDrawer>();

	/**
	 * Creates the PaletteRoot and adds all palette elements. Use this factory
	 * method to create a new palette for your graphical editor.
	 * 
	 * @param visualization
	 * 
	 * @param editor
	 * 
	 * @return a new PaletteRoot
	 */
	public PaletteRoot createPalette(Visualization visualization) {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette, visualization));
		createControls(palette, visualization);
		createFromExtension(palette, visualization);
		return palette;
	}

	private void createFromExtension(PaletteRoot palette,
			Visualization visualization) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.gef.editor.paletteEntry");
		// Iterate over controls
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				if ("entry".equals(configurationElement.getName())) {
					try {
						IInstallPaletteEntry entry = (IInstallPaletteEntry) configurationElement
								.createExecutableExtension("class");
						entry.installPaletteEntry(palette, groupMap);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void createControls(PaletteRoot palette, Visualization visualization) {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.gef.editor.control");

		// Iterate over groups
		for (IExtension extension : extensionPoint.getExtensions()) {

			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("group".equals(configurationElement.getName())) {

					String groupID = configurationElement.getAttribute("id");
					String groupName = configurationElement
							.getAttribute("name");

					PaletteDrawer componentsDrawer = new PaletteDrawer(
							groupName);
					if (!groupMap.containsKey(groupID))
						groupMap.put(groupID, componentsDrawer);

				}

			}

		}

		// Iterate over controls
		for (IExtension extension : extensionPoint.getExtensions()) {

			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("control".equals(configurationElement.getName())) {

					String groupID = configurationElement
							.getAttribute("groupid");
					PaletteDrawer groupDrawer = groupMap.get(groupID);

					if (groupDrawer != null) {

						// boolean createDefaultToolEntry = true;

						try {
							IBControlService service = (IBControlService) configurationElement
									.createExecutableExtension("service");
							if (service.showInPalette()) {
								ToolEntry toolEntry = service.createToolEntry(
										visualization, configurationElement);
								if (toolEntry != null) {
									groupDrawer.add(toolEntry);
								}
							}
						} catch (CoreException e) {
							// I think we can ignore the exception since
							// we create a default tool entry which is
							// independent from the configuration
							// element
						}

						// if (createDefaultToolEntry)
						// groupDrawer.add(createDefaultToolEntry(type,
						// visualization, configurationElement));

					}

				}

			}

		}

		for (Map.Entry<String, PaletteDrawer> entry : groupMap.entrySet()) {
			if (entry.getValue().getChildren().size() > 0)
				palette.add(entry.getValue());
		}

	}

	/**
	 * Create the "Tools" group.
	 * 
	 * @param visualization
	 */
	private PaletteContainer createToolsGroup(PaletteRoot palette,
			Visualization visualization) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);
		// Add a marquee tool to the group
		toolbar.add(new MarqueeToolEntry());
		// Add connector tool to the group
		toolbar.add(new ConnectionCreationToolEntry("Connection",
				"Universal Connector", new BControlCreationFactory(
						BConnection.TYPE, visualization), BMotionStudioImage
						.getImageDescriptor("icons/icon_connection16.gif"),
				BMotionStudioImage
						.getImageDescriptor("icons/icon_connection24.gif")));
		return toolbar;
	}

}
