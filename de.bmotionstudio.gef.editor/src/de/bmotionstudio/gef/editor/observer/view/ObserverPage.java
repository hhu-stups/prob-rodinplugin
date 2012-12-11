package de.bmotionstudio.gef.editor.observer.view;

import java.util.Collection;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;

public class ObserverPage extends Page implements ISelectionListener {

	private Composite container;

	private BControl selectedControl;

	private ListViewer listViewer;

	private Composite rightContainer;

	private HelpAction helpAction;

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		// layout.horizontalSpacing = 0;
		// layout.verticalSpacing = 0;
		container.setLayout(layout);
		listViewer = new ListViewer(container);
		listViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				Collection<?> observerList = (Collection<?>) inputElement;
				return observerList.toArray();
			}

		});
		listViewer.setLabelProvider(new ILabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getText(Object element) {
				Observer o = (Observer) element;
				return o.getName();
			}

			@Override
			public Image getImage(Object element) {
				return BMotionStudioImage
						.getImage(EditorImageRegistry.IMG_ICON_OBSERVER);
			}
		});

		GridData layoutData = new GridData(GridData.FILL_VERTICAL);
		layoutData.widthHint = 120;
		listViewer.getControl().setLayoutData(layoutData);
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				restoreHelpButton();
				if (rightContainer != null)
					rightContainer.dispose();
				if (event.getSelection() != null
						&& event.getSelection() instanceof StructuredSelection) {
					Object firstElement = ((StructuredSelection) event
							.getSelection())
							.getFirstElement();
					if (firstElement instanceof Observer) {
						Observer o = (Observer) firstElement;
						ObserverWizard wizard = o
								.getWizard(Display.getDefault()
										.getActiveShell(), selectedControl);
						// IWizardPage page = wizard.getPages()[0];
						rightContainer = new Composite(container, SWT.NONE);
						rightContainer.setLayoutData(new GridData(
								GridData.FILL_BOTH));
						rightContainer.setLayout(new FillLayout());
						wizard.createWizardContent(rightContainer);
						helpAction.setEnabled(true);
						helpAction.setObserverID(o.getID());
					}
				}
				container.layout();
			}
		});
		
		getSite().getPage().addPostSelectionListener(this);
		createActions();
		createMenu(getSite());

	}

	private void createActions() {
		helpAction = new HelpAction();
	}

	private void createMenu(final IPageSite pageSite) {
		pageSite.getActionBars().getToolBarManager().add(helpAction);
	}

	@Override
	public Control getControl() {
		return container;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection != null && selection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof BMSAbstractEditPart) {
				selectedControl = (BControl) ((BMSAbstractEditPart) firstElement)
						.getModel();
				if (!listViewer.getControl().isDisposed()) {
					Collection<Observer> values = selectedControl
							.getObservers().values();
					listViewer.setInput(values);
					if (values.size() > 0) {
						Observer firstObserver = values.iterator().next();
						listViewer.setSelection(new StructuredSelection(
								firstObserver));
					} else {
						restoreHelpButton();
						if (rightContainer != null)
							rightContainer.dispose();
					}
				}
			}
		}
	}

	@Override
	public void dispose() {
		getSite().getPage().addPostSelectionListener(this);
		super.dispose();
	}

	private void restoreHelpButton() {
		helpAction.setEnabled(false);
	}

}
