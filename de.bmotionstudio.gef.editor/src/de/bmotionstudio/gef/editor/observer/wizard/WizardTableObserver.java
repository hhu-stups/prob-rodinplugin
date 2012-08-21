/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer.wizard;

import java.util.ArrayList;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.PowerSetType;

import de.bmotionstudio.gef.editor.eventb.EventBHelper;
import de.bmotionstudio.gef.editor.eventb.MachineContentObject;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.observer.TableObserver;
import de.prob.unicode.UnicodeTranslator;

public class WizardTableObserver extends ObserverWizard {

	private class TableObserverPage extends WizardPage {

		private Text txtExpression;
		private Text txtPredicate;

		public Text getTxtExpression() {
			return txtExpression;
		}

		protected TableObserverPage(final String pageName) {
			super(pageName);
		}

		public void createControl(final Composite parent) {

			final DataBindingContext dbc = new DataBindingContext();

			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout(2, false));

			Composite conLeft = new Composite(container, SWT.NONE);
			conLeft.setLayoutData(new GridData(GridData.FILL_BOTH));
			conLeft.setLayout(new GridLayout(2, false));

			Label lb = new Label(conLeft, SWT.NONE);
			lb.setText("Predicate:");

			txtPredicate = new Text(conLeft, SWT.BORDER);
			txtPredicate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			txtPredicate.setFont(new Font(Display.getDefault(), new FontData(
					"Arial", 10, SWT.NONE)));

			lb = new Label(conLeft, SWT.NONE);
			lb.setText("Expression:");
			lb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

			txtExpression = new Text(conLeft, SWT.BORDER | SWT.MULTI
					| SWT.WRAP);
			txtExpression.setLayoutData(new GridData(GridData.FILL_BOTH));

			Composite conRight = new Composite(container, SWT.NONE);
			conRight.setLayoutData(new GridData(GridData.FILL_BOTH));
			conRight.setLayout(new GridLayout(1, false));

			lb = new Label(conRight, SWT.NONE);
			lb.setText("List of available power sets:");
			lb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

			ArrayList<String> relationList = new ArrayList<String>();
			
			java.util.List<MachineContentObject> constants = EventBHelper
					.getConstants(getBControl().getVisualization());
			for (MachineContentObject mobj : constants) {
				if (mobj.getType() instanceof PowerSetType) {
					Expression expression = ((PowerSetType) mobj.getType())
							.toExpression(FormulaFactory.getDefault());
					System.out.println(UnicodeTranslator.toAscii(expression
							.toString()));
					relationList.add(mobj.getLabel());
				}
			}

			java.util.List<MachineContentObject> variables = EventBHelper
					.getVariables(getBControl().getVisualization());
			for (MachineContentObject mobj : variables) {
				if (mobj.getType() instanceof PowerSetType) {
					Expression expression = ((PowerSetType) mobj.getType())
							.toExpression(FormulaFactory.getDefault());
					System.out.println(UnicodeTranslator.toAscii(expression
							.toString()));
					relationList.add(mobj.getLabel());
				}
			}

			final List list = new List(conRight, SWT.SINGLE | SWT.BORDER);
			list.setLayoutData(new GridData(GridData.FILL_BOTH));
			list.setItems(relationList.toArray(new String[relationList.size()]));

			list.addMouseListener(new MouseAdapter() {
				public void mouseDoubleClick(MouseEvent e) {
					String[] selection = list.getSelection();
					if (selection.length > 0)
						txtExpression.setText(txtExpression.getText() + " "
								+ selection[0]);
				}
			});
			initBindings(dbc);

			setControl(conLeft);

		}

		private void initBindings(DataBindingContext dbc) {

			dbc.bindValue(SWTObservables.observeText(txtPredicate, SWT.Modify),
					BeansObservables.observeValue(
							(TableObserver) getObserver(), "predicate"));

			dbc.bindValue(
					SWTObservables.observeText(txtExpression, SWT.Modify),
					BeansObservables.observeValue(
							(TableObserver) getObserver(), "expression"));

		}

	}

	public WizardTableObserver(BControl bcontrol,
			Observer bobserver) {
		super(bcontrol, bobserver);
		addPage(new TableObserverPage("TableObserverPage"));
	}

	@Override
	protected Boolean prepareToFinish() {

		TableObserverPage page = (TableObserverPage) getPage("TableObserverPage");

		String errorStr = "";

		if (page.getTxtExpression().getText().length() == 0)
			errorStr += "Please enter an expression.\n";

		if (page.getErrorMessage() != null)
			errorStr += "Please check the syntax/parser error.\n";

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
