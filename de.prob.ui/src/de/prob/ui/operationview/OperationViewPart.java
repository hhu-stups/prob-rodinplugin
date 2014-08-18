/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.services.ISourceProviderService;

import de.prob.core.domainobjects.*;
import de.prob.ui.StateBasedViewPart;
import de.prob.ui.services.ModelLoadedProvider;

/**
 * This class defines the view that shows the currently available operations.
 */
public class OperationViewPart extends StateBasedViewPart implements
		FilterListener {

	private Composite pageComposite;

	@Override
	public Control createStatePartControl(final Composite parent) {
		pageComposite = new Composite(parent, SWT.NULL);
		pageComposite.setLayout(new FillLayout());
		OperationTableViewer.create(pageComposite, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
		hookContextMenu();
		updateModelLoadedProvider();
		return pageComposite;
	}

	/**
	 * This method is called whenever the activeState of the Animator changes
	 * (i.e. Animation of a new file, running an operation)
	 */
	@Override
	protected void stateChanged(final State activeState,
			final Operation operation) {
		OperationTableViewer.getInstance().getViewer().setInput(activeState);
		OperationTableViewer.getInstance().refresh();

	}

	private void hookContextMenu() {
		final OperationViewPart x = this;
		TableViewer viewer = OperationTableViewer.getInstance().getViewer();
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				x.fillContextMenu(manager);
			}
		});
		Control c = viewer.getControl();
		Menu menu = menuMgr.createContextMenu(c);
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(final IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	protected void stateReset() {
		OperationTableViewer.destroy();
	}

	@Override
	public void filtersChanged() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				OperationTableViewer.getInstance().refresh();
			}
		});
	}

	private void updateModelLoadedProvider() {
		ISourceProviderService service = (ISourceProviderService) this
				.getSite().getService(ISourceProviderService.class);
		ModelLoadedProvider sourceProvider = (ModelLoadedProvider) service
				.getSourceProvider(ModelLoadedProvider.SERVICE);
		sourceProvider.setEnabled(true);
	}
}
