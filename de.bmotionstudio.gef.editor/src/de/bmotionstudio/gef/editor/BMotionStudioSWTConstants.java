/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BMotionStudioSWTConstants {

	public static final String RODIN_FONT_KEY = "org.rodinp.keyboard.textFont";

	public final static Color containerHighlighting1 = new Color(null, 200,
			200, 240);

	public final static Color containerHighlighting2 = new Color(null, 200,
			240, 200);

	public final static Font fontArial10 = new Font(Display.getDefault(),
			new FontData("Arial", 10, SWT.NONE));

}
