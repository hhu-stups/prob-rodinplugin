/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.command.FitImageCommand;
import de.bmotionstudio.gef.editor.model.BControl;

public class FitImageAction extends SelectionAction {

	public final static String ID = "de.bmotionstudio.gef.editor.action.fitImage";

	public FitImageAction(final IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	protected void init() {
		setText("Fit size to image");
		setToolTipText("Fit size to image");
		setId(ID);
		ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(
				BMotionEditorPlugin.PLUGIN_ID, "icons/icon_fitimage.png");
		if (icon != null) {
			setImageDescriptor(icon);
		}
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		List<?> selectedObjects = getSelectedObjects();
		for (Object obj : selectedObjects) {
			if (obj instanceof EditPart) {
				EditPart part = (EditPart) obj;
				if (part.getModel() instanceof BControl) {
					BControl bcontrol = (BControl) part.getModel();
					if (bcontrol
							.getAttributeValue(AttributeConstants.ATTRIBUTE_IMAGE) != null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public FitImageCommand createFitImageCommand(List<BControl> modelList,
			Map<BControl, org.eclipse.draw2d.geometry.Rectangle> newSizeMap) {
		FitImageCommand command = new FitImageCommand();
		command.setModelList(modelList);
		command.setNewSizeMap(newSizeMap);
		return command;
	}

	public void run() {

		List<BControl> modelList = new ArrayList<BControl>();
		Map<BControl, org.eclipse.draw2d.geometry.Rectangle> newSizeMap = new HashMap<BControl, org.eclipse.draw2d.geometry.Rectangle>();

		List<?> selectedObjects = getSelectedObjects();

		for (Object obj : selectedObjects) {

			if (obj instanceof EditPart) {

				EditPart part = (EditPart) obj;
				BControl control = (BControl) part.getModel();

				Object imgAttribute = control
						.getAttributeValue(AttributeConstants.ATTRIBUTE_IMAGE);

				if (imgAttribute != null) {

					String imagePath = control.getAttributeValue(
							AttributeConstants.ATTRIBUTE_IMAGE).toString();

					Rectangle imageBounds = null;
					Image img = null;

					IFile pFile = control.getVisualization().getProjectFile();

					if (pFile != null) {
						final String myPath = (pFile.getProject().getLocation()
								+ "/images/" + imagePath).replace("file:", "");
						if (new File(myPath).exists() && imagePath.length() > 0) {
							img = new Image(Display.getCurrent(), myPath);
							imageBounds = img.getBounds();
						}
					}

					if (imageBounds != null) {

						modelList.add(control);
						newSizeMap
								.put(control,
										new org.eclipse.draw2d.geometry.Rectangle(
												Integer.valueOf(control
														.getAttributeValue(
																AttributeConstants.ATTRIBUTE_X)
														.toString()),
												Integer.valueOf(control
														.getAttributeValue(
																AttributeConstants.ATTRIBUTE_Y)
														.toString()),
												imageBounds.width,
												imageBounds.height));

					}

					if (img != null) {
						img.dispose();
					}

				}

			}

		}

		execute(createFitImageCommand(modelList, newSizeMap));

	}

}
