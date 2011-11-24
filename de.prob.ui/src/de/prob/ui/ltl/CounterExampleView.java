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
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.part.ViewPart;

import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;

/***
 * Provides a view for a counter-example
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleView extends ViewPart {
	public static final String ID = "de.prob.ui.ltl.CounterExampleView";
	private static final String COUNTEREXAMPLE_DATA_KEY = "counterexample";

	private final List<CounterExample> counterExamples = new ArrayList<CounterExample>();

	private static CTabFolder tabFolder;

	private RootEditPart rootEditPart;
	private GraphicalViewer graphicalViewer;
	private EditDomain editDomain;

	@Override
	public void createPartControl(final Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.BORDER);
	}

	@Override
	public void setFocus() {
		tabFolder.setFocus();
	}

	public void addCounterExample(final CounterExample counterExample) {
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

	public CounterExample getCurrentCounterExample() {
		final CTabItem selection = tabFolder.getSelection();
		return selection == null ? null : (CounterExample) selection
				.getData(COUNTEREXAMPLE_DATA_KEY);
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart) rootEditPart).getZoomManager();
		else if (type == GraphicalViewer.class) {
			return graphicalViewer;
		} else if (type == EditDomain.class) {
			return editDomain;
		} else if (type == IWorkbenchPage.class) {
			return getSite().getPage();
		}

		return null;
	}

	public static void setViewType(String viewType) {
		for (CTabItem tabItem : tabFolder.getItems()) {
			Control control = tabItem.getControl();

			if (!(control instanceof SashForm))
				return;

			SashForm sashForm = (SashForm) control;

			if (sashForm.getChildren().length <= 0)
				return;
			if (!(sashForm.getChildren()[0] instanceof CounterExampleComposite))
				return;

			CounterExampleComposite composite = (CounterExampleComposite) sashForm
					.getChildren()[0];

			if (!(composite.getLayout() instanceof StackLayout))
				return;

			StackLayout stackLayout = (StackLayout) composite.getLayout();

			stackLayout.topControl = viewType.equals("Table") ? composite
					.getTableView() : composite.getTreeView();

			composite.layout();
		}
	}

	private CTabItem createTabItem(final CounterExample counterExample) {
		final CTabItem tabItem = new CTabItem(tabFolder, SWT.CLOSE);
		tabItem.setText(counterExample.getPropositionRoot().toString());
		tabItem.setData(COUNTEREXAMPLE_DATA_KEY, counterExample);

		final SashForm sashForm = new SashForm(tabFolder, SWT.HORIZONTAL);
		tabItem.setControl(sashForm);

		CounterExampleComposite composite = new CounterExampleComposite(
				sashForm, SWT.None);

		StackLayout stackLayout = new StackLayout();
		composite.setLayout(stackLayout);

		Composite tableView = new Composite(composite, SWT.None);
		createTableViewer(tableView, counterExample);

		Composite treeView = new Composite(composite, SWT.None);
		treeView.setLayout(new FillLayout());
		createTreeViewer(treeView, counterExample);

		composite.setTableComposite(tableView);
		composite.setTreeComposite(treeView);

		if (tabFolder.getChildren().length == 1) {
			MenuManager manager = (MenuManager) getViewSite().getActionBars()
					.getMenuManager();

			if (manager.getItems().length > 0) {
				if (manager.getItems()[0] instanceof CommandContributionItem) {
					CommandContributionItem item = (CommandContributionItem) manager
							.getItems()[0];

					ParameterizedCommand parameterizedCommand = item
							.getCommand();

					try {
						Command command = parameterizedCommand.getCommand();
						HandlerUtil.updateRadioState(command, "Table");
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		String currentViewType = CounterExampleViewMenuHandler
				.getCurrentViewType();
		stackLayout.topControl = currentViewType.equals("Table") ? tableView
				: treeView;

		rootEditPart = new ScalableRootEditPart();

		graphicalViewer = new ScrollingGraphicalViewer();
		graphicalViewer.createControl(sashForm);
		graphicalViewer.getControl().setBackground(
				Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		graphicalViewer.setRootEditPart(rootEditPart);
		graphicalViewer.setEditPartFactory(new CounterExampleEditPartFactory());
		graphicalViewer.setContents(counterExample);

		editDomain = new DefaultEditDomain(null);
		editDomain.addViewer(graphicalViewer);

		ZoomManager zoomManager = ((ScalableRootEditPart) rootEditPart)
				.getZoomManager();

		IAction zoomIn = new ZoomInAction(zoomManager);
		IAction zoomOut = new ZoomOutAction(zoomManager);
		IAction print = new PrintAction(this);

		IActionBars actionBar = getViewSite().getActionBars();
		actionBar.getMenuManager().add(zoomIn);
		actionBar.getMenuManager().add(zoomOut);
		actionBar.getMenuManager().add(print);

		return tabItem;
	}

	private TableViewer createTableViewer(Composite parent,
			final CounterExample counterExample) {
		final TableViewer tableViewer = new TableViewer(parent);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		final TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);
		createEventColumn(tableViewer, layout);

		final Collection<CounterExampleProposition> propositions = counterExample
				.getPropositions();

		for (CounterExampleProposition proposition : propositions) {
			createPropositionColumn(tableViewer, layout, proposition);
		}

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(counterExample.getStates());

		return tableViewer;
	}

	private TreeViewer createTreeViewer(Composite parent,
			CounterExample counterExample) {
		final CounterExampleTreeViewer treeViewer = new CounterExampleTreeViewer(
				parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);

		TreeViewerColumn propositionColumn = new TreeViewerColumn(treeViewer,
				SWT.CENTER);
		propositionColumn.getColumn().setAlignment(SWT.CENTER);
		propositionColumn.getColumn().setText("Proposition");
		propositionColumn.getColumn().setResizable(false);

		for (int j = 0; j < counterExample.getStates().size(); j++) {
			TreeViewerColumn column = new TreeViewerColumn(treeViewer,
					SWT.CENTER);
			column.getColumn().setAlignment(SWT.CENTER);
			column.getColumn().setText(
					"State " + counterExample.getStates().get(j).getState()
							+ " ("
							+ counterExample.getStates().get(j).getOperation()
							+ ")");

			column.getColumn().pack();
			column.getColumn().setResizable(false);
		}

		treeViewer.setLabelProvider(new CounterExampleTreeLabelProvider());
		treeViewer.setContentProvider(new CounterExampleContentProvider());

		// treeViewer.getTree().addMouseListener(
		// new CounterExampleMouseAdapter(treeViewer));

		treeViewer.getTree().addMouseMoveListener(
				new CounterExampleMouseMoveAdapter(treeViewer));

		// List<CounterExampleProposition> propositions = new
		// ArrayList<CounterExampleProposition>();
		// propositions.add(counterExample.getPropositionRoot());
		treeViewer.setInput(Arrays
				.asList(new CounterExampleProposition[] { counterExample
						.getPropositionRoot() }));
		treeViewer.expandAll();
		propositionColumn.getColumn().pack();

		return treeViewer;
	}

	private void createPropositionColumn(final TableViewer tableViewer,
			final TableColumnLayout layout,
			final CounterExampleProposition proposition) {
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new PropositionColumnLabelProvider(
				proposition));
		tableViewerColumn.getColumn().setText(proposition.toString());
		tableViewerColumn.getColumn().setAlignment(SWT.CENTER);
		layout.setColumnData(tableViewerColumn.getColumn(),
				new ColumnWeightData(1));
	}

	private void createEventColumn(final TableViewer tableViewer,
			final TableColumnLayout layout) {
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new EventColumnLabelProvider());
		tableViewerColumn.getColumn().setText("Event");
		tableViewerColumn.getColumn().setAlignment(SWT.CENTER);
		layout.setColumnData(tableViewerColumn.getColumn(),
				new ColumnWeightData(1));
	}
}