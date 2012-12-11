/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.model.BControl;

public abstract class BMotionAbstractWizard extends TrayDialog {

	private BControl control;
	
	public static final int CLOSE = 2;

	public BMotionAbstractWizard(Shell shell, BControl control) {
		super(shell);
		this.control = control;
	}

	public BControl getBControl() {
		return this.control;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}

	public abstract String getName();
	
	@Override
	protected int getShellStyle() {
		return SWT.SHELL_TRIM;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createWizardContent(container);

		return container;

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.CLOSE_ID == buttonId)
			closePressed();
	}

	protected void closePressed() {
		setReturnCode(CLOSE);
		close();
	}

	public abstract Control createWizardContent(Composite parent);

}
