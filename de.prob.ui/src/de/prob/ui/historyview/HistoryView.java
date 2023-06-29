/**
 * 
 */
package de.prob.ui.historyview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryItem;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.StateBasedViewPart;
import de.prob.ui.dnd.StaticStateElementTransfer;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * The history view shows the current history of the animator. This includes the
 * name of executed events and optional expressions on the state.
 * 
 * @author plagge
 */
public class HistoryView extends StateBasedViewPart {
	public static final String VIEW_ID = "de.prob.ui.HistoryView";

	private TableViewer tableViewer;
	private Collection<HistoryLabelProvider> labelProviders;

	@Override
	protected Control createStatePartControl(final Composite parent) {
		Composite tableComposite = new Composite(parent, SWT.NONE);
		tableViewer = new TableViewer(tableComposite, SWT.NONE);
		tableViewer.setContentProvider(new HistContentProvider());
		// tableViewer.setLabelProvider(new HistoryLabelProviderOld());
		tableViewer.addDoubleClickListener(new HistDoubleClickListener());
		createColumns(tableComposite);
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		setPaintListener(table);
		initDragAndDrop();
		return tableComposite;
	}

	/**
	 * Adds a listener that does some custom painting of the cells. In
	 * particular, a line is drawn on top of the active history item. This
	 * should have the effect that a line is shown after the last executed
	 * transition, indicating the currently shown state.
	 * 
	 * @param table
	 */
	private void setPaintListener(final Table table) {
		// The color of the drawn line
		final Color activecolor = Display.getDefault().getSystemColor(
				SWT.COLOR_BLUE);
		final Color samecolor = Display.getDefault().getSystemColor(
				SWT.COLOR_GRAY);
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// The corresponding item for the shown row
				final HistViewItem item = (HistViewItem) event.item.getData();
				if (item != null) {
					if (item.followingStateIsActive()) {
						drawLine(table, event, true, activecolor);
					} else if (item.followingStateIsSameAsCurrent()) {
						drawLine(table, event, true, samecolor);
					}
					if (item.previousStateIsActive()) {
						drawLine(table, event, false, activecolor);
					} else if (item.previousStateIsSameAsCurrent()) {
						drawLine(table, event, false, samecolor);
					}
				}
			}

