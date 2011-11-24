/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.ui.IWorkbenchPart;


public class AttributeTransferDropTargetListener extends
		AbstractTransferDropTargetListener {

	private IWorkbenchPart workbenchPart;

	public AttributeTransferDropTargetListener(EditPartViewer viewer,
			IWorkbenchPart workbenchPart) {
		super(viewer, AttributeTransfer.getInstance());
		this.workbenchPart = workbenchPart;
	}

	@Override
	protected void updateTargetRequest() {
	}

	@Override
	protected Request createTargetRequest() {
		AttributeRequest req = new AttributeRequest();
		req.setDropLocation(getDropLocation());
		Object transferObject = AttributeTransfer.getInstance().getObject();
		if (transferObject instanceof AttributeTransferObject)
			req
					.setAttributeTransferObject((AttributeTransferObject) transferObject);
		return req;
	}

	@Override
	protected void handleDragOperationChanged() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOperationChanged();
	}

	@Override
	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}

	@Override
	protected void handleDrop() {
		super.handleDrop();
		workbenchPart.setFocus();
	}

}
