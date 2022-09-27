/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor;

import java.util.HashMap;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;

/**
 * @author Lukas Ladenberger
 * 
 */
public interface IInstallPaletteEntry {

	public void installPaletteEntry(PaletteRoot palette,
			HashMap<String, PaletteDrawer> groups);

}
