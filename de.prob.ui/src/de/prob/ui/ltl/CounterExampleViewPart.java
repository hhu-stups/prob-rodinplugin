package de.prob.ui.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.services.ISourceProviderService;

import de.prob.core.Animator;
import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryItem;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleState;
import de.prob.logging.Logger;
import de.prob.ui.StateBasedViewPart;

/***
 * Provides a view for a counter-example
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleViewPart extends StateBasedViewPart {
	private static final String ID = "de.prob.ui.ltl.CounterExampleView";

	private static final String DATA_KEY = "tabdata";

	public static enum ViewType {
		INTERACTIVE, TREE, TABLE
	};

	private static class TabData {
		private final CounterExample counterExample;

		private final StackLayout layout;

		// Table view
		private final Composite tableView;
		private final TableViewer tableViewer;

		// Tree view
		private final Composite treeView;
		private final TreeViewer treeViewer;

		// Graphical view
		private final ScalableRootEditPart editPart;
		private final GraphicalViewer graphicalViewer;
		private final Control interactiveView;

		public TabData(CounterExample counterExample, StackLayout layout,
				Composite tableView, TableViewer tableViewer,
				Composite treeView, TreeViewer treeViewer,
				ScalableRootEditPart editPart, GraphicalViewer graphicalViewer,
				Control interactiveView) {
			super();
			this.counterExample = counterExample;
			this.layout = layout;
			this.tableView = tableView;
			this.tableViewer = tableViewer;
			this.treeView = treeView;
			this.treeViewer = treeViewer;
			this.editPart = editPart;
			this.graphicalViewer = graphicalViewer;
			this.interactiveView = interactiveView;
		}
	}

	private final List<CounterExample> counterExamples = new ArrayList<CounterExample>();

	private CTabFolder tabFolder;
	private ViewType viewType = ViewType.INTERACTIVE;
	private int currentIndex = -1;

	public static CounterExampleViewPart showDefault() {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		CounterExampleViewPart counterExampleView = null;
		try {
			counterExampleView = (CounterExampleViewPart) workbenchPage
					.showView(ID);
		} catch (PartInitException e) {
			Logger.notifyUser("Failed to show the LTL view.", e);
		}
		return counterExampleView;
	}

	public static CounterExampleViewPart getDefault() {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		return (CounterExampleViewPart) workbenchPage.findView(ID);
	}

	@Override
	protected Control createStatePartControl(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.BORDER);

		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				if (tabFolder.getItemCount() == 1)
					updateCounterExampleLoadedProvider(false);
			}
		});

		return tabFolder;
	}

	public void addCounterExample(final CounterExample counterExample) {
		initializeMenuSetting();

		updateCounterExampleLoadedProvider(true);
		counterExamples.add(counterExample);

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				final CTabItem tabItem = createTabItem(counterExample);
				tabFolder.setSelection(tabItem);
				tabFolder.update();
			}
		};

		Display.getDefault().asyncExec(runnable);
	}

	private void initializeMenuSetting() {
		if (tabFolder.getItemCount() == 0) {
			MenuManager manager = (MenuManager) getViewSite().getActionBars()
					.getMenuManager();

			if (manager.getSize() > 0) {
				if (manager.getItems()[0] instanceof MenuManager) {
					manager = (MenuManager) manager.getItems()[0];

					if (manager.getSize() > 0)
						if (manager.getItems()[0] instanceof CommandContributionItem) {
							CommandContributionItem item = (CommandContributionItem) manager
									.getItems()[0];

							ParameterizedCommand parameterizedCommand = item
									.getCommand();

							Command command = parameterizedCommand.getCommand();

							try {
								HandlerUtil.updateRadioState(command,
										viewType.name());
							} catch (ExecutionException e) {
							}
						}
				}
			}
		}
	}

	public void zoomInCounterExample() {
		final ZoomManager zoomManager = getZoomManager();
		if (zoomManager != null) {
			if (zoomManager != null && zoomManager.canZoomIn()) {
				zoomManager.setZoom(zoomManager.getNextZoomLevel());
			}
		}
	}

	public void zoomOutCounterExample() {
		final ZoomManager zoomManager = getZoomManager();
		if (zoomManager != null) {
			if (zoomManager != null && zoomManager.canZoomOut()) {
				zoomManager.setZoom(zoomManager.getPreviousZoomLevel());
			}
		}
	}

	private ZoomManager getZoomManager() {
		final TabData data = getCurrentTabData();
		return data == null ? null : data.editPart.getZoomManager();
	}

	public void printCounterExample() {
		final TabData data = getCurrentTabData();
		if (data != null) {
			final GraphicalViewer graphicalViewer = data.graphicalViewer;
			final PrintDialog dialog = new PrintDialog(graphicalViewer
					.getControl().getShell(), SWT.NULL);

			try {
				final PrinterData printerData = dialog.open();
				if (printerData != null) {
					final PrintGraphicalViewerOperation viewerOperation = new PrintGraphicalViewerOperation(
							new Printer(printerData), graphicalViewer);

					viewerOperation.run(getTitle());
				}
			} catch (SWTException e) {
				Logger.notifyUser("Failed to print the LTL counter example.", e);
			}
		}
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
		for (CTabItem tabItem : tabFolder.getItems()) {
			final TabData data = getTabData(tabItem);
			updateTopControl(data);
		}
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	protected void stateChanged(final State activeState,
			final Operation operation) {
		final TabData data = getCurrentTabData();
		if (data != null) {
			if (activeState.isInitialized()) {
				final CounterExample ce = data.counterExample;
				final List<Operation> fullPath = ce.getFullPath();

				final Animator animator = Animator.getAnimator();
				final History history = animator.getHistory();

				if (isCounterExampleLoadedInHistory(data.counterExample)) {
					final int initPathSize = ce.getInitPath().size();

					currentIndex = history.getCurrentPosition() - initPathSize;

					// HistoryItem item = history.getCurrent();

					if (ce.getPathType() == PathType.INFINITE
							&& currentIndex == fullPath.size() - initPathSize) {
						currentIndex = ce.getLoopEntry();
					}
				}

				data.treeViewer.refresh();
				data.tableViewer.refresh();
				// We know that each element is of type
				// EditPart, but AbstractEditPart.getChildren() returns just
				// a list
				@SuppressWarnings("unchecked")
				List<EditPart> children = data.editPart.getChildren();
				for (EditPart child : children) {
					child.refresh();
				}

				currentIndex = -1;
			}
		}
	}

	private boolean isCounterExampleLoadedInHistory(final CounterExample ce) {
		final List<Operation> fullPath = ce.getFullPath();

		final Animator animator = Animator.getAnimator();
		final History history = animator.getHistory();
		final List<HistoryItem> historyItems = new ArrayList<HistoryItem>(
				Arrays.asList(history.getAllItems()));

		final boolean isLoaded;
		if (!historyItems.isEmpty()) {
			if (historyItems.get(historyItems.size() - 1).getOperation() == null) {
				historyItems.remove(historyItems.size() - 1);
			}

			if (fullPath.size() == historyItems.size()) {
				boolean ceIsEqual = true;
				for (int i = 0; i < fullPath.size(); i++) {
					final Operation ceOperation = fullPath.get(i);
					final Operation histOperation = historyItems.get(i)
							.getOperation();
					if (!ceOperation.equals(histOperation)) {
						ceIsEqual = false;
						break;
					}
				}
				isLoaded = ceIsEqual;
			} else {
				isLoaded = false;
			}
		} else {
			isLoaded = false;
		}
		return isLoaded;
	}

	@Override
	protected void stateReset() {
		super.stateReset();
		updateCounterExampleLoadedProvider(false);
	}

	@Override
	public void dispose() {
		super.dispose();
		updateCounterExampleLoadedProvider(false);
	}

	private void updateCounterExampleLoadedProvider(boolean enabled) {
		ISourceProviderService service = (ISourceProviderService) getSite()
				.getService(ISourceProviderService.class);
		CounterExampleLoadedProvider provider = (CounterExampleLoadedProvider) service
				.getSourceProvider(CounterExampleLoadedProvider.SERVICE);

		provider.setEnabled(enabled);
	}

	private CTabItem createTabItem(final CounterExample counterExample) {
		final CTabItem tabItem = new CTabItem(tabFolder, SWT.CLOSE);
		tabItem.setText(counterExample.getPropositionRoot().toString());

		final Composite sashForm = new SashForm(tabFolder, SWT.HORIZONTAL);
		tabItem.setControl(sashForm);

		final Composite composite = new Composite(sashForm, SWT.None);

		final StackLayout layout = new StackLayout();
		composite.setLayout(layout);

		final Composite tableView = new Composite(composite, SWT.None);
		TableViewer tableViewer = createTableViewer(tableView, counterExample);
		// createPopupMenu(tableViewer.getTable(), tableViewer);

		final Composite treeView = new Composite(composite, SWT.None);
		treeView.setLayout(new FillLayout());
		TreeViewer treeViewer = createTreeViewer(treeView, counterExample);
		// createPopupMenu(treeViewer.getTree(), treeViewer);

		final ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		final GraphicalViewer graphicalViewer = new ScrollingGraphicalViewer();

		final Control interactiveView = graphicalViewer
				.createControl(composite);
		interactiveView.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		// createPopupMenu(interactiveView, graphicalViewer);

		graphicalViewer.setRootEditPart(rootEditPart);
		graphicalViewer.setEditPartFactory(new CounterExampleEditPartFactory());
		graphicalViewer.setContents(counterExample);

		EditDomain editDomain = new DefaultEditDomain(null);
		editDomain.addViewer(graphicalViewer);

		final TabData tabData = new TabData(counterExample, layout, tableView,
				tableViewer, treeView, treeViewer, rootEditPart,
				graphicalViewer, interactiveView);
		tabItem.setData(DATA_KEY, tabData);

		updateTopControl(tabData);

		return tabItem;
	}

	private void updateTopControl(final TabData data) {
		final Control topControl;
		switch (viewType) {
		case TABLE:
			topControl = data.tableView;
			break;
		case TREE:
			topControl = data.treeView;
			break;
		case INTERACTIVE:
			topControl = data.interactiveView;
			break;
		default:
			throw new IllegalStateException(
					"Unexpected view type in LTL counter-example view");
		}

		final StackLayout layout = data.layout;
		layout.topControl = topControl;
		if (layout.topControl != null) {
			final Composite parent = layout.topControl.getParent();
			if (parent != null) {
				parent.layout();
			}
		}

	}

	private TableViewer createTableViewer(Composite parent,
			final CounterExample counterExample) {
		final CounterExampleTableViewer tableViewer = new CounterExampleTableViewer(
				parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		final TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);
		createEventColumn(tableViewer, layout);

		final Collection<CounterExampleProposition> propositions = counterExample
				.getPropositionRoot().getChildren();

		for (CounterExampleProposition proposition : propositions) {
			createPropositionColumn(tableViewer, layout, proposition);
		}

		tableViewer.getTable().setToolTipText(
				"Click to show the state in the history");

		tableViewer.getTable()
				.addMouseListener(
						new CounterExampleTableMouseAdapter(tableViewer,
								counterExample));

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(counterExample.getStates());

		return tableViewer;
	}

	private void createEventColumn(final TableViewer tableViewer,
			final TableColumnLayout layout) {
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new TableColumnEventLabelProvider());
		tableViewerColumn.getColumn().setText("Event");
		tableViewerColumn.getColumn().setAlignment(SWT.CENTER);
		layout.setColumnData(tableViewerColumn.getColumn(),
				new ColumnWeightData(1));
	}

	private void createPropositionColumn(final TableViewer tableViewer,
			final TableColumnLayout layout,
			final CounterExampleProposition proposition) {
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new TableColumnValueLabelProvider(
				proposition));
		tableViewerColumn.getColumn().setText(proposition.toString());
		tableViewerColumn.getColumn().setAlignment(SWT.CENTER);
		layout.setColumnData(tableViewerColumn.getColumn(),
				new ColumnWeightData(1));
	}

	private TreeViewer createTreeViewer(Composite parent,
			CounterExample counterExample) {
		final CounterExampleTreeViewer treeViewer = new CounterExampleTreeViewer(
				parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);

		TreeViewerColumn propositionColumn = new TreeViewerColumn(treeViewer,
				SWT.CENTER);
		propositionColumn
				.setLabelProvider(new TreeColumnPropositionLabelProvider());
		propositionColumn.getColumn().setAlignment(SWT.CENTER);
		propositionColumn.getColumn().setText("Proposition");

		for (int j = 0; j < counterExample.getStates().size(); j++) {
			TreeViewerColumn column = new TreeViewerColumn(treeViewer,
					SWT.CENTER);
			column.getColumn().setAlignment(SWT.CENTER);

			CounterExampleState state = counterExample.getStates().get(j);
			column.setLabelProvider(new TreeColumnValueLabelProvider(state));
			Operation operation = state.getOperation();

			if (operation != null)
				column.getColumn().setText(operation.getName());
			else
				column.getColumn().setText("No operation");

			column.getColumn().pack();
		}

		treeViewer.getTree().setToolTipText(
				"Click to show the state in the history");

		treeViewer.getTree().addMouseListener(
				new CounterExampleTreeMouseAdapter(treeViewer, counterExample));

		// treeViewer.getTree().addMouseMoveListener(
		// new CounterExampleTreeMouseMoveAdapter(treeViewer));

		treeViewer.setContentProvider(new CounterExampleContentProvider());
		treeViewer.setInput(Arrays
				.asList(new CounterExampleProposition[] { counterExample
						.getPropositionRoot() }));

		treeViewer.expandAll();
		propositionColumn.getColumn().pack();

		return treeViewer;
	}

	public CounterExample getCurrentCounterExample() {
		final TabData data = getCurrentTabData();
		return data == null ? null : data.counterExample;
	}

	private TabData getCurrentTabData() {
		final TabData data;
		if (tabFolder != null) {
			final CTabItem selection = tabFolder.getSelection();
			data = getTabData(selection);
		} else {
			data = null;
		}
		return data;
	}

	private TabData getTabData(final CTabItem item) {
		return item == null ? null : (TabData) item.getData(DATA_KEY);
	}

	// private void createPopupMenu(Control control,
	// ISelectionProvider selectionProvider) {
	// MenuManager menuManager = new MenuManager();
	// Menu viewMenu = menuManager.createContextMenu(control);
	// control.setMenu(viewMenu);
	// getSite().registerContextMenu(menuManager, selectionProvider);
	// getSite().setSelectionProvider(selectionProvider);
	// }
}