/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.palette.ToolEntry;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;

/**
 * @author Lukas Ladenberger
 * 
 */
public interface IBControlService {

	public BControl createControl(Visualization visualization);

	public BMSAbstractEditPart createEditPart();

	public ToolEntry createToolEntry(Visualization visualization,
			IConfigurationElement configurationElement);

	public boolean showInPalette();

}