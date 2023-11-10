/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.ltl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.be4.ltl.core.parser.LtlParseException;
import de.be4.ltl.core.parser.LtlParser;
import de.prob.core.Animator;
import de.prob.core.command.CommandException;
import de.prob.core.command.LtlCheckingCommand;
import de.prob.core.command.SymmetryReductionOption;
import de.prob.logging.Logger;
import de.prob.prolog.term.PrologTerm;
import de.prob.ui.DialogHelpers;

public final class LtlCheckingDialog extends TrayDialog {
	private static final String CONFIGURATION_ID = "de.prob.ui.ltl.CounterExampleViewPart";

	private static final StartOption[] START_MODES = new StartOption[] {
			new StartOption(LtlCheckingCommand.StartMode.init,
					"Start in the (possible several) initialisation states of the model"),
			new StartOption(LtlCheckingCommand.StartMode.starthere,
					"Start the evaluation of the formula in the current state"),
			new StartOption(
					LtlCheckingCommand.StartMode.checkhere,
					"Start the search as in init, but check the formula F in the current state by constructing a new formula (current -> F)") };

	private final Shell shell;
	private boolean trayOpened = false;

	private Combo formulas = null;
	private Combo startingPointOptions = null;
	private Combo symmetryOptions = null;

	public LtlCheckingDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("LTL Checking");
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		formulas = createFormulas(dialogArea);
		startingPointOptions = createStartingPointOptions(dialogArea);
		symmetryOptions = createSymmetryOptions(dialogArea);

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		Button startButton = new Button(composite, SWT.PUSH);
		startButton.setText("Start LTL Checking");
		startButton.addSelectionListener(new StartButtonSelectionListener());

		createButton(composite, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

		return dialogArea;
	}

