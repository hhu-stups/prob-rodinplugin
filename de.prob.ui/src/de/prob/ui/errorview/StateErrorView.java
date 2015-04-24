/**
 * 
 */
package de.prob.ui.errorview;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.ui.StateBasedViewPart;
import de.prob.ui.errorview.ShownErrors.ShownStateError;

/**
 * This view shows the errors that are specific to a certain state.
 * 
 * @author plagge
 */
public class StateErrorView extends StateBasedViewPart {
	public static final String VIEWID = "de.prob.ui.EventErrorView";
	private TreeViewer errorTree;
	private Text text;
	private final ShownErrors errors = new ShownErrors();

	@Override
	public Control createStatePartControl(final Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH | SWT.NO_TRIM); //  can we use things likw SWT.NOTRIM

		createErrorTree(sashForm);

		text = new Text(sashForm, SWT.MULTI | SWT.V_SCROLL);
		text.setText("");
		text.setEditable(false);

		errorTree.addSelectionChangedListener(new ErrorSelection());

		return sashForm;
	}

	private void createErrorTree(final SashForm sashForm) {
		errorTree = new TreeViewer(sashForm);
		errorTree.getTree().setHeaderVisible(false);
		errorTree.getTree().setLinesVisible(false);
		errorTree.setAutoExpandLevel(2);

		TreeViewerColumn col = new TreeViewerColumn(errorTree, SWT.LEFT);
		col.getColumn().setText("Short description");
		col.getColumn().setResizable(true);
		col.getColumn().setWidth(175);

		errorTree.setContentProvider(new ErrorContentProvider());
		errorTree.setLabelProvider(new ErrorLabelProvider());
		errorTree.setInput(errors);
	}

	@Override
	public void stateChanged(final State currentState, final Operation operation) {
		errors.update(Animator.getAnimator().getMachineDescription(),
				currentState);
		errorTree.refresh();
	}

	private class ErrorSelection implements ISelectionChangedListener {

		public void selectionChanged(final SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			final String msg;
			if (selection.isEmpty()) {
				msg = "";
			} else {
				Object sel = selection.getFirstElement();
				if (sel instanceof ShownStateError) {
					final ShownStateError error = (ShownStateError) sel;
					msg = error.getError().getLongDescription();
				} else {
					msg = "";
				}
			}
			text.setText(msg);
		}
	}
}
