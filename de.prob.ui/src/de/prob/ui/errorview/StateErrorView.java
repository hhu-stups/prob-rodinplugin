/**
 * 
 */
package de.prob.ui.errorview;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

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
		// the errors above, the description of the selected one below
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);

		createErrorTree(sashForm);

		// no wrapping, so that long lines (e.g. values) keep their layout;
		// scroll bars when the description is too long or too wide
		text = new Text(sashForm, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		text.setText("");
		// a fixed-width font, so that values formatted by ProB stay aligned
		text.setFont(JFaceResources.getTextFont());
		sashForm.setWeights(new int[] { 2, 3 });

		errorTree.addSelectionChangedListener(new ErrorSelection());

		return sashForm;
	}

	private void createErrorTree(final SashForm sashForm) {
		errorTree = new TreeViewer(sashForm);
		errorTree.getTree().setHeaderVisible(false);
		errorTree.getTree().setLinesVisible(false);
		errorTree.setAutoExpandLevel(3);

		TreeViewerColumn col = new TreeViewerColumn(errorTree, SWT.LEFT);
		col.getColumn().setText("Short description");
		col.getColumn().setResizable(true);
		col.getColumn().setWidth(150);

		errorTree.setContentProvider(new ErrorContentProvider());
		errorTree.setLabelProvider(new ErrorLabelProvider());
		errorTree.setInput(errors);
		// the column fills the tree, or is wider (with a scroll bar) so that
		// no short description is cut off
		errorTree.getTree().addListener(SWT.Resize, e -> fitColumn());
		errorTree.addTreeListener(new ITreeViewerListener() {
			@Override
			public void treeExpanded(final TreeExpansionEvent event) {
				errorTree.getTree().getDisplay().asyncExec(() -> fitColumn());
			}

			@Override
			public void treeCollapsed(final TreeExpansionEvent event) {
			}
		});
	}

	private void fitColumn() {
		final Tree tree = errorTree.getTree();
		if (tree.isDisposed() || tree.getColumnCount() == 0)
			return;
		final TreeColumn column = tree.getColumn(0);
		column.pack();
		final int available = tree.getClientArea().width;
		if (column.getWidth() < available)
			column.setWidth(available);
	}

	@Override
	public void stateChanged(final State currentState, final Operation operation) {
		errors.update(Animator.getAnimator().getMachineDescription(),
				currentState);
		errorTree.refresh();
		fitColumn();
	}

	/**
	 * Expands all events and selects the first error, so that its description
	 * is shown, e.g. for the State view's "Show Event Errors".
	 */
	public void showAll() {
		errorTree.refresh();
		errorTree.expandAll();
		fitColumn();
		final TreeItem[] events = errorTree.getTree().getItems();
		if (events.length > 0 && events[0].getItemCount() > 0) {
			final Object first = events[0].getItem(0).getData();
			if (first != null)
				errorTree.setSelection(new StructuredSelection(first), true);
		}
	}

	private class ErrorSelection implements ISelectionChangedListener {
		@Override
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
