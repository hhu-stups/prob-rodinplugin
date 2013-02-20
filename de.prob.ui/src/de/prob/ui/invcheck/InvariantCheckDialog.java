/**
 * 
 */
package de.prob.ui.invcheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author plagge
 * 
 */
public class InvariantCheckDialog extends Dialog {

	private final Collection<String> events;

	private ListViewer listviewer;
	private Collection<String> selected;

	public InvariantCheckDialog(final Shell parentShell,
			final Collection<String> events) {
		super(parentShell);
		this.events = new ArrayList<String>(events);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		composite.getShell().setText("Constraint Based Invariant Check");
		composite.setLayout(new GridLayout());
		// final Button check = new Button(composite, SWT.CHECK);
		final Label label1 = new Label(composite, SWT.NONE);
		label1.setText("ProB will search for events which can violate the invariant. You can limit the analysis to some events.");
		final Label label2 = new Label(composite, SWT.NONE);
		label2.setText("If no event is chosen, all events will be checked.");
		listviewer = new ListViewer(composite, SWT.MULTI | SWT.V_SCROLL
				| SWT.BORDER);
		final GridData listLayout = new GridData();
		listLayout.grabExcessHorizontalSpace = true;
		listviewer.getList().setLayoutData(listLayout);
		listviewer.setContentProvider(new ArrayContentProvider());
		listviewer.setInput(events);
		listviewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final ISelection sel = event.getSelection();
				if (sel instanceof IStructuredSelection) {
					final IStructuredSelection ssel = (IStructuredSelection) sel;
					final Collection<String> newSelection = new ArrayList<String>();
					for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
						final Object item = it.next();
						newSelection.add(item.toString());
					}
					selected = newSelection;
				}
			}
		});
		composite.pack();
		return composite;
	}

	public Collection<String> getSelected() {
		return selected;
	}
}
