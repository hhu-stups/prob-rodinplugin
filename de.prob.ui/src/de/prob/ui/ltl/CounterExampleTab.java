/**
 * 
 */
package de.prob.ui.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
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
import de.prob.ui.ltl.CounterExampleViewPart.ViewType;

/**
 * @author plagge
 * 
 */
public class CounterExampleTab {
	private final CounterExample counterExample;

	private final CTabItem tabItem;
	private final StackLayout layout;

	// Table view
	private final TableViewer tableViewer;

	// Tree view
	private final TreeViewer treeViewer;

	// Graphical view
	private final ScalableRootEditPart rootEditPart;
	private final GraphicalViewer graphicalViewer;
	private final Control interactiveView;

	// TODO remove it!
	private int currentIndex = -1;

	public CounterExampleTab(final CTabFolder tabFolder,
			final CounterExample counterExample) {
		this.counterExample = counterExample;

		tabItem = new CTabItem(tabFolder, SWT.CLOSE);
		tabItem.setText(counterExample.getPropositionRoot().toString());

		final Composite sashForm = new SashForm(tabFolder, SWT.HORIZONTAL);
		tabItem.setControl(sashForm);

		final Composite composite = new Composite(sashForm, SWT.None);

		layout = new StackLayout();
		composite.setLayout(layout);

		final Composite tableView = new Composite(composite, SWT.None);
		tableViewer = createTableViewer(tableView, counterExample);
		// createPopupMenu(tableViewer.getTable(), tableViewer);

		final Composite treeView = new Composite(composite, SWT.None);
		treeView.setLayout(new FillLayout());
		treeViewer = createTreeViewer(treeView, counterExample);
		// createPopupMenu(treeViewer.getTree(), treeViewer);

		rootEditPart = new ScalableRootEditPart();
		graphicalViewer = new ScrollingGraphicalViewer();

		interactiveView = graphicalViewer.createControl(composite);
		interactiveView.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		// createPopupMenu(interactiveView, graphicalViewer);

		graphicalViewer.setRootEditPart(rootEditPart);
		graphicalViewer.setEditPartFactory(new CounterExampleEditPartFactory());
		graphicalViewer.setContents(counterExample);

		EditDomain editDomain = new DefaultEditDomain(null);
		editDomain.addViewer(graphicalViewer);
	}

	public CTabItem getTabitem() {
		return tabItem;
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

	public void printCounterExample(final String title) {
		final PrintDialog dialog = new PrintDialog(graphicalViewer.getControl()
				.getShell(), SWT.NULL);

		try {
			final PrinterData printerData = dialog.open();
			if (printerData != null) {
				final PrintGraphicalViewerOperation viewerOperation = new PrintGraphicalViewerOperation(
						new Printer(printerData), graphicalViewer);

				viewerOperation.run(title);
			}
		} catch (SWTException e) {
			Logger.notifyUser("Failed to print the LTL counter example.", e);
		}
	}

	public void updateTopControl(final ViewType viewType) {
		final Control topControl;
		switch (viewType) {
		case TABLE:
			topControl = tableViewer.getControl();
			break;
		case TREE:
			topControl = treeViewer.getControl();
			break;
		case INTERACTIVE:
			topControl = interactiveView;
			break;
		default:
			throw new IllegalStateException(
					"Unexpected view type in LTL counter-example view");
		}

		layout.topControl = topControl;
		if (layout.topControl != null) {
			final Composite parent = layout.topControl.getParent();
			if (parent != null) {
				parent.layout();
			}
		}

	}

	public CounterExample getCounterExample() {
		return counterExample;
	}

	public void zoomIn() {
		final ZoomManager zoomManager = rootEditPart.getZoomManager();
		if (zoomManager != null) {
			if (zoomManager != null && zoomManager.canZoomIn()) {
				zoomManager.setZoom(zoomManager.getNextZoomLevel());
			}
		}
	}

	public void zoomOut() {
		final ZoomManager zoomManager = rootEditPart.getZoomManager();
		if (zoomManager != null) {
			if (zoomManager != null && zoomManager.canZoomOut()) {
				zoomManager.setZoom(zoomManager.getPreviousZoomLevel());
			}
		}
	}

	protected void stateChanged(final State activeState,
			final Operation operation) {
		if (activeState.isInitialized()) {
			final List<Operation> fullPath = counterExample.getFullPath();

			final Animator animator = Animator.getAnimator();
			final History history = animator.getHistory();

			if (isCounterExampleLoadedInHistory(counterExample)) {
				final int initPathSize = counterExample.getInitPath().size();

				currentIndex = history.getCurrentPosition() - initPathSize;

				// HistoryItem item = history.getCurrent();

				if (counterExample.getPathType() == PathType.INFINITE
						&& currentIndex == fullPath.size() - initPathSize) {
					currentIndex = counterExample.getLoopEntry();
				}
			}

			treeViewer.refresh();
			tableViewer.refresh();
			// We know that each element is of type
			// EditPart, but AbstractEditPart.getChildren() returns just
			// a list
			@SuppressWarnings("unchecked")
			List<EditPart> children = rootEditPart.getChildren();
			for (EditPart child : children) {
				child.refresh();
			}

			currentIndex = -1;
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

	public int getCurrentIndex() {
		return currentIndex;
	}

}
