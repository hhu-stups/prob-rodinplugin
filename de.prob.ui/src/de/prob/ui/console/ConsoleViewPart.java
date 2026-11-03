package de.prob.ui.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.rodinp.keyboard.ui.RodinKeyboardUIPlugin;

import de.prob.core.Animator;
import de.prob.core.ILifecycleListener;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.exceptions.ProBException;
import de.prob.ui.ProbUiPlugin;
import de.prob.ui.StaticListenerRegistry;
import de.prob.ui.console.ConsoleEvaluator.Kind;
import de.prob.ui.console.ConsoleEvaluator.Result;
import de.prob.unicode.UnicodeTranslator;

/**
 * A console to evaluate predicates and expressions in the current state of
 * the animation. Enter evaluates the input, Shift+Enter starts a new line,
 * Up/Down browse the previous inputs and Tab or Ctrl+Space complete the
 * identifiers of the model and the keywords of Event-B.
 */
public class ConsoleViewPart extends ViewPart implements ILifecycleListener {

	public static final String VIEW_ID = "de.prob.ui.ConsoleView";

	private static final String PROMPT = "> ";
	// the font of the Rodin editors, which contains the mathematical symbols
	private static final String RODIN_FONT_KEY = "org.rodinp.keyboard.textFont";
	private static final String MATH_SYMBOLS_SETTING = "console.mathSymbols";
	// older output is removed, a StyledText becomes slow with huge contents
	private static final int MAX_OUTPUT_LENGTH = 500000;
	private static final int INPUT_ROWS = 3;

	private final ConsoleEvaluator evaluator = new ConsoleEvaluator();
	private final List<String> history = new ArrayList<String>();
	// position in the history while browsing it with Up/Down
	private int historyIndex = 0;
	// the input being evaluated, null if none
	private Job runningJob;

	private StyledText output;
	private Text input;
	private ContentProposalAdapter proposalAdapter;
	// translates ASCII into mathematical symbols while typing, as in the Rodin
	// editors; null if the Rodin keyboard is not available
	private ModifyListener mathSymbolsListener;

	@Override
	public void createPartControl(final Composite viewParent) {
		// Eclipse does not let a view limit how small it is made, so when
		// there is no room for the minimal output and the input the console
		// scrolls instead of clipping the input
		viewParent.setLayout(new FillLayout());
		final ScrolledComposite scroller = new ScrolledComposite(viewParent,
				SWT.V_SCROLL);
		scroller.setExpandHorizontal(true);
		scroller.setExpandVertical(true);
		final Composite parent = new Composite(scroller, SWT.NONE);
		scroller.setContent(parent);

		final GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);

