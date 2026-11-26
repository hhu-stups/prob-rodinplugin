/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.LimitedLogger;
import de.prob.core.command.CommandException;
import de.prob.core.command.EvaluationGetTopLevelCommand;
import de.prob.core.command.EvaluationGetValuesCommand.EvaluationResult;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationInsertFormulaCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.EvaluationStateElement;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.StateError;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parserbase.ProBParseException;
import de.prob.ui.ProbUiPlugin;
import de.prob.ui.StateBasedViewPart;
import de.prob.ui.dnd.StaticStateElementTransfer;
import de.prob.ui.errorview.StateErrorView;
import de.prob.ui.eventb.ProjectProblems;
import de.prob.ui.graph.GraphViewPart;
import de.prob.ui.settings.ModelSettings;
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
	/** widget data excluding a widget from Eclipse's theme (CSS) styling */
	private static final String CSS_DISABLED = "org.eclipse.e4.ui.css.disabled";

	private static final ShowMultipleVarsFilter DUP_FILTER = new ShowMultipleVarsFilter();

	private Composite pageComposite;

	private TreeViewer treeViewer;
	/** the state shown, for the texts and menus of the labels below */
	private State currentState;
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
						}
						first = false;
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
		this.currentState = activeState;
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
				topEvaluationElements = new ArrayList<EvaluationElement>();
				for (EvaluationElement top : tops)
					// ProB keeps the formulas removed from the view
					if (!REMOVED.contains(String.valueOf(top.getId())))
						topEvaluationElements.add(top);
				restoreSavedFormulas();
			} catch (ProBException e) {
				e.notifyUserOnce();
				// mutable: user-defined formulas are added later
				topEvaluationElements = new ArrayList<EvaluationElement>();
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

		final Font bold = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

		// create the LabelViewer for the invariant
		invariantViewer = new LabelViewer(pageComposite, SWT.NONE);
		invariantViewer.getLabel().setLayoutData(signalLayout);
		invariantViewer.getLabel().setToolTipText(StateViewStrings.signalInvariantTooltip);
		final BooleanLabelProvider invProvider = new BooleanLabelProvider();
		invProvider.setTexts(null, StateViewStrings.signalInvariantGood, StateViewStrings.signalInvariantBad);
		invProvider.setFonts(null, null, bold);
		invariantViewer.setLabelProvider(invProvider);
		invariantViewer.setContentProvider(new InvContentProvider());
		invariantViewer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent event) {
				showInvariants();
			}
		});

		// create the LabelViewer for state-based errors
		stateErrorViewer = new LabelViewer(pageComposite, SWT.NONE);
		stateErrorViewer.getLabel().setLayoutData(signalLayout);
		stateErrorViewer.getLabel().setToolTipText(StateViewStrings.signalEventerrorTooltip);
		final BooleanLabelProvider errorProvider = new BooleanLabelProvider() {
			@Override
			public String getText(final Object element) {
				// e.g. "Event error in Inc: ..." instead of the fixed text
				return Boolean.FALSE.equals(element) && currentState != null ? eventErrorSummary(currentState
						.getStateBasedErrors()) : super.getText(element);
			}
		};
		errorProvider.setTexts(null, StateViewStrings.signalEventerrorGood, StateViewStrings.signalEventerrorBad);
		errorProvider.setFonts(null, null, bold);
		stateErrorViewer.setLabelProvider(errorProvider);
		stateErrorViewer.setContentProvider(new StateErrorProvider());
		stateErrorViewer.addMouseListener(new ErrorViewDoubleClick());

		// create the LabelViewer for changed model
		modelchangeViewer = new LabelViewer(pageComposite, SWT.NONE);
		modelchangeViewer.getLabel().setLayoutData(signalLayout);
		modelchangeViewer.getLabel().setToolTipText(StateViewStrings.signalModelmodifiedTooltip);
		modelchangeViewer.addMouseListener(new ResetAnimationListener());

		final BooleanLabelProvider modelchangeProvider = new BooleanLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Animator animator = Animator.getAnimator();
				// e.g. "Project has 2 errors!" instead of the fixed text
				return Boolean.TRUE.equals(element) ? ProjectProblems.summary(
						animator.getRodinProjectErrors(),
						animator.getRodinProjectWarnings()) : super.getText(element);
			}
		};
		// setTexts(final String inactive, final String ok, final String ko)
		modelchangeProvider.setTexts(null, StateViewStrings.signalModelhasRodinErrors, StateViewStrings.signalModelmodifiedBad);
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
		timeoutProvider.setFonts(null, bold, bold);
		timeoutProvider.hideWhenInactive(false);
		timeoutViewer.setLabelProvider(timeoutProvider);
		timeoutViewer.setContentProvider(new TimeoutContentProvider());

		// Eclipse's theme (CSS) engine restyles a view's widgets e.g. when it
		// is activated by a click, which would reset the labels' colours
		for (LabelViewer viewer : new LabelViewer[] { invariantViewer,
				stateErrorViewer, modelchangeViewer, timeoutViewer })
			viewer.getLabel().setData(CSS_DISABLED, Boolean.TRUE);

		// colours once Eclipse's theme has styled the view (dark or light)
		setStatusColors(invProvider, errorProvider, modelchangeProvider,
				timeoutProvider);
		pageComposite.getDisplay().asyncExec(() -> {
			if (pageComposite.isDisposed())
				return;
			setStatusColors(invProvider, errorProvider, modelchangeProvider,
					timeoutProvider);
			for (LabelViewer viewer : new LabelViewer[] { invariantViewer,
					stateErrorViewer, modelchangeViewer, timeoutViewer })
				viewer.refresh();
		});

		// right-click menus with details
		addMenu(invariantViewer, this::fillInvariantMenu);
		addMenu(stateErrorViewer, this::fillEventErrorMenu);
		addMenu(modelchangeViewer, this::fillModelMenu);
		addMenu(timeoutViewer, this::fillTimeoutMenu);

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
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (element instanceof StateTreeSection)
					openSection(((StateTreeSection) element).getLabel());
			}
		});

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

	/**
	 * Subtle tinted backgrounds with a darker text of the same hue, instead
	 * of saturated system colours; lighter text on darker tints for a dark
	 * theme. Inactive labels get the view's colours (the labels are not
	 * styled by the theme, see {@link #CSS_DISABLED}).
	 */
	private void setStatusColors(final BooleanLabelProvider invariant,
			final BooleanLabelProvider eventError,
			final BooleanLabelProvider model, final BooleanLabelProvider timeout) {
		final Color background = pageComposite.getBackground();
		final Color foreground = pageComposite.getForeground();
		final boolean dark = isDark(background);
		final Color okBg = color("ok.bg", dark, new RGB(30, 58, 36), new RGB(223, 240, 216));
		final Color okFg = color("ok.fg", dark, new RGB(165, 214, 167), new RGB(43, 84, 44));
		final Color warnBg = color("warning.bg", dark, new RGB(69, 57, 26), new RGB(252, 243, 207));
		final Color warnFg = color("warning.fg", dark, new RGB(232, 207, 134), new RGB(110, 80, 20));
		final Color errorBg = color("error.bg", dark, new RGB(78, 34, 36), new RGB(248, 222, 222));
		final Color errorFg = color("error.fg", dark, new RGB(242, 165, 165), new RGB(160, 40, 40));
		// icons as in Eclipse's Problems view, a check mark for OK
		final ISharedImages shared = PlatformUI.getWorkbench().getSharedImages();
		final Image error = shared.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		final Image warning = shared.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		final Image ok = ProbUiPlugin.getDefault().getImageRegistry()
				.get(ProbUiPlugin.IMG_STATUS_OK);
		invariant.setImages(null, ok, error);
		eventError.setImages(null, ok, error);
		model.setImages(null, warning, error);
		timeout.setImages(null, warning, error);
		invariant.setBackgroundColors(background, okBg, errorBg);
		invariant.setForegroundColors(foreground, okFg, errorFg);
		eventError.setBackgroundColors(background, okBg, errorBg);
		eventError.setForegroundColors(foreground, okFg, errorFg);
		model.setBackgroundColors(background, warnBg, errorBg);
		model.setForegroundColors(foreground, warnFg, errorFg);
		timeout.setBackgroundColors(background, warnBg, errorBg);
		timeout.setForegroundColors(foreground, warnFg, errorFg);
	}

	private static boolean isDark(final Color background) {
		final RGB rgb = background.getRGB();
		return 0.299 * rgb.red + 0.587 * rgb.green + 0.114 * rgb.blue < 128;
	}

	/**
	 * a colour of the status labels for a dark or light theme, disposed by
	 * JFace's colour registry
	 */
	private static Color color(final String name, final boolean dark,
			final RGB forDark, final RGB forLight) {
		final String key = "de.prob.ui.stateview.status." + name
				+ (dark ? ".dark" : ".light");
		final ColorRegistry registry = JFaceResources.getColorRegistry();
		if (!registry.hasValueFor(key))
			registry.put(key, dark ? forDark : forLight);
		return registry.get(key);
	}

	/** @return e.g. "Event error in Inc: ..." or "3 event errors in Inc, Dec!" */
	static String eventErrorSummary(final Collection<StateError> errors) {
		if (errors.size() == 1) {
			final StateError error = errors.iterator().next();
			String description = error.getShortDescription().replaceAll("\\s+", " ");
			if (description.length() > 60)
				description = description.substring(0, 60) + "\u2026";
			return "Event error in " + error.getEvent() + ": " + description;
		}
		final Set<String> events = new LinkedHashSet<String>();
		for (StateError error : errors)
			events.add(error.getEvent());
		final StringBuilder result = new StringBuilder();
		result.append(errors.size()).append(" event errors in ");
		int i = 0;
		for (String event : events) {
			if (i == 3) {
				result.append(", \u2026");
				break;
			}
			result.append(i++ == 0 ? "" : ", ").append(event);
		}
		return result.append('!').toString();
	}

	/** a context menu for a label, filled when it is opened */
	private static void addMenu(final LabelViewer viewer,
			final java.util.function.Consumer<Menu> fill) {
		final CLabel label = viewer.getLabel();
		final Menu menu = new Menu(label);
		label.setMenu(menu);
		menu.addListener(SWT.Show, event -> {
			for (MenuItem item : menu.getItems())
				item.dispose();
			fill.accept(menu);
		});
	}

	private static MenuItem menuItem(final Menu menu, final String text,
			final boolean enabled, final Runnable action) {
		final MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(text);
		item.setEnabled(enabled);
		item.addListener(SWT.Selection, event -> action.run());
		return item;
	}

	private boolean isInitialised() {
		return currentState != null && currentState.isInitialized();
	}

	private void fillInvariantMenu(final Menu menu) {
		final boolean violated = isInitialised()
				&& !currentState.isInvariantPreserved();
		menuItem(menu, violated ? "Show Violated Invariant as Graph"
				: "Show Invariant as Graph", isInitialised(),
				() -> showGraph(GraphViewPart.INVARIANT_GRAPH));
		menuItem(menu, "Show Invariants in State View", true,
				this::showInvariants);
		menuItem(menu, "Show Details", currentState != null, () -> {
			if (!isInitialised())
				Logger.notifyUserNonModal("The machine is not initialised yet:"
						+ " the invariant is checked once it is.", null);
			else if (violated)
				showViolatedInvariants(currentState);
			else
				Logger.notifyUserNonModal("All invariants are true in the current state.", null);
		});
	}

	/**
	 * Notifies the user of the invariants that are false (or cannot be
	 * evaluated) in the state; computed by ProB in the background.
	 */
	private static void showViolatedInvariants(final State state) {
		final Job job = new Job("ProB: violated invariants") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final List<String> violated = new ArrayList<String>();
				final List<String> failed = new ArrayList<String>();
				String error = null;
				try {
					EvaluationElement invariant = null;
					for (EvaluationElement top : EvaluationGetTopLevelCommand
							.retrieveTopLevelElements())
						// ProB's node for the invariant, its children the
						// single invariants
						if (top.getId().hasFunctor("inv", 0))
							invariant = top;
					if (invariant == null)
						error = "ProB did not provide the invariants of the model.";
					else
						for (EvaluationStateElement child : new EvaluationStateElement(
								invariant, state, null).getChildren()) {
							final EvaluationResult result = child.getResult();
							final String label = child.getElement().getLabel();
							if (result == null)
								continue;
							if (result.hasError())
								failed.add(label + "\n      " + result.getText());
							else if (result.isPredicate()
									&& !result.isPredicateTrue())
								violated.add(label);
						}
				} catch (ProBException e) {
					error = "ProB could not evaluate the invariants: "
							+ e.getMessage();
				}
				final StringBuilder details = new StringBuilder();
				if (!violated.isEmpty()) {
					details.append("False (").append(violated.size())
							.append("):\n");
					for (String inv : violated)
						details.append("  ").append(inv).append('\n');
				}
				if (!failed.isEmpty()) {
					details.append(details.length() > 0 ? "\n" : "")
							.append("Could not be evaluated (")
							.append(failed.size()).append("):\n");
					for (String inv : failed)
						details.append("  ").append(inv).append('\n');
				}
				if (error != null)
					details.append(error).append('\n');
				details.append("\nRight-click the label and choose \"Show Violated Invariant as Graph\""
						+ " to see which parts are false (red) in the Invariant Formula Tree.");
				final String headline = violated.isEmpty() ? "The invariant is violated in the current state."
						: violated.size() == 1 ? "1 invariant is false in the current state: "
								+ shorten(violated.get(0), 80)
								: violated.size() + " invariants are false in the current state.";
				Logger.notifyUserNonModal(headline, details.toString(),
						"Show Violated Invariant as Graph", () -> {
							try {
								final GraphViewPart view = (GraphViewPart) PlatformUI
										.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage().showView(GraphViewPart.VIEW_ID);
								view.showGraph(GraphViewPart.INVARIANT_GRAPH);
							} catch (PartInitException e) {
								Logger.notifyUser("Could not open the ProB Graph view", e);
							}
						});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	private static String shorten(final String text, final int max) {
		final String line = text.replaceAll("\\s+", " ");
		return line.length() <= max ? line : line.substring(0, max) + "\u2026";
	}

	/**
	 * Expands the invariants in the State view's formula tree (in the
	 * Formulas section) and scrolls to them.
	 */
	private void showInvariants() {
		if (expressionSection == null || treeViewer == null
				|| treeViewer.getTree().isDisposed())
			return;
		for (StaticStateElement child : expressionSection.getChildren())
			if (child instanceof StateTreeExpression
					&& ((StateTreeExpression) child).getStaticElement().getId()
							.hasFunctor("inv", 0)) {
				treeViewer.expandToLevel(expressionSection, 1);
				treeViewer.expandToLevel(child, 1);
				treeViewer.setSelection(new StructuredSelection(child), true);
				// show the invariants below it as well
				final TreeItem[] selected = treeViewer.getTree().getSelection();
				if (selected.length == 1)
					treeViewer.getTree().setTopItem(selected[0]);
				treeViewer.getTree().setFocus();
				return;
			}
		Logger.notifyUserNonModal(
				"The State view has no invariants entry, e.g. because it was removed.",
				null);
	}

	private void fillEventErrorMenu(final Menu menu) {
		final boolean errors = currentState != null
				&& currentState.hasStateBasedErrors();
		menuItem(menu, "Show Event Errors", errors, this::showEventErrors);
		menuItem(menu, "Show Error Source as Graph", errors,
				() -> showGraph(GraphViewPart.LAST_ERROR_GRAPH));
	}

	private void fillModelMenu(final Menu menu) {
		final Animator animator = Animator.getAnimator();
		final ProjectProblems problems = animatedProjectProblems();
		menuItem(menu, "Show Errors and Warnings", problems != null
				&& animator.isRodinProjectHasErrorsOrWarnings(),
				() -> problems.notifyUser());
		menuItem(menu, "Show Problems View", true,
				ProjectProblems::showProblemsView);
		new MenuItem(menu, SWT.SEPARATOR);
		menuItem(menu, "Reload Animation", animator.isMachineLoaded(),
				() -> ResetAnimationListener.reload());
	}

	private void fillTimeoutMenu(final Menu menu) {
		final State state = currentState;
		menuItem(menu, "Show Details", state != null
				&& (state.isTimeoutOccured() || state.isMaxOperationReached()), () -> {
			final StringBuilder details = new StringBuilder();
			if (state.isTimeoutOccured()) {
				final Set<String> ops = state.getTimeoutOperations();
				details.append(ops.isEmpty() ? "A timeout occurred when computing the enabled events."
						: "A timeout occurred when computing the enabled events (the parameters"
								+ " satisfying the guard) of: " + String.join(", ", ops) + ".");
				details.append(" Not all enabled events may be shown. You can increase the timeout (TIME_OUT)"
						+ " in the ProB preferences.");
			}
			if (state.isMaxOperationReached()) {
				if (details.length() > 0)
					details.append('\n');
				details.append("The maximum number of enabled events (parameter combinations)"
						+ " shown per event was reached, so not all of them may be shown."
						+ " You can change it (MAX_OPERATIONS: Max Number of Enablings per Operation Computed)"
						+ " in the ProB preferences.");
			}
			Logger.notifyUserNonModal(details.toString(), null);
		});
	}

	private void showGraph(final String command) {
		try {
			final GraphViewPart view = (GraphViewPart) getSite().getPage()
					.showView(GraphViewPart.VIEW_ID);
			view.showGraph(command);
		} catch (PartInitException e) {
			Logger.notifyUser("Could not open the ProB Graph view", e);
		}
	}

	private void showEventErrors() {
		try {
			final IViewPart view = getSite().getPage().showView(
					StateErrorView.VIEWID);
			if (view instanceof StateErrorView)
				((StateErrorView) view).showAll();
		} catch (PartInitException e) {
			Logger.notifyUser(StateViewStrings.errorShowEventerrors, e);
		}
	}

	/** @return the problems of the animated project, or null */
	private static ProjectProblems animatedProjectProblems() {
		final LanguageDependendAnimationPart ldp = Animator.getAnimator()
				.getLanguageDependendPart();
		return ldp instanceof EventBAnimatorPart ? ProjectProblems
				.of(((EventBAnimatorPart) ldp).getRoot().getRodinProject()
						.getProject()) : null;
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
		    if (Animator.getAnimator().isDirty())
		      return false;  // ko string is signalModelmodifiedBad
		    else if (Animator.getAnimator().isRodinProjectHasErrorsOrWarnings())
		      return true; // ok string is signalModelhasRodinErrors
		    else
		      return null; // leave box empty and gray
		}
	}

	/**
	 * Opens the machine or context with the name of a section in its default
	 * editor (e.g. Rodin's editor or Camille).
	 */
	private void openSection(final String name) {
		final LanguageDependendAnimationPart ldp = Animator.getAnimator().getLanguageDependendPart();
		if (!(ldp instanceof EventBAnimatorPart))
			return;
		final IEventBProject project = ((EventBAnimatorPart) ldp).getRoot().getEventBProject();
		final IEventBRoot root = isContext(name) ? project.getContextRoot(name) : project.getMachineRoot(name);
		if (!root.exists())
			return;
		try {
			IDE.openEditor(getSite().getPage(), root.getRodinFile().getResource());
		} catch (PartInitException e) {
			Logger.notifyUser("Could not open " + name, e);
		}
	}

	private static boolean isContext(final String section) {
		final MachineDescription md = Animator.getAnimator().getMachineDescription();
		if (md != null)
			for (final MachineDescription.Section s : md.getSections())
				if (s.getName().equals(section))
					return s.getType() == MachineDescription.SectionType.CONTEXT;
		return false;
	}

	/**
	 * A double-click reloads the animation (e.g. after the model was
	 * modified); a single click shows the errors and warnings of the project.
	 */
	/**
	 * Loads the animated model again (e.g. after it was changed), like a
	 * double-click on the State view's model label; the project's errors and
	 * warnings are counted again first, as they may have been fixed. Can be
	 * called from any thread.
	 * 
	 * @return false if no model is animated
	 */
	public static boolean reloadModel() throws ProBException {
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (ldp == null)
			return false;
		if (ldp instanceof EventBAnimatorPart)
			// the errors may have been fixed
			ProjectProblems.of(((EventBAnimatorPart) ldp).getRoot()
					.getRodinProject().getProject()).storeInAnimator();
		ldp.reload(animator);
		return true;
	}

	private static class ResetAnimationListener implements MouseListener {
		/** increased by a double-click, cancelling a pending single click */
		private int clicks;

		@Override
		public void mouseDoubleClick(final MouseEvent event) {
			clicks++;
			reload();
		}

		static void reload() {
			try {
				reloadModel();
			} catch (ProBException e) {
				e.notifyUserOnce();
			}
		}

		@Override
		public void mouseDown(final MouseEvent arg0) {
		}

		@Override
		public void mouseUp(final MouseEvent event) {
			if (event.button != 1 || event.count != 1
					|| !Animator.getAnimator().isRodinProjectHasErrorsOrWarnings())
				return;
			// wait whether it becomes a double-click (reload)
			final int before = ++clicks;
			event.display.timerExec(event.display.getDoubleClickTime(), () -> {
				if (clicks != before)
					return;
				final ProjectProblems problems = animatedProjectProblems();
				if (problems != null)
					problems.notifyUser();
			});
		}

	}

	private static class ErrorViewDoubleClick implements MouseListener {
		@Override
		public void mouseDoubleClick(final MouseEvent event) {
			IWorkbenchPage wpage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				final IViewPart view = wpage.showView(StateErrorView.VIEWID);
				if (view instanceof StateErrorView)
					((StateErrorView) view).showAll();
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
				if (treeViewer != null
						&& !treeViewer.getControl().isDisposed())
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

	/**
	 * The formulas the user added (by the id of their element in ProB), as
	 * entered: they are saved in the model's settings file (see
	 * {@link ModelSettings}) and can be removed again.
	 */
	private static final Map<String, String> USER_FORMULAS = new ConcurrentHashMap<String, String>();
	/** the ids of user formulas removed from the view (ProB keeps them) */
	private static final Set<String> REMOVED = ConcurrentHashMap.newKeySet();
	/** the model load the saved formulas were added to ProB for */
	private MachineDescription restoredFor;

	/** @return whether the element is a formula the user added */
	public static boolean isUserFormula(final EvaluationElement element) {
		return element != null && element.getId() != null
				&& USER_FORMULAS.containsKey(element.getId().toString());
	}

	/**
	 * Adds a formula the user entered and saves it in the model's settings
	 * file, so that it is added again when the model is animated again.
	 */
	public void addUserFormula(final EvaluationElement element,
			final String formula) {
		USER_FORMULAS.put(element.getId().toString(), formula);
		addUserDefinedExpression(element);
		final IEventBRoot model = ModelSettings.animatedModel();
		if (model == null)
			return;
		try {
			ModelSettings.add(model, ModelSettings.Kind.STATE, formula, null);
		} catch (CoreException e) {
			Logger.notifyUserNonModal("The formula " + formula
					+ " could not be saved for " + model.getComponentName()
					+ ".", e.getMessage());
		}
	}

	/**
	 * Removes a formula the user added from the view and from the model's
	 * settings file.
	 */
	public void removeUserFormula(final EvaluationElement element) {
		final String id = element.getId().toString();
		final String formula = USER_FORMULAS.remove(id);
		if (formula == null)
			return;
		REMOVED.add(id);
		if (expressionSection != null)
			expressionSection.removeChild(element);
		if (topEvaluationElements != null)
			for (Iterator<EvaluationElement> it = topEvaluationElements
					.iterator(); it.hasNext();)
				if (id.equals(String.valueOf(it.next().getId())))
					it.remove();
		refreshTreeView();
		final IEventBRoot model = ModelSettings.animatedModel();
		if (model == null)
			return;
		try {
			ModelSettings.remove(model, ModelSettings.Kind.STATE, formula, null);
		} catch (CoreException e) {
			Logger.notifyUserNonModal("The formula " + formula
					+ " could not be removed from the settings of "
					+ model.getComponentName() + ".", e.getMessage());
		}
	}

	/**
	 * Adds the formulas saved for the model to ProB, once per load of a
	 * model (ProB forgets them when a model is loaded).
	 */
	private void restoreSavedFormulas() {
		final MachineDescription md = Animator.getAnimator()
				.getMachineDescription();
		if (md == null || md == restoredFor)
			return;
		restoredFor = md;
		USER_FORMULAS.clear();
		REMOVED.clear();
		final IEventBRoot model = ModelSettings.animatedModel();
		if (model == null)
			return;
		final StringBuilder failed = new StringBuilder();
		int failures = 0;
		for (final ModelSettings.SavedFormula saved : ModelSettings.formulas(
				model, ModelSettings.Kind.STATE)) {
			try {
				final EvaluationElement element = Animator
						.callQuietly(new Animator.ProBCall<EvaluationElement>() {
							@Override
							public EvaluationElement call() throws ProBException {
								return insert(saved.formula);
							}
						});
				USER_FORMULAS.put(element.getId().toString(), saved.formula);
				topEvaluationElements.add(element);
			} catch (ProBException e) {
				failures++;
				failed.append(saved.formula).append(": ")
						.append(e.getMessage()).append('\n');
			}
		}
		if (failures > 0)
			Logger.notifyUserNonModal(
					(failures == 1 ? "A saved formula" : failures
							+ " saved formulas")
							+ " of "
							+ model.getComponentName()
							+ " could not be added to the State view, e.g. because the model has changed.",
					failed
							+ "\nRemove "
							+ (failures == 1 ? "it" : "them")
							+ " from "
							+ ModelSettings.file(model).getFullPath()
							+ ", or add the corrected formula again.");
	}

	/** inserts a formula, a predicate or else an expression, into ProB */
	private static EvaluationElement insert(final String formula)
			throws ProBException {
		final Animator animator = Animator.getAnimator();
		try {
			return EvaluationInsertFormulaCommand.insertFormula(animator,
					EvaluationInsertFormulaCommand.FormulaType.PREDICATE,
					formula);
		} catch (ProBParseException e) {
			try {
				return EvaluationInsertFormulaCommand.insertFormula(animator,
						EvaluationInsertFormulaCommand.FormulaType.EXPRESSION,
						formula);
			} catch (ProBParseException e2) {
				throw new CommandException("syntax error: " + e2.getMessage());
			}
		}
	}

	public void addUserDefinedExpression(final EvaluationElement userDefinedElement) {
		if (shownState == null) {
			// no state shown yet; the top-level elements retrieved from ProB
			// may already contain the new formula
			initShownState();
		}
		if (!topEvaluationElements.contains(userDefinedElement)) {
			this.expressionSection.addChild(userDefinedElement);
			this.topEvaluationElements.add(userDefinedElement);
		}
		refreshTreeView();
	}
}
