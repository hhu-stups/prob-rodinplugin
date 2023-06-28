/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.domainobjects.Operation;

public class SelectOperationDialog extends Dialog {

	private List<Operation> ops;
	private Operation selectedOperation;
	// private ComboViewer cb;
	private ListViewer listViewer;

	protected SelectOperationDialog(Shell parentShell, List<Operation> ops) {
		super(parentShell);
		this.ops = ops;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 15;
		gl.marginTop = 20;

		container.setLayout(gl);

		Label lb = new Label(container, SWT.NONE);
		lb.setText("Select an event:");
		lb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		listViewer = new ListViewer(container);
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setInput(ops);
		listViewer.getList().setLayoutData(new GridData(GridData.FILL_BOTH));

		// cb = new ComboViewer(container, SWT.NONE);
		// cb.setContentProvider(new ArrayContentProvider());
		// cb.setInput(ops);
		// cb.getCombo().setLayoutData(new GridData(200, 20));
		// cb.getCombo()
		// .setFont(
		// JFaceResources.getFontRegistry().get(
		// BasicUtils.RODIN_FONT_KEY));

		return container;

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Please select an event ...");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 250);
	}

	@Override
	protected void okPressed() {

		IStructuredSelection selection = (IStructuredSelection) listViewer
				.getSelection();
		selectedOperation = (Operation) selection.getFirstElement();

		if (selectedOperation == null) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"An error occurred", "Please select an event ...");
			return;
		} else {
			setReturnCode(OK);
			close();
		}

	}

	public Operation getSelectedOperation() {
		return this.selectedOperation;
	}

}
