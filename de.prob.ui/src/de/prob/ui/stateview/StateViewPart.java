/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.LimitedLogger;
import de.prob.core.command.EvaluationGetTopLevelCommand;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.StateBasedViewPart;
import de.prob.ui.dnd.StaticStateElementTransfer;
import de.prob.ui.errorview.StateErrorView;
import de.prob.ui.stateview.statetree.StateTreeElement;
import de.prob.ui.stateview.statetree.StateTreeExpression;
import de.prob.ui.stateview.statetree.StateTreeExpressionSection;
import de.prob.ui.stateview.statetree.StateTreeSection;
import de.prob.ui.stateview.statetree.StateTreeVariable;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * This is the view that shows variables and formulas for the current state.
 *
 * @author plagge
 */
public class StateViewPart extends StateBasedViewPart {
	public static final String STATE_VIEW_ID = "de.prob.ui.StateView";

	private static final ShowMultipleVarsFilter DUP_FILTER = new ShowMultipleVarsFilter();

	private Composite pageComposite;

	private TreeViewer treeViewer;
	private LabelViewer invariantViewer;
	private LabelViewer stateErrorViewer;
	private LabelViewer timeoutViewer;

	private ShownState shownState;

	private LabelViewer modelchangeViewer;

	private Collection<EvaluationElement> topEvaluationElements;
	private StateTreeExpressionSection expressionSection;
	private final VarLabelProvider varLabelProvider = new VarLabelProvider();

	@Override
	public Control createStatePartControl(final Composite parent) {
		pageComposite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, true);
		pageComposite.setLayout(layout);
		createVariableTree();
		createSignalLabels();
		pageComposite.pack();

		hookContextMenu();

		getSite().setSelectionProvider(treeViewer);

		initDragAndDrop();

		initialiseFilter();

