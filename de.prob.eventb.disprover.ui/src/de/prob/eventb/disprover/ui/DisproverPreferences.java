package de.prob.eventb.disprover.ui;

import de.prob.eventb.disprover.core.DisproverReasonerInput;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class DisproverPreferences extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final class PushButton extends SelectionAdapter {

		private final Text text;
		private final Shell shell;

		public PushButton(final Shell shell, final Text text) {
			this.shell = shell;
			this.text = text;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			super.widgetSelected(e);
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			String open = dialog.open();
			text.setText(open);
		}

	}

	public static final String PREFERENCE_NODE = DisproverReasonerInput.PREFERENCE_NODE;
	/** time-out for "Use ProB WD Prover", option wd_prover_timeout(T) */
	public static final String WD_PROVER_TIMEOUT_KEY = DisproverReasonerInput.WD_PROVER_TIMEOUT_KEY;
	public static final int DEFAULT_WD_PROVER_TIMEOUT = DisproverReasonerInput.DEFAULT_WD_PROVER_TIMEOUT;

	private Preferences prefNode;
	private Text timeoutTextField;
	private Text wdTimeoutTextField;
	private Button checkCLPFD;
	private Button checkCHR;
	private Button checkCSE;
	private Button checkSMT;
	private Button checkDoubleEval;
	private Button checkExportSelectedHyps;

	public DisproverPreferences() {
		super();
	}

	public DisproverPreferences(final String title) {
		super(title);
	}

	public DisproverPreferences(final String title, final ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(final Composite parent) {
		Composite pageComponent = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		pageComponent.setLayout(layout);

		new Label(pageComponent, SWT.NONE).setText("Time-out:");
		timeoutTextField = new Text(pageComponent, SWT.NONE);
		int timeout = prefNode.getInt("timeout", 1000);
		timeoutTextField.setText(Integer.toString(timeout));
		timeoutTextField.setSize(100, timeoutTextField.getSize().y);
		timeoutTextField.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				if (e.text.length() >= 1) {
					e.doit = true;
					for (int i = 0; i < e.text.length(); i++) {
						if (!Character.isDigit(e.text.charAt(i))) {
							e.doit = false;
						}
					}
				}
			}
		});

		Label timeoutRemark = new Label(pageComponent, SWT.WRAP);
		timeoutRemark
				.setText("Note: The time-out is applied to each constraint-solving action.\nThe maximum time-out is thus twice as long (solving with all hypotheses and with the selected hypotheses only).");
		GridData gridData2 = new GridData();
		gridData2.horizontalSpan = 2;
		timeoutRemark.setLayoutData(gridData2);

		new Label(pageComponent, SWT.NONE).setText("WD prover time-out:");
		wdTimeoutTextField = new Text(pageComponent, SWT.NONE);
		wdTimeoutTextField.setText(Integer.toString(getWdProverTimeout()));
		wdTimeoutTextField.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				for (int i = 0; i < e.text.length(); i++) {
					if (!Character.isDigit(e.text.charAt(i))) {
						e.doit = false;
					}
				}
			}
		});

		Label wdTimeoutRemark = new Label(pageComponent, SWT.WRAP);
		wdTimeoutRemark
				.setText("Note: The WD prover time-out is used by \"Use ProB WD Prover\", which does not use constraint solving.");
		// layout data must not be shared between controls
		GridData wdGridData = new GridData();
		wdGridData.horizontalSpan = 2;
		wdTimeoutRemark.setLayoutData(wdGridData);

		new Label(pageComponent, SWT.NONE).setText("Use CLP(FD) Solver:");
		checkCLPFD = new Button(pageComponent, SWT.CHECK);
		checkCLPFD.setSelection(prefNode.getBoolean("clpfd", true));
		checkCLPFD.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!checkCLPFD.getSelection()) {
					checkCHR.setSelection(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		new Label(pageComponent, SWT.NONE).setText("Use CHR Solver:");
		checkCHR = new Button(pageComponent, SWT.CHECK);
		checkCHR.setSelection(prefNode.getBoolean("chr", true));
		checkCHR.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (checkCHR.getSelection()) {
					checkCLPFD.setSelection(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label chrRemark = new Label(pageComponent, SWT.WRAP);
		chrRemark
				.setText("Note: The CHR Solver can only be used in conjunction with the CLP(FD) solver.");
		chrRemark.setLayoutData(gridData2);

		new Label(pageComponent, SWT.NONE)
				.setText("Use Common Subexpression Elemination:");
		checkCSE = new Button(pageComponent, SWT.CHECK);
		checkCSE.setSelection(prefNode.getBoolean("cse", false));

		new Label(pageComponent, SWT.NONE)
				.setText("Enable SMT solver support in ProB interpreter:");
		checkSMT = new Button(pageComponent, SWT.CHECK);
		checkSMT.setSelection(prefNode.getBoolean("smt", false));

		new Label(pageComponent, SWT.NONE)
				.setText("Check (Hypotheses ^ Goal) in addition to (Hypotheses ^ not Goal) to identify contradiction in hypotheses:");
		checkDoubleEval = new Button(pageComponent, SWT.CHECK);
		// default must match the one used in DisproverCommand
		checkDoubleEval.setSelection(prefNode.getBoolean("doubleeval", true));

		new Label(pageComponent, SWT.NONE)
				.setText("Export Goal and selected Hypotheses to B file (/tmp/ProB_Rodin_PO_SelectedHyps.mch):");
		checkExportSelectedHyps = new Button(pageComponent, SWT.CHECK);
		checkExportSelectedHyps.setSelection(prefNode.getBoolean("exportpo", false));

		return pageComponent;
	}

	@Override
	public boolean performOk() {
		prefNode.put("timeout", timeoutTextField.getText());
		final String wdTimeout = wdTimeoutTextField.getText().trim();
		if (wdTimeout.isEmpty()) {
			prefNode.remove(WD_PROVER_TIMEOUT_KEY); // back to the default
		} else {
			prefNode.put(WD_PROVER_TIMEOUT_KEY, wdTimeout);
		}
		prefNode.putBoolean("clpfd", checkCLPFD.getSelection());
		prefNode.putBoolean("chr", checkCHR.getSelection());
		prefNode.putBoolean("cse", checkCSE.getSelection());
		prefNode.putBoolean("smt", checkSMT.getSelection());
		prefNode.putBoolean("doubleeval", checkDoubleEval.getSelection());
		prefNode.putBoolean("exportpo", checkExportSelectedHyps.getSelection());
		try {
			prefNode.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return super.performOk();
	}

	@Override
	public void init(final IWorkbench workbench) {
		prefNode = getPreferenceNode();
	}

	private static Preferences getPreferenceNode() {
		return Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node(PREFERENCE_NODE);
	}

	/**
	 * @return the time-out for ProB's WD prover from the preferences
	 */
	public static int getWdProverTimeout() {
		return DisproverReasonerInput.getWdProverTimeoutPreference();
	}

}
