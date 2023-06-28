/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RenameWizard extends Wizard {

	private class RenamePage extends WizardPage {

		public Text nameText;

		public RenamePage(final String pageName) {
			super(pageName);
			setTitle("BMotion Studio Rename Wizard");
			setDescription("Rename a control");
		}

		public void createControl(final Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));

			Label lab = new Label(composite, SWT.NONE);
			lab.setText("Rename to: ");
			lab.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 90;

			nameText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			nameText.setText(oldName);
			nameText.setLayoutData(gd);

			setControl(composite);

		}

	}

	private final String oldName;
	private String newName;

	public RenameWizard(final String oldName) {
		this.oldName = oldName;
		this.newName = null;
		addPage(new RenamePage("MyRenamePage"));
	}

	@Override
	public boolean performFinish() {
		RenamePage page = (RenamePage) getPage("MyRenamePage");
		newName = page.nameText.getText();
		return true;
	}

	public String getRenameValue() {
		return newName;
	}

}