		return pageComposite;
	}

	private void initialiseFilter() {
		org.eclipse.core.commands.State filterState = ToggleShowDuplicatesHandler.getCurrentState(getSite());
		setDuplicateVariableFilter((Boolean) filterState.getValue());
	}

	private void hookContextMenu() {
		final StateViewPart x = this;
		TreeViewer viewer = treeViewer;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				x.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(final IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void initDragAndDrop() {
		Transfer[] transferTypes = new Transfer[] { StaticStateElementTransfer.getInstance(),
				TextTransfer.getInstance() };
		treeViewer.addDragSupport(DND.DROP_COPY, transferTypes, new DragSourceListener() {
			@Override
			public void dragStart(final DragSourceEvent event) {
				// System.out.println("dragStart");
			}

			@Override
			public void dragSetData(final DragSourceEvent event) {
				// System.out.println("dragSetData");
				final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				StaticStateElement[] elements = new StaticStateElement[selection.size()];
				int i = 0;
				for (Iterator<?> it = selection.iterator(); it.hasNext(); i++) {
					elements[i] = (StaticStateElement) it.next();
				}
				if (StaticStateElementTransfer.getInstance().isSupportedType(event.dataType)) {
					// System.out
					// .println("dragSetData: static state element");
					event.data = elements;
				} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					StringBuilder sb = new StringBuilder();
					boolean first = true;
					for (final StaticStateElement element : elements) {
						if (!first) {
							sb.append(", ");
							first = false;
						}
						sb.append(element.getLabel());
					}
					event.data = sb.toString();
					// System.out.println("dragSetData: text");
				}
			}

			@Override
			public void dragFinished(final DragSourceEvent event) {
				// System.out.println("dragFinished");
			}
		});
	}

	@Override
	protected void stateChanged(final State activeState, final Operation operation) {
		LimitedLogger.getLogger().log("state view: new state", activeState == null ? null : activeState.getId(), null);
		initShownState();
		final Animator animator = Animator.getAnimator();
		final State lastState = animator.getHistory().getState(-1);
		shownState.setCurrent(activeState);
		shownState.setLast(lastState);
		loadEvaluationElements(shownState, activeState, lastState);
		varLabelProvider.setStates(activeState, lastState);
		treeViewer.refresh(true);
		invariantViewer.setInput(activeState);
		stateErrorViewer.setInput(activeState);
		timeoutViewer.setInput(activeState);
		modelchangeViewer.setInput(activeState);
	}

	private void loadEvaluationElements(final ShownState shownState, final State current, final State last) {
		Set<EvaluationElement> visibleElements = new HashSet<EvaluationElement>();
		boolean errorShown = false;
		visibleElements.addAll(topEvaluationElements);
		try {
			for (final Object obj : treeViewer.getVisibleExpandedElements()) {
				if (obj instanceof StateTreeExpression) {
					StateTreeExpression ste = (StateTreeExpression) obj;
					EvaluationElement elem = ste.getStaticElement();
					visibleElements.add(elem);
					visibleElements.addAll(Arrays.asList(elem.getChildren()));
				}
			}
			EvaluationGetValuesCommand.getValuesForExpressionsCached(current, visibleElements);
			EvaluationGetValuesCommand.getValuesForExpressionsCached(last, visibleElements);
		} catch (ProBException e) {
			if (!errorShown) {
				e.notifyUserOnce();
			}
		}
	}

	@Override
	protected void stateReset() {
		shownState = null;
		varLabelProvider.setStates(null, null);
	}

	private void initShownState() {
		if (shownState == null) {
			try {
				EvaluationElement[] tops = EvaluationGetTopLevelCommand.retrieveTopLevelElements();
				topEvaluationElements = new ArrayList<EvaluationElement>(Arrays.asList(tops));
			} catch (ProBException e) {
				e.notifyUserOnce();
				topEvaluationElements = Collections.emptyList();
			}
			shownState = new ShownState();
			final MachineDescription md = Animator.getAnimator().getMachineDescription();
			shownState.setMachineDescription(md);
			final StateTreeElement[] topLevelElements = new StateTreeElement[shownState.getSections().size() + 1];
			int i = 0;
			for (final String section : shownState.getSections()) {
				topLevelElements[i] = new StateTreeSection(section, md);
				i++;
			}
			expressionSection = new StateTreeExpressionSection(StateViewStrings.formulasSectionLabel,
					topEvaluationElements);
			topLevelElements[i] = expressionSection;
			treeViewer.setInput(topLevelElements);
		}
	}

	private void createSignalLabels() {
		final GridData signalLayout = new GridData();
		signalLayout.horizontalAlignment = SWT.FILL;

		// define some colors and fonts
		final Display display = Display.getCurrent();
		final Color gray = display.getSystemColor(SWT.COLOR_GRAY);
		final Color orange = display.getSystemColor(SWT.COLOR_DARK_YELLOW);
		final Color green = display.getSystemColor(SWT.COLOR_GREEN);
		final Color red = display.getSystemColor(SWT.COLOR_RED);
		final Font bold = JFaceResources.getFontRegistry().getBold(JFaceResources.BANNER_FONT);

		// create the LabelViewer for the invariant
		invariantViewer = new LabelViewer(pageComposite, SWT.NONE);
		invariantViewer.getLabel().setLayoutData(signalLayout);
		invariantViewer.getLabel().setToolTipText(StateViewStrings.signalInvariantTooltip);
		final BooleanLabelProvider invProvider = new BooleanLabelProvider();
		invProvider.setTexts(null, StateViewStrings.signalInvariantGood, StateViewStrings.signalInvariantBad);
		invProvider.setBackgroundColors(gray, green, red);
		invProvider.setFonts(null, null, bold);
		invariantViewer.setLabelProvider(invProvider);
		invariantViewer.setContentProvider(new InvContentProvider());

		// create the LabelViewer for state-based errors
		stateErrorViewer = new LabelViewer(pageComposite, SWT.NONE);
		stateErrorViewer.getLabel().setLayoutData(signalLayout);
		stateErrorViewer.getLabel().setToolTipText(StateViewStrings.signalEventerrorTooltip);
		final BooleanLabelProvider errorProvider = new BooleanLabelProvider();
		errorProvider.setTexts(null, StateViewStrings.signalEventerrorGood, StateViewStrings.signalEventerrorBad);
		errorProvider.setBackgroundColors(gray, green, red);
		errorProvider.setFonts(null, null, null);
		stateErrorViewer.setLabelProvider(errorProvider);
		stateErrorViewer.setContentProvider(new StateErrorProvider());
		stateErrorViewer.addMouseListener(new ErrorViewDoubleClick());

		// create the LabelViewer for changed model
		modelchangeViewer = new LabelViewer(pageComposite, SWT.NONE);
		modelchangeViewer.getLabel().setLayoutData(signalLayout);
		modelchangeViewer.getLabel().setToolTipText(StateViewStrings.signalModelmodifiedTooltip);
		modelchangeViewer.addMouseListener(new ResetAnimationListener());

		final BooleanLabelProvider modelchangeProvider = new BooleanLabelProvider();
		// setTexts(final String inactive, final String ok, final String ko)
		modelchangeProvider.setTexts(null, StateViewStrings.signalModelhasRodinErrors, StateViewStrings.signalModelmodifiedBad);
		modelchangeProvider.setBackgroundColors(gray, orange, red);
		modelchangeProvider.setFonts(null, bold, bold);
		modelchangeProvider.hideWhenInactive(false);
		modelchangeViewer.setLabelProvider(modelchangeProvider);
		modelchangeViewer.setContentProvider(new ModelChangeContentProvider());

		// create the LabelViewer for the timeout
		timeoutViewer = new LabelViewer(pageComposite, SWT.NONE);
		timeoutViewer.getLabel().setLayoutData(signalLayout);
		timeoutViewer.getLabel().setToolTipText(StateViewStrings.signalTimeoutTooltip);

		final BooleanLabelProvider timeoutProvider = new BooleanLabelProvider();
		timeoutProvider.setTexts(null, StateViewStrings.signalTimeoutMaxReached, StateViewStrings.signalTimeoutBad);
		timeoutProvider.setBackgroundColors(gray, orange, red);
		timeoutProvider.setFonts(null, bold, bold);
		timeoutProvider.hideWhenInactive(false);
		timeoutViewer.setLabelProvider(timeoutProvider);
		timeoutViewer.setContentProvider(new TimeoutContentProvider());

	}

	private void createVariableTree() {
		final GridData treeViewerLayout = new GridData();
		treeViewerLayout.grabExcessHorizontalSpace = true;
		treeViewerLayout.grabExcessVerticalSpace = true;
		treeViewerLayout.horizontalAlignment = SWT.FILL;
		treeViewerLayout.verticalAlignment = SWT.FILL;
		treeViewerLayout.horizontalSpan = 2;

		treeViewer = new TreeViewer(pageComposite);
		treeViewer.getTree().setLayoutData(treeViewerLayout);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.setAutoExpandLevel(2);

		TreeViewerColumn col1 = new TreeViewerColumn(treeViewer, SWT.LEFT);
		col1.getColumn().setText(StateViewStrings.columnHeaderName);
		col1.getColumn().setResizable(true);
		col1.getColumn().setWidth(200);

		TreeViewerColumn col2 = new TreeViewerColumn(treeViewer, SWT.RIGHT);
		col2.getColumn().setText(StateViewStrings.columnHeaderCurrentvalue);
		col2.getColumn().setResizable(true);
		col2.getColumn().setWidth(150);

		TreeViewerColumn col3 = new TreeViewerColumn(treeViewer, SWT.RIGHT);
		col3.getColumn().setText(StateViewStrings.columnHeaderPreviousvalue);
		col3.getColumn().setResizable(true);
		col3.getColumn().setWidth(150);

		treeViewer.setContentProvider(new VarContentProvider());
		treeViewer.setLabelProvider(varLabelProvider);
		treeViewer.setInput(null);

		treeViewer.getTree().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				Font fontOfFirstColumn = item.getFont(1);
				FontData fd = fontOfFirstColumn.getFontData()[0];
				if ((fd.getStyle() & SWT.BOLD) != 0) {
					event.height = fd.getHeight() + 8;
				}
			}
		});
	}

	private static class InvContentProvider extends SimpleContentProvider {
		@Override
		public Object convert(final Object element) {
			final Boolean result;
			if (element != null && element instanceof State) {
				final State state = (State) element;
				result = state.isInitialized() ? Boolean.valueOf(state.isInvariantPreserved()) : null;
			} else {
				result = null;
			}
			return result;
		}
	}

	private static class StateErrorProvider extends SimpleContentProvider {
		@Override
		public Object convert(final Object element) {
			final Boolean result;
			if (element != null && element instanceof State) {
				final State state = (State) element;
				// check if constantsSetUp
				result = Boolean.valueOf(!state.hasStateBasedErrors());
			} else {
				result = null;
			}
			return result;
		}
	}

	private static class TimeoutContentProvider extends SimpleContentProvider {
		@Override
		public Object convert(final Object element) {
			final Boolean result;
			if (element != null && element instanceof State) {
				final State state = (State) element;
				if (state.isTimeoutOccured()) {
					result = Boolean.FALSE;
				} else {
					result = (state.isMaxOperationReached() ? Boolean.TRUE : null);
				}
			} else {
				result = null;
			}
			return result;
		}
	}

	private static class ModelChangeContentProvider extends SimpleContentProvider {
		@Override
		public Object convert(final Object element) {
		    if Animator.getAnimator().isDirty()
		      return false;  // ko string is signalModelmodifiedBad
		    else if Animator.getAnimator().isRodinProjectHasErrorsOrWarnings()
		      return true; // ok string is signalModelhasRodinErrors
		    else
		      return null; // leave box empty and gray
		}
	}

	private static class ResetAnimationListener implements MouseListener {
		@Override
		public void mouseDoubleClick(final MouseEvent event) {
			Animator animator = Animator.getAnimator();
			LanguageDependendAnimationPart ldp = animator.getLanguageDependendPart();
			if (ldp != null) {
				try {
					ldp.reload(animator);
				} catch (ProBException e) {
					e.notifyUserOnce();
				}
			}
		}

		@Override
		public void mouseDown(final MouseEvent arg0) {
		}

		@Override
		public void mouseUp(final MouseEvent arg0) {
		}

	}

	private static class ErrorViewDoubleClick implements MouseListener {
		@Override
		public void mouseDoubleClick(final MouseEvent event) {
			IWorkbenchPage wpage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				wpage.showView(StateErrorView.VIEWID);
			} catch (PartInitException e) {
				Logger.notifyUser(StateViewStrings.errorShowEventerrors, e);
			}
		}

		@Override
		public void mouseDown(final MouseEvent arg0) {
		}

		@Override
		public void mouseUp(final MouseEvent arg0) {
		}
	}

	public void setDuplicateVariableFilter(final boolean filterState) {
		final ViewerFilter[] filters = filterState ? new ViewerFilter[] { DUP_FILTER } : new ViewerFilter[0];
		treeViewer.setFilters(filters);
		refreshTreeView();
	}

	private void refreshTreeView() {
		final Runnable refresh = new Runnable() {
			@Override
			public void run() {
				treeViewer.refresh();
			}
		};
		Display.getDefault().asyncExec(refresh);
	}

	private static class ShowMultipleVarsFilter extends ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parent, final Object element) {
			if (element instanceof StateTreeSection)
				return ((StateTreeSection) element).isMainSectionOfVariable();
			else if (element instanceof StateTreeVariable)
				return ((StateTreeVariable) element).isInMainSection();
			else
				return true;
		}
	}

	public void addUserDefinedExpression(final EvaluationElement userDefinedElement) {
		this.expressionSection.addChild(userDefinedElement);
		this.topEvaluationElements.add(userDefinedElement);
		refreshTreeView();
	}
}
