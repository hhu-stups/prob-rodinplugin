/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.ExternalObserverScript;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;

public class WizardObserverExternalObserverScript extends ObserverWizard {

	private class ObserverExternalObserverScriptPage extends WizardPage {

		private Text txtScriptPath;

		protected ObserverExternalObserverScriptPage(final String pageName) {
			super(pageName);
		}

		public Text getTxtScriptPath() {
			return txtScriptPath;
		}

		public void createControl(final Composite parent) {

			final DataBindingContext dbc = new DataBindingContext();

			Composite container = new Composite(parent, SWT.NONE);
			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			container.setLayout(new GridLayout(2, false));

			Label lb = new Label(container, SWT.NONE);
			lb.setText("Script File:");

			txtScriptPath = new Text(container, SWT.BORDER);
			txtScriptPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			txtScriptPath.setFont(new Font(Display.getDefault(), new FontData(
					"Arial", 10, SWT.NONE)));

			initBindings(dbc);

			setControl(container);

		}

		private void initBindings(DataBindingContext dbc) {

			dbc.bindValue(
					SWTObservables.observeText(txtScriptPath, SWT.Modify),
					BeansObservables.observeValue(
							(ExternalObserverScript) getObserver(),
							"scriptPath"));

		}


	}

	public WizardObserverExternalObserverScript(BControl bcontrol,
			Observer bobserver) {
		super(bcontrol, bobserver);
		addPage(new ObserverExternalObserverScriptPage(
				"ObserverExternalObserverScriptPage"));
	}

	@Override
	protected Boolean prepareToFinish() {

		ObserverExternalObserverScriptPage page = (ObserverExternalObserverScriptPage) getPage("ObserverExternalObserverScriptPage");

		String errorStr = "";

		if (page.getTxtScriptPath().getText().length() == 0)
			errorStr += "Please enter a path for a script file.\n";

		if (errorStr.length() > 0) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"An Error occured", errorStr);
			return false;
		}

		return true;

	}

	@Override
	public Point getSize() {
		return new Point(600, 500);
	}

}
