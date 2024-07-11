/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.bmotionstudio.gef.editor.BMotionStudioSWTConstants;
import de.bmotionstudio.gef.editor.eventb.EventBHelper;
import de.bmotionstudio.gef.editor.eventb.MachineContentObject;
import de.bmotionstudio.gef.editor.eventb.MachineOperation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.ExecuteOperationByPredicate;
import de.bmotionstudio.gef.editor.scheduler.PredicateOperation;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;
import de.bmotionstudio.gef.editor.scheduler.SchedulerWizard;

public class WizardExecuteOperationByPredicate extends SchedulerWizard {

	private class SchedulerExecuteOperationByPredicatePage extends WizardPage {

		private ComboViewer cbOperation;

		private Text txtPredicate;

		private Text txtMaxRandomOperations;

		private Label lbMaxRandomOperations;

		private Composite container;

		private Label renderedOpLabel;

		private Button checkboxRandomMode;

		private ScrolledComposite c1;

		public ComboViewer getCbOperation() {
			return cbOperation;
		}

		public Text getTxtMaxRandomOperations() {
			return txtMaxRandomOperations;
		}

		protected SchedulerExecuteOperationByPredicatePage(String pageName) {
			super(pageName);
		}

		public void createControl(final Composite parent) {

			final DataBindingContext dbc = new DataBindingContext();

			container = new Composite(parent, SWT.NONE);
			GridLayout gl = new GridLayout(2, false);
			container.setLayout(gl);

			Label lb = new Label(container, SWT.NONE);
			lb.setText("Select an operation: ");

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 50;

			cbOperation = new ComboViewer(container, SWT.NONE);
			cbOperation.getCombo().setLayoutData(new GridData(300, 50));

			lb = new Label(container, SWT.NONE);
			lb.setText("Predicate: ");
			lb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
			txtPredicate = new Text(container, SWT.BORDER | SWT.WRAP
					| SWT.V_SCROLL);
			txtPredicate.setLayoutData(gd);

			lb = new Label(container, SWT.NONE);
			lb.setText("Random mode: ");
			lb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
			checkboxRandomMode = new Button(container, SWT.CHECK);
			checkboxRandomMode.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setRandomVisibility(checkboxRandomMode.getSelection());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			lbMaxRandomOperations = new Label(container, SWT.NONE);
			lbMaxRandomOperations.setText("Max Random Operations: ");
			lbMaxRandomOperations.setLayoutData(new GridData(
					GridData.VERTICAL_ALIGN_BEGINNING));
			txtMaxRandomOperations = new Text(container, SWT.BORDER);
			txtMaxRandomOperations.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			txtMaxRandomOperations.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					String string = e.text;
					char[] chars = new char[string.length()];
					string.getChars(0, chars.length, chars, 0);
					for (int i = 0; i < chars.length; i++) {
						if (!('0' <= chars[i] && chars[i] <= '9')) {
							e.doit = false;
							return;
						}
					}
				}
			});
			setRandomVisibility(((ExecuteOperationByPredicate) getScheduler())
					.getPredicateOperation().isRandom());

			initBindings(dbc);

			IStructuredSelection structuredSelection = (IStructuredSelection) cbOperation
					.getSelection();

			if (!structuredSelection.isEmpty()) {
				createRenderedOperationContainer((MachineOperation) structuredSelection
						.getFirstElement());
			}

			setControl(container);

		}

		private void setRandomVisibility(boolean b) {
			if (lbMaxRandomOperations == null || txtMaxRandomOperations == null)
				return;
			lbMaxRandomOperations.setVisible(b);
			txtMaxRandomOperations.setVisible(b);
		}

		private void initBindings(DataBindingContext dbc) {

			// MachineContentList operationList = BMotionEditorPlugin
			// .getActiveEditor().getVisualization().getOperationList();
			// operationList.getMap().remove("INITIALISATION");

			ObservableListContentProvider cbOpContentProvider = new ObservableListContentProvider();
			cbOperation.setContentProvider(cbOpContentProvider);
			IObservableMap[] attributeMaps = {
				BeanProperties.value(MachineContentObject.class, "label").observeDetail(cbOpContentProvider.getKnownElements()),
			};
			cbOperation.setLabelProvider(new ObservableMapLabelProvider(
					attributeMaps));
			cbOperation.setInput(new WritableList(EventBHelper
					.getOperations(getBControl().getVisualization()),
					MachineOperation.class));
			cbOperation.getCombo().setFont(
					new Font(Display.getDefault(), new FontData("Arial", 10,
							SWT.NONE)));

			IObservableValue observeSelection = ViewerProperties.singleSelection().observe(cbOperation);

			dbc.bindValue(WidgetProperties.widgetSelection().observe(cbOperation.getCombo()),
					BeanProperties.value(PredicateOperation.class, "operationName")
							.observe(((ExecuteOperationByPredicate) getScheduler()).getPredicateOperation()),
					null, null);

			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(txtPredicate),
					BeanProperties.value(PredicateOperation.class, "predicate")
							.observe(((ExecuteOperationByPredicate) getScheduler()).getPredicateOperation()));

			observeSelection.addValueChangeListener(new IValueChangeListener() {
				public void handleValueChange(ValueChangeEvent event) {
					Object sel = event.getObservableValue().getValue();
					createRenderedOperationContainer((MachineOperation) sel);
				}
			});

			dbc.bindValue(WidgetProperties.widgetSelection().observe(checkboxRandomMode),
					BeanProperties.value(PredicateOperation.class, "random")
							.observe(((ExecuteOperationByPredicate) getScheduler()).getPredicateOperation()));

			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(txtMaxRandomOperations),
					BeanProperties.value(PredicateOperation.class, "maxrandom")
							.observe(((ExecuteOperationByPredicate) getScheduler()).getPredicateOperation()));

		}

		private void createRenderedOperationContainer(MachineOperation op) {

			if (c1 != null)
				c1.dispose();

			GridLayout gl = new GridLayout(1, true);
			gl.marginLeft = 5;
			gl.marginTop = 5;

			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			
			c1 = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL
					| SWT.V_SCROLL);
			c1.setBackground(ColorConstants.red);
			c1.setLayoutData(gd);

			Composite cp = new Composite(c1, SWT.NONE);
			cp.setLayout(gl);

			renderedOpLabel = new Label(cp, SWT.NONE);
			renderedOpLabel.setLayoutData(gd);
			renderedOpLabel.setText(op.getRenderedOperation());
			renderedOpLabel.setFont(JFaceResources.getFontRegistry().get(
					BMotionStudioSWTConstants.RODIN_FONT_KEY));

			c1.setContent(cp);
			c1.setExpandHorizontal(true);
			c1.setExpandVertical(true);
			c1.setMinSize(cp.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			container.layout();

		}

	}

	public WizardExecuteOperationByPredicate(BControl bcontrol,
			SchedulerEvent scheduler) {
		super(bcontrol, scheduler);
		addPage(new SchedulerExecuteOperationByPredicatePage(
				"SchedulerExecuteOperationByPredicatePage"));
	}

	@Override
	protected Boolean prepareToFinish() {

		SchedulerExecuteOperationByPredicatePage page = (SchedulerExecuteOperationByPredicatePage) getPage("SchedulerExecuteOperationByPredicatePage");

		String errorStr = "";

		if (((ExecuteOperationByPredicate) getScheduler())
				.getPredicateOperation().isRandom()
				&& !(Integer
						.valueOf(page.getTxtMaxRandomOperations().getText()) > 0))
			errorStr += "Max Random Operations must be greater than 0.\n";

		if (page.getCbOperation().getCombo().getSelectionIndex() == -1)
			errorStr += "Please select an operation.\n";

		if (errorStr.length() > 0) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"An Error occured", errorStr);
			return false;
		}

		// PredicateOperation predicateOperation =
		// ((ExecuteOperationByPredicate) getScheduler())
		// .getPredicateOperation();
		//
		// Observer observer = getBControl().getObserver(
		// ListenOperationByPredicate.ID);
		// ListenOperationByPredicate listenObserver;
		//
		// if (observer != null) {
		// listenObserver = (ListenOperationByPredicate) observer;
		// listenObserver
		// .removePredicateOperationByUniqueID(predicateOperation
		// .getUniqueID());
		// } else {
		// listenObserver = new ListenOperationByPredicate();
		// getBControl().addObserver(listenObserver);
		// }
		//
		// try {
		// listenObserver.addPredicateOperation(predicateOperation.clone());
		// } catch (CloneNotSupportedException e) {
		// e.printStackTrace();
		// }

		return true;

	}

	@Override
	public Point getSize() {
		return new Point(600, 600);
	}

}