	@Override
	protected Control createHelpControl(Composite parent) {
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.NO_FOCUS);
		((GridLayout) parent.getLayout()).numColumns++;
		toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		final Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		toolBar.setCursor(cursor);
		toolBar.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				cursor.dispose();
			}
		});

		ToolItem item = new ToolItem(toolBar, SWT.NONE);
		Image image = JFaceResources.getImage(DLG_IMG_HELP);

		if (image != null)
			item.setImage(JFaceResources.getImage(DLG_IMG_HELP));

		item.setToolTipText(JFaceResources.getString("helpToolTip"));

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (trayOpened) {
					closeTray();
				} else {
					openTray(new DialogTray() {
						@Override
						protected Control createContents(Composite parent) {
							final StyledText help = new StyledText(parent,
									SWT.READ_ONLY);
							help.setEditable(false);
							help.setEnabled(false);
							help.setText(LtlStrings.ltlHelpText);
							help.setBackground(Display.getDefault()
									.getSystemColor(SWT.COLOR_WHITE));

							return help;
						}
					});
				}

				trayOpened = !trayOpened;
			}
		});

		return toolBar;
	}

	private Combo createFormulas(final Composite parent) {
		final Group group = DialogHelpers.createGroup(parent, "Formula:");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Combo combo = new Combo(group, SWT.None);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (String formula : getFormulas()) {
			combo.add(formula);
		}

		combo.select(0);

		Button button = new Button(group, SWT.None);
		button.setText("Open...");
		button.addSelectionListener(new OpenButtonSelectionListener());

		return combo;
	}

	private Combo createStartingPointOptions(final Composite parent) {
		final Group group = DialogHelpers
				.createGroup(parent, "Starting Point:");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Combo combo = new Combo(group, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (StartOption startOption : START_MODES) {
			combo.add(startOption.description);
		}

		combo.select(0);

		return combo;
	}

	private Combo createSymmetryOptions(final Composite parent) {
		Group group = DialogHelpers.createGroup(parent, "Symmetry Reduction:");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Combo combo = new Combo(group, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		SymmetryReductionOption[] symmetryOptions = SymmetryReductionOption
				.values();

		for (SymmetryReductionOption startOption : symmetryOptions) {
			combo.add(startOption.getDescription());
		}

		combo.select(0);

		return combo;
	}

	private void saveFormulas(Set<String> formulas) {
		final IEclipsePreferences configNode = ConfigurationScope.INSTANCE
				.getNode(CONFIGURATION_ID);

		if (configNode != null) {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(
						byteArrayOutputStream);
				objectOutputStream.writeObject(formulas);
				objectOutputStream.flush();

				byte[] formula = byteArrayOutputStream.toByteArray();
				configNode.putByteArray("formula", formula);

				objectOutputStream.close();
				byteArrayOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Set<String> getFormulas() {
		IEclipsePreferences configNode = ConfigurationScope.INSTANCE
				.getNode(CONFIGURATION_ID);

		byte[] formula = null;
		if (configNode != null) {
			formula = configNode.getByteArray("formula", null);
		}

		Set<String> formulas;
		if (formula != null) {
			try {
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
						formula);
				ObjectInputStream objectInputStream = new ObjectInputStream(
						byteArrayInputStream);

				formulas = (Set<String>) objectInputStream.readObject();
			} catch (ClassNotFoundException e) {
				Logger.notifyUser(
						"Class not found exception when trying to read formulas",
						e);
				formulas = new HashSet<String>();
			} catch (IOException e) {
				Logger.notifyUser("Unexpected IO exception", e);
				formulas = new HashSet<String>();
			}
		} else {
			formulas = new HashSet<String>();
		}

		return formulas;
	}

	private PrologTerm parseLTLFormula(String formula) throws CommandException {
		final Animator animator = Animator.getAnimator();
		PrologTerm parsedFormula = null;
		final LtlParser parser = new LtlParser(
				animator.getLanguageDependendPart());

		try {
			parsedFormula = parser.generatePrologTerm(formula, "root");
		} catch (LtlParseException e) { // see Task#163
			MessageDialog.openError(getShell(), "Syntax error",
					e.getLocalizedMessage());
		}

		return parsedFormula;
	}

	private void scheduleJob(final PrologTerm parsedFormula,
			JobChangeAdapter checkingListener) {
		LtlCheckingCommand.StartMode option = START_MODES[startingPointOptions
				.getSelectionIndex()].mode;
		String symmetryOption = SymmetryReductionOption.get(
				symmetryOptions.getSelectionIndex()).name();

		final Job job = new LtlCheckingJob("LTL Model Checking", parsedFormula,
				option, symmetryOption);
		job.setUser(true);
		job.addJobChangeListener(checkingListener);
		job.schedule();
	}

	private final class StartButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			PrologTerm parsedFormula = null;

			try {
				String formula = formulas.getText();
				parsedFormula = parseLTLFormula(formula);

				if (parsedFormula != null) {
					List<String> formulaItems = Arrays.asList(formulas
							.getItems());

					if (!formulaItems.contains(formula))
						formulas.add(formula);

					saveFormulas(new TreeSet<String>(Arrays.asList(formulas
							.getItems())));
				}
			} catch (CommandException exception) {
				// the only thing we can do is to abort
				close();
			}

			if (parsedFormula != null) {
				scheduleJob(parsedFormula, new LtlCheckingFinishedListener(
						shell));
				close();
			}
		}
	}

	private final class OpenButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
			fileDialog.setFilterExtensions(new String[] { "*.ltl", "*.*" });
			fileDialog.setFilterNames(new String[] { "LTL files (*.ltl)",
					"All files (*.*)" });
			String fileName = fileDialog.open();

			if (fileName != null) {
				BufferedReader reader = null;

				try {
					reader = new BufferedReader(new FileReader(fileName));

					String formula = null;
					List<String> formulaItems = Arrays.asList(formulas
							.getItems());

					List<String> fileFormulas = new ArrayList<String>();
					List<PrologTerm> parsedFormulas = new ArrayList<PrologTerm>();

					while ((formula = reader.readLine()) != null) {
						formula = formula.trim();

						if (formula.equals("")
								|| (formula.length() > 0 && formula.charAt(0) == '#'))
							continue;

						PrologTerm parsedFormula = parseLTLFormula(formula);

						if (parsedFormula != null) {
							if (!formulaItems.contains(formula)) {
								formulas.add(formula);
							}

							if (!fileFormulas.contains(formula)) {
								fileFormulas.add(formula);
								parsedFormulas.add(parsedFormula);
							}
						} else {
							break;
						}
					}

					saveFormulas(new TreeSet<String>(Arrays.asList(formulas
							.getItems())));

					JobChangeAdapter checkingListener = new LtlMultiCheckingFinishedListener(
							shell, fileFormulas);

					for (PrologTerm parsedFormula : parsedFormulas) {
						scheduleJob(parsedFormula, checkingListener);
					}

					formulas.select(0);
					close();

				} catch (FileNotFoundException | NoSuchFileException e) {
					Logger.notifyUser("File not found", e);
				} catch (IOException e) {
					Logger.notifyUser("Unexpected IO exception", e);
				} catch (CommandException e) {
					Logger.notifyUser("Command exception", e);
				} finally {
					try {
						if (reader != null) reader.close();
					} catch (IOException e) {
						Logger.notifyUser("Unexpected IO exception", e);
					}
				}
			}
		}
	}

	private static final class StartOption {
		private final LtlCheckingCommand.StartMode mode;
		private final String description;

		private StartOption(LtlCheckingCommand.StartMode mode,
				String description) {
			this.mode = mode;
			this.description = description;
		}
	}
}
