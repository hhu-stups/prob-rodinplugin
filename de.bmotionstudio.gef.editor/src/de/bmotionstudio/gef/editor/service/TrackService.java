/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.service;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.bmotionstudio.gef.editor.AbstractBControlService;
import de.bmotionstudio.gef.editor.BControlCreationFactory;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Track;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.TrackPart;

/**
 * @author Lukas Ladenberger
 * 
 */
public class TrackService extends AbstractBControlService implements
		IBControlService {

	@Override
	public ToolEntry createToolEntry(Visualization visualization,
			IConfigurationElement configurationElement) {
		String sourcePluginID = configurationElement.getContributor().getName();
		String name = configurationElement.getAttribute("name");
		String icon = configurationElement.getAttribute("icon");
		return new ConnectionCreationToolEntry(name, "Create " + name,
				new BControlCreationFactory(Track.TYPE, visualization),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon));
	}

	@Override
	public BControl createControl(Visualization visualization) {
		return new Track(visualization);
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new TrackPart();
	}

}
