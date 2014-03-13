package de.prob.eventb.disprover.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.osgi.service.prefs.*;

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

	private Preferences prefNode;
	private Text timeoutTextField;
	private Button checkCLPFD;
	private Button checkCHR;

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

		return pageComponent;
	}

	@Override
	public boolean performOk() {
		prefNode.put("timeout", timeoutTextField.getText());
		prefNode.putBoolean("clpfd", checkCLPFD.getSelection());
		prefNode.putBoolean("chr", checkCHR.getSelection());
		try {
			prefNode.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return super.performOk();
	}

	@Override
	public void init(final IWorkbench workbench) {
		prefNode = Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node("prob_disprover_preferences");
	}

}
