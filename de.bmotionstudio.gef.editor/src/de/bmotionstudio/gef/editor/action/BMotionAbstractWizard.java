/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;

public abstract class BMotionAbstractWizard extends WizardDialog {

	public static final int DELETE = 3;

	private IWorkbenchPart workbenchPart;

	private String deleteToolTip;

	public BMotionAbstractWizard(IWorkbenchPart workbenchPart, IWizard newWizard) {
		super(workbenchPart.getSite().getShell(), newWizard);
		this.workbenchPart = workbenchPart;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		composite.setFont(parent.getFont());

		// create help control if needed
		if (isHelpAvailable()) {
			Control helpControl = createHelpControl(composite);
			((GridData) helpControl.getLayoutData()).horizontalIndent = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		}

		Control deleteControl = createDeleteControl(composite);
		((GridData) deleteControl.getLayoutData()).horizontalIndent = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		setHelpAvailable(false);
		Control buttonSection = super.createButtonBar(composite);
		((GridData) buttonSection.getLayoutData()).grabExcessHorizontalSpace = true;
		return composite;
	}

	private Control createDeleteControl(Composite parent) {
		return createDeleteImageButton(parent,
				BMotionStudioImage
						.getImage(EditorImageRegistry.IMG_ICON_DELETE21));
	}

	private ToolBar createDeleteImageButton(Composite parent, Image image) {
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.NO_FOCUS);
		((GridLayout) parent.getLayout()).numColumns++;
		toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		final Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		toolBar.setCursor(cursor);
		toolBar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				cursor.dispose();
			}
		});
		ToolItem deleteToolItem = new ToolItem(toolBar, SWT.NONE);
		deleteToolItem.setImage(image);
		deleteToolItem.setToolTipText(deleteToolTip);
		deleteToolItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deletePressed();
			}
		});
		return toolBar;
	}

	protected abstract void deletePressed();

	public IWorkbenchPart getWorkbenchPart() {
		return workbenchPart;
	}

	protected void setDeleteToolTip(String msg) {
		this.deleteToolTip = msg;
	}

}
