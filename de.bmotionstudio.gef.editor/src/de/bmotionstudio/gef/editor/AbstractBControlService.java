/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.bmotionstudio.gef.editor.internal.BControlTemplate;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BControlTreeEditPart;
import de.bmotionstudio.gef.editor.part.BMSAbstractTreeEditPart;

/**
 * @author Lukas Ladenberger
 * 
 */
public abstract class AbstractBControlService {

	public ToolEntry createToolEntry(Visualization visualization,
			IConfigurationElement configurationElement) {
		String name = configurationElement.getAttribute("name");
		String icon = configurationElement.getAttribute("icon");
		String type = configurationElement.getAttribute("id");
		// Get the source plug-in (from the control extension)
		String sourcePluginID = configurationElement.getContributor().getName();
		return new CombinedTemplateCreationEntry(name, "Create " + name,
				new BControlTemplate(type), new BControlCreationFactory(type,
						visualization),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon));
	}

	public boolean showInPalette() {
		return true;
	}

	public BMSAbstractTreeEditPart createTreeEditPart() {
		return new BControlTreeEditPart();
	}

}
