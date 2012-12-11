/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.ColumnObserver;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;

public class WizardColumnObserver extends ObserverWizard {

	private Text txtExpression;
	private Text txtPredicate;

	@Override
	public Control createWizardContent(Composite parent) {

		final DataBindingContext dbc = new DataBindingContext();

		Composite container = new Composite(parent, SWT.NONE);

		container.setLayout(new GridLayout(2, false));

		Label lb = new Label(container, SWT.NONE);
		lb.setText("Predicate:");

		txtPredicate = new Text(container, SWT.BORDER);
		txtPredicate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtPredicate.setFont(new Font(Display.getDefault(), new FontData(
				"Arial", 10, SWT.NONE)));

		lb = new Label(container, SWT.NONE);
		lb.setText("Expression:");
		lb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		txtExpression = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		txtExpression.setLayoutData(new GridData(GridData.FILL_BOTH));

		initBindings(dbc);

		return container;

	}

	private void initBindings(DataBindingContext dbc) {

		dbc.bindValue(SWTObservables.observeText(txtPredicate, SWT.Modify),
				BeansObservables.observeValue((ColumnObserver) getObserver(),
						"predicate"));

		dbc.bindValue(SWTObservables.observeText(txtExpression, SWT.Modify),
				BeansObservables.observeValue((ColumnObserver) getObserver(),
						"expression"));

	}


	public WizardColumnObserver(Shell shell, BControl bcontrol,
			Observer bobserver) {
		super(shell, bcontrol, bobserver);
	}

	// @Override
	// protected Boolean prepareToFinish() {
	//
	// ColumnObserverPage page = (ColumnObserverPage)
	// getPage("ColumnObserverPage");
	//
	// String errorStr = "";
	//
	// if (page.getTxtExpression().getText().length() == 0)
	// errorStr += "Please enter an expression.\n";
	//
	// if (page.getErrorMessage() != null)
	// errorStr += "Please check the syntax/parser error.\n";
	//
	// if (errorStr.length() > 0) {
	// MessageDialog.openError(Display.getDefault().getActiveShell(),
	// "An Error occured", errorStr);
	// return false;
	// }
	//
	// return true;
	//
	// }

	@Override
	public Point getSize() {
		return new Point(600, 500);
	}

}
