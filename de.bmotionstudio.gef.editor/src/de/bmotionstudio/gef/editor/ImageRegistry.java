/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;


public class ImageRegistry implements IBMotionStudioImageRegistry {

	public static final String IMG_RADIOBUTTON_CHECKED = "img_radiobutton_checked";
	public static final String IMG_RADIOBUTTON_UNCHECKED = "img_radiobutton_unchecked";

	public void registerImages() {

		BMotionStudioImage.registerImage(IMG_RADIOBUTTON_CHECKED,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_radiobutton_c.gif");

		BMotionStudioImage.registerImage(IMG_RADIOBUTTON_UNCHECKED,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_radiobutton_uc.gif");

	}

}