		output = new StyledText(parent, SWT.MULTI | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL);
		output.setFont(getConsoleFont());
		final GridData outputData = new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 2);
		// at least two lines of output stays visible above the input
		outputData.minimumHeight = output.getLineHeight();
		// the preferred size of the console is its minimal size, the output
		// still grows with the view
		outputData.heightHint = outputData.minimumHeight;
		output.setLayoutData(outputData);
		output.setText("Evaluate predicates and expressions in the current animation state.\n"
				      + "Enter: evaluate, Shift+Enter: new line,\n"
				      + "Up/Down: previous inputs, Tab/Ctrl+Space: complete identifiers and keywords.\n");
		output.setStyleRange(new StyleRange(0, output.getCharCount(),
				getColor(SWT.COLOR_DARK_GRAY), null));

		final Label prompt = new Label(parent, SWT.NONE);
		prompt.setText(PROMPT.trim());
		prompt.setFont(getConsoleFont());
		prompt.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false,
				false));

		input = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL
				| SWT.BORDER);
		input.setFont(getConsoleFont());
		final GridData inputData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		inputData.heightHint = INPUT_ROWS * input.getLineHeight();
		input.setLayoutData(inputData);
		// before creating the proposal adapter, so that this listener sees
		// whether its popup is open (Enter then selects a proposal)
		input.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				if (proposalAdapter != null
						&& proposalAdapter.isProposalPopupOpen())
					return;
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					// Shift+Enter inserts a line break
					if ((e.stateMask & SWT.SHIFT) == 0) {
						e.doit = false;
						submit();
					}
				} else if (e.keyCode == SWT.TAB
						&& (e.stateMask & SWT.MODIFIER_MASK) == 0) {
					e.doit = false;
					complete();
				} else if (e.keyCode == SWT.ARROW_UP
						&& input.getCaretLineNumber() == 0) {
					// in a longer input, Up/Down move between its lines
					e.doit = false;
					browseHistory(-1);
				} else if (e.keyCode == SWT.ARROW_DOWN
						&& input.getCaretLineNumber() == input.getLineCount() - 1) {
					e.doit = false;
					browseHistory(1);
				}
			}
		});
		// Tab completes instead of moving the focus to the next control
		input.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT
						&& (e.stateMask & SWT.MODIFIER_MASK) == 0)
					e.doit = false;
			}
		});
		installContentProposals();
		redirectFocusToInput(parent, prompt);
		scroller.setMinHeight(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		final Action clear = new Action("Clear Console") {
			@Override
			public void run() {
				output.setText("");
			}
		};
		clear.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
		getViewSite().getActionBars().getToolBarManager().add(clear);
		installMathSymbols();

		StaticListenerRegistry.registerListener((ILifecycleListener) this);
	}

	/**
	 * Clicking into the console, or typing while the output has the focus,
	 * goes to the input line. Text in the output can still be selected and
	 * copied.
	 */
	private void redirectFocusToInput(final Composite parent,
			final Label prompt) {
		final Listener focusInput = new Listener() {
			@Override
			public void handleEvent(final Event e) {
				// not after selecting text with the mouse
				if (e.widget != output || output.getSelectionCount() == 0)
					focusInput();
			}
		};
		output.addListener(SWT.MouseUp, focusInput);
		prompt.addListener(SWT.MouseUp, focusInput);
		parent.addListener(SWT.MouseUp, focusInput);

		output.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				// keep shortcuts like Ctrl+C or Ctrl+A for the output
				final boolean shortcut = (e.stateMask & (SWT.MOD1 | SWT.MOD3 | SWT.MOD4)) != 0;
				if (!shortcut && e.character >= ' ' && e.character != SWT.DEL
						&& input.isEnabled()) {
					e.doit = false;
					focusInput();
					input.insert(String.valueOf(e.character));
				}
			}
		});
	}

	private void focusInput() {
		if (input.isEnabled()) {
			input.setFocus();
		}
	}

	private static Font getConsoleFont() {
		final FontRegistry fonts = JFaceResources.getFontRegistry();
		return fonts.hasValueFor(RODIN_FONT_KEY) ? fonts.get(RODIN_FONT_KEY)
				: JFaceResources.getTextFont();
	}

	/**
	 * Adds the toolbar toggle for translating ASCII into mathematical symbols
	 * while typing (e.g. ":" into the element-of symbol)
	 */
	private void installMathSymbols() {
		try {
			final RodinKeyboardUIPlugin keyboard = RodinKeyboardUIPlugin
					.getDefault();
			if (keyboard == null)
				return;
			mathSymbolsListener = keyboard.createRodinModifyListener();
		} catch (LinkageError e) {
			// the Rodin keyboard is an optional dependency, or an old version
			// without this method: no translation
			return;
		}
		final IDialogSettings settings = ProbUiPlugin.getDefault()
				.getDialogSettings();
		final boolean enabled = settings.get(MATH_SYMBOLS_SETTING) == null
				|| settings.getBoolean(MATH_SYMBOLS_SETTING);

		final Action toggle = new Action("\u2200", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setMathSymbols(isChecked());
				settings.put(MATH_SYMBOLS_SETTING, isChecked());
				focusInput();
			}
		};
		toggle.setToolTipText("Type Mathematical Symbols (translate ASCII as in the Rodin editors)");
		toggle.setChecked(enabled);
		setMathSymbols(enabled);
		getViewSite().getActionBars().getToolBarManager().add(toggle);
	}

	private void setMathSymbols(final boolean enabled) {
		// removing a listener that has not been added does nothing
		input.removeModifyListener(mathSymbolsListener);
		if (enabled) {
			input.addModifyListener(mathSymbolsListener);
		}
	}

	/**
	 * Tab completion: a unique identifier is completed directly, otherwise
	 * the common prefix of the candidates is inserted and if there is none
	 * the proposal popup opens.
	 */
	private void complete() {
		final IContentProposal[] proposals = new IdentifierProposals()
				.getProposals(input.getText(), input.getCaretPosition());
		if (proposals.length == 0)
			return;
		String common = proposals[0].getContent();
		for (IContentProposal proposal : proposals) {
			final String content = proposal.getContent();
			int i = 0;
			while (i < common.length() && i < content.length()
					&& common.charAt(i) == content.charAt(i))
				i++;
			common = common.substring(0, i);
		}
		if (!common.isEmpty())
			input.insert(common);
		else if (proposalAdapter != null)
			proposalAdapter.openProposalPopup();
	}

	private void installContentProposals() {
		try {
			proposalAdapter = new ContentProposalAdapter(input,
					new TextContentAdapter(), new IdentifierProposals(),
					KeyStroke.getInstance("Ctrl+Space"), null);
			proposalAdapter
					.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
		} catch (ParseException e) {
			// cannot happen for a constant key stroke; no completion then
		}
	}

	private void submit() {
		final String text = input.getText();
		if (text.trim().isEmpty() || runningJob != null)
			return;
		if (history.isEmpty() || !history.get(history.size() - 1).equals(text))
			history.add(text);
		historyIndex = history.size();
		input.setText("");
		evaluate(text);
	}

	private void browseHistory(final int direction) {
		if (history.isEmpty())
			return;
		historyIndex = Math.max(0,
				Math.min(history.size(), historyIndex + direction));
		input.setText(historyIndex < history.size() ? history.get(historyIndex)
				: "");
		input.setSelection(input.getText().length());
	}

	private void evaluate(final String text) {
		setContentDescription("Evaluating " + text + " ...");
		input.setEnabled(false);
		final Job job = new Job("Evaluating " + text) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				Result result;
				try {
					result = evaluator.evaluate(text);
				} catch (ProBException e) {
					result = new Result(Kind.ERROR, monitor.isCanceled() ? "Cancelled."
							: e.getMessage());
				} catch (RuntimeException e) {
					// e.g. an unexpected answer of ProB
					result = new Result(Kind.ERROR, e.toString());
				}
				showResult(this, text, result);
				return Status.OK_STATUS;
			}

			@Override
			protected void canceling() {
				// ProB may need long for some formulas
				Animator.getAnimator().sendUserInterruptSignal();
			}
		};
		runningJob = job;
		job.schedule();
	}

	private void showResult(final Job job, final String text,
			final Result result) {
		asyncExecIfAlive(new Runnable() {
			@Override
			public void run() {
				if (runningJob == job)
					runningJob = null;
				setContentDescription("");
				input.setEnabled(true);
				input.setFocus();
				append(PROMPT + UnicodeTranslator.toUnicode(text) + "\n",
						null, SWT.BOLD);
				append(result.getText() + "\n", getResultColor(result
						.getKind()), SWT.NORMAL);
			}
		});
	}

	private Color getResultColor(final Kind kind) {
		switch (kind) {
		case TRUE:
			return getColor(SWT.COLOR_DARK_GREEN);
		case FALSE:
		case ERROR:
			return getColor(SWT.COLOR_RED);
		case NOT_AVAILABLE:
			return getColor(SWT.COLOR_DARK_GRAY);
		case UNKNOWN:
			return getColor(SWT.COLOR_DARK_YELLOW);
		default:
			return null;
		}
	}

	private Color getColor(final int systemColor) {
		// system colors must not be disposed
		return output.getDisplay().getSystemColor(systemColor);
	}

	private void append(final String text, final Color color, final int style) {
		final int start = output.getCharCount();
		output.append(text);
		final StyleRange range = new StyleRange(start, text.length(), color,
				null, style);
		output.setStyleRange(range);
		if (output.getCharCount() > MAX_OUTPUT_LENGTH) {
			output.replaceTextRange(0, output.getCharCount()
					- MAX_OUTPUT_LENGTH, "");
		}
		output.setTopIndex(output.getLineCount() - 1);
	}

	private void asyncExecIfAlive(final Runnable runnable) {
		final Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (output != null && !output.isDisposed())
					runnable.run();
			}
		});
	}

	/**
	 * A new model has been loaded (called from any thread)
	 */
	@Override
	public void reset() {
		evaluator.reset();
		asyncExecIfAlive(new Runnable() {
			@Override
			public void run() {
				append("--- New model loaded ---\n",
						getColor(SWT.COLOR_DARK_GRAY), SWT.ITALIC);
			}
		});
	}

	@Override
	public void setFocus() {
		input.setFocus();
	}

	@Override
	public void dispose() {
		StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
		if (runningJob != null)
			runningJob.cancel();
		super.dispose();
	}

	/**
	 * Proposes the identifiers of the model that start with the word before
	 * the cursor
	 */
	private static final class IdentifierProposals implements
			IContentProposalProvider {
		// the keywords of Event-B's ASCII syntax
		private static final List<String> KEYWORDS = Arrays.asList("BOOL",
				"FALSE", "INT", "INTER", "NAT", "NAT1", "POW", "POW1", "TRUE",
				"UNION", "bool", "card", "dom", "finite", "id", "inter", "max",
				"min", "mod", "not", "oftype", "or", "partition", "pred",
				"prj1", "prj2", "ran", "succ", "union");

		@Override
		public IContentProposal[] getProposals(final String contents,
				final int position) {
			int start = position;
			while (start > 0
					&& isIdentifierChar(contents.charAt(start - 1)))
				start--;
			final String prefix = contents.substring(start, position);

			final Set<String> identifiers = new TreeSet<String>(KEYWORDS);
			final Animator animator = Animator.getAnimator();
			// getMachineDescription() would start probcli if none is running
			if (animator.isMachineLoaded()) {
				final MachineDescription md = animator
						.getMachineDescription();
				if (md != null)
					identifiers.addAll(md.getIdentifiers());
			}
			final List<IContentProposal> proposals = new ArrayList<IContentProposal>();
			for (String identifier : identifiers) {
				if (identifier.startsWith(prefix)) {
					// the prefix has already been typed
					proposals.add(new ContentProposal(identifier
							.substring(prefix.length()), identifier, null));
				}
			}
			return proposals.toArray(new IContentProposal[proposals.size()]);
		}

		private static boolean isIdentifierChar(final char c) {
			return Character.isLetterOrDigit(c) || c == '_';
		}
	}

}
