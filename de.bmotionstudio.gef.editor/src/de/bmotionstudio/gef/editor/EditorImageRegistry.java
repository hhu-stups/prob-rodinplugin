/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

public class EditorImageRegistry implements IBMotionStudioImageRegistry {

	public static final String IMG_ICON_ADD = "icon_add";
	public static final String IMG_ICON_CHOP = "icon_chop";
	public static final String IMG_ICON_DELETE = "icon_delete";
	public static final String IMG_ICON_DELETE21 = "icon_delete21";
	public static final String IMG_ICON_EDIT = "icon_edit";
	public static final String IMG_ICON_CHECKED = "icon_checked";
	public static final String IMG_ICON_UNCHECKED = "icon_unchecked";
	public static final String IMG_ICON_OBSERVER = "icon_observer";
	public static final String IMG_ICON_LOADING = "icon_loading";
	public static final String IMG_ICON_LIBRARY = "icon_library";
	public static final String IMG_ICON_ASCRIPT = "icon_ascript";
	public static final String IMG_ICON_UP = "icon_up";
	public static final String IMG_ICON_DOWN = "icon_down";
	public static final String IMG_ICON_CONNECTION16 = "icon_connection16";
	public static final String IMG_ICON_CONNECTION24 = "icon_connection24";
	public static final String IMG_ICON_NEW_WIZ = "icon_new_wiz";
	public static final String IMG_ICON_DELETE_EDIT = "icon_delete_edit";
	public static final String IMG_ICON_TR_UP = "icon_tr_up";
	public static final String IMG_ICON_TR_LEFT = "icon_tr_left";

	public static final String IMG_ICON_JPG = "icon_jpg";
	public static final String IMG_ICON_GIF = "icon_gif";

	public static final String IMG_SPLASH = "splash";

	public void registerImages() {

		BMotionStudioImage.registerImage(IMG_ICON_ADD,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_add.gif");
		BMotionStudioImage.registerImage(IMG_ICON_CHOP,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_chop.gif");
		BMotionStudioImage.registerImage(IMG_ICON_DELETE,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_delete.gif");
		BMotionStudioImage.registerImage(IMG_ICON_DELETE21,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_delete21.png");
		BMotionStudioImage.registerImage(IMG_ICON_CHECKED,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_checked.gif");
		BMotionStudioImage.registerImage(IMG_ICON_UNCHECKED,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_unchecked.gif");
		BMotionStudioImage.registerImage(IMG_ICON_EDIT,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_edit.gif");
		BMotionStudioImage.registerImage(IMG_ICON_LOADING,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_loading.gif");
		BMotionStudioImage.registerImage(IMG_ICON_LIBRARY,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_library.gif");
		BMotionStudioImage.registerImage(IMG_ICON_ASCRIPT,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_ascript.png");
		BMotionStudioImage.registerImage(IMG_ICON_UP,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_up.gif");
		BMotionStudioImage.registerImage(IMG_ICON_DOWN,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_down.gif");
		BMotionStudioImage.registerImage(IMG_ICON_CONNECTION16,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_connection16.gif");
		BMotionStudioImage.registerImage(IMG_ICON_CONNECTION24,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_connection24.gif");
		BMotionStudioImage.registerImage(IMG_ICON_NEW_WIZ, "org.eclipse.ui",
				"$nl$/icons/full/etool16/new_wiz.gif");
		BMotionStudioImage.registerImage(IMG_ICON_DELETE_EDIT,
				"org.eclipse.ui", "$nl$/icons/full/etool16/delete_edit.gif");

		BMotionStudioImage
				.registerImage(IMG_ICON_TR_UP, BMotionEditorPlugin.PLUGIN_ID,
						"icons/eclipse16/updated_co.gif");
		BMotionStudioImage.registerImage(IMG_ICON_TR_LEFT,
				BMotionEditorPlugin.PLUGIN_ID,
				"icons/eclipse16/updated_col.gif");

		BMotionStudioImage.registerImage(IMG_ICON_JPG,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_jpg.gif");
		BMotionStudioImage.registerImage(IMG_ICON_GIF,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_gif.gif");

		BMotionStudioImage.registerImage(IMG_ICON_OBSERVER,
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_observer.gif");

		BMotionStudioImage.registerImage(IMG_SPLASH,
				BMotionEditorPlugin.PLUGIN_ID, "icons/splash.jpg");

	}

}