			private void drawLine(final Table table, Event event,
					final boolean onTop, final Color color) {
				final int clientWidth = table.getClientArea().width;
				final GC gc = event.gc;
				final int y = onTop ? event.y : event.y + event.height - 1;
				// A rectangle is painted with the background color
				gc.setBackground(color);
				gc.fillRectangle(event.x, y, clientWidth, 1);
			}
		});
	}

	private void initDragAndDrop() {
		Transfer[] transferTypes = new Transfer[] { StaticStateElementTransfer
				.getInstance() };
		DropTargetListener dropListener = new ViewerDropAdapter(tableViewer) {
			@Override
			public boolean validateDrop(final Object target, final int type,
					final TransferData transferData) {
				final boolean isSupported = StaticStateElementTransfer
						.getInstance().isSupportedType(transferData);
				// System.out.println("validateDrop: is "
				// + (isSupported ? "" : "not ") + "supported, type="
				// + type);
				return isSupported;
			}

			@Override
			public boolean performDrop(final Object data) {
				// System.out.println("peformDrop");
				if (data != null && data instanceof StaticStateElement[])
					return true;
				else
					return false;
			}
		};
		tableViewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE,
				transferTypes, dropListener);
	}

	public void addColumns(final Collection<StaticStateElement> elements) {
		final Runnable update = new Runnable() {
			public void run() {
				final Composite parent = tableViewer.getTable().getParent();
				final TableColumnLayout layout = (TableColumnLayout) parent
						.getLayout();
				for (final StaticStateElement element : elements) {
					createColumn(layout, element.getLabel(),
							new HistoryElementLabelProvider(element), false);
				}
				parent.layout();
				parent.redraw();
				tableViewer.refresh();
			}
		};
		Display.getDefault().asyncExec(update);
	}

	private void createColumns(final Composite composite) {
		final Animator animator = Animator.getAnimator();
		MachineDescription machineDescription = animator
				.getMachineDescription();
		List<String> models;
		if (machineDescription != null) {
			models = new ArrayList<>(machineDescription.getModelNames());
			Collections.reverse(models);
		} else {
			models = Collections.emptyList();
		}
		final TableColumnLayout layout = new TableColumnLayout();
		composite.setLayout(layout);
		labelProviders = new ArrayList<HistoryLabelProvider>();
		if (!models.isEmpty()) {
			int pos = 0;
			for (final String model : models) {
				final boolean isFirst = pos == 0;
				final HistoryEventLabelProvider labelProvider = new HistoryEventLabelProvider(
						pos);
				createColumn(layout, model, labelProvider, isFirst);
				pos++;
			}
		} else {
			createColumn(layout, "Event", new HistoryEventLabelProvider(0),
					false);
		}
	}

	private void createColumn(final TableColumnLayout layout,
			final String header, final HistoryLabelProvider labelProvider,
			final boolean setMinimumSize) {
		final TableViewerColumn tvc = new TableViewerColumn(tableViewer,
				SWT.NONE);
		tvc.setLabelProvider(labelProvider);
		final TableColumn column = tvc.getColumn();
		column.setText(header);
		column.setResizable(true);
		column.setMoveable(true);
		final ColumnWeightData weightData = setMinimumSize ? new ColumnWeightData(
				1, 100) : new ColumnWeightData(1);
		layout.setColumnData(column, weightData);
		labelProviders.add(labelProvider);
	}

	@Override
	protected void stateChanged(final State currentState,
			final Operation operation) {
		final Animator animator = Animator.getAnimator();
		final History history = animator.getHistory();
		final HistoryItem[] items = history.getAllItems();
		final int size = items.length;
		final HistViewItem[] vItems = new HistViewItem[size];
		final HistViewItem current;
		if (size > 0) {
			final int activeItem = history.getCurrentPosition();
			final State cs = items[activeItem].getState();
			final State rootState = items[0].getState();
			vItems[0] = new HistViewItem(0, activeItem, rootState, null, cs);
			for (int i = 1; i < size; i++) {
				final State state = items[i].getState();
				final Operation op = items[i - 1].getOperation();
				vItems[i] = new HistViewItem(i, activeItem, state, op, cs);
			}
			current = vItems[activeItem];
			Collections.reverse(Arrays.asList(vItems));
		} else {
			current = null;
		}
		tableViewer.setInput(vItems);
		tableViewer.refresh();
		if (current != null) {
			tableViewer.reveal(current);
		}
	}

	static class HistViewItem {
		private final int historyPosition;
		private final int activePosition;
		private final State dstState;
		private final Operation operation;
		private final boolean previousStateIsSameAsCurrent;
		private final boolean followingStateIsSameAsCurrent;

		public HistViewItem(final int historyPosition,
				final int activePosition, final State dstState,
				final Operation operation, final State currentState) {
			this.historyPosition = historyPosition;
			this.activePosition = activePosition;
			this.dstState = dstState;
			this.operation = operation;
			// The item representing the root state has no operation leading to
			// it, so we have to check if it is null
			this.previousStateIsSameAsCurrent = operation != null
					&& Objects.equals(currentState.getId(), operation.getSource());
			this.followingStateIsSameAsCurrent = Objects.equals(currentState, dstState);
		}

		public State getDestination() {
			return dstState;
		}

		public Operation getOperation() {
			return operation;
		}

		public boolean followingStateIsActive() {
			return activePosition == historyPosition;
		}

		public boolean previousStateIsActive() {
			return activePosition == historyPosition - 1;
		}

		public boolean followingStateIsSameAsCurrent() {
			return followingStateIsSameAsCurrent;
		}

		public boolean previousStateIsSameAsCurrent() {
			return previousStateIsSameAsCurrent;
		}

		public void jumpToState() throws ProBException {
			final History history = Animator.getAnimator().getHistory();
			history.gotoPos(historyPosition);
		}
	}

	private static class HistContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(final Object data) {
			return data == null ? new HistViewItem[0] : (HistViewItem[]) data;
		}

		public void dispose() {
		}

		public void inputChanged(final Viewer arg0, final Object arg1,
				final Object arg2) {
		}
	}

	private static class HistDoubleClickListener implements
			IDoubleClickListener {
		private static final String EXCEPTION_MSG = "exception raised while trying to jump to state";

		public void doubleClick(final DoubleClickEvent event) {
			IStructuredSelection sel = (IStructuredSelection) event
					.getSelection();
			if (sel != null && !sel.isEmpty()) {
				HistViewItem item = (HistViewItem) sel.getFirstElement();
				try {
					item.jumpToState();
				} catch (ProBException e) {
					Logger.notifyUser(EXCEPTION_MSG, e);
				}
			}
		}
	}

	public void setShowParameters(boolean show) {
		if (labelProviders != null) {
			for (HistoryLabelProvider provider : labelProviders) {
				provider.setShowParameters(show);
			}
		}
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				tableViewer.refresh();
			}
		};
		Display.getDefault().asyncExec(runnable);
	}

}
