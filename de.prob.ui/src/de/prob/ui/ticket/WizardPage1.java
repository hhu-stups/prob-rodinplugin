package de.prob.ui.ticket;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardPage1 extends WizardPage {
	private Composite container;
	private Text textEmail;
	private Text textSummary;
	private Text textDescription;
	private Button buttonPrivacy;
	private Button buttonAddTrace;
	private Boolean addTrace = true;
	private Boolean privacy = false;
	private String email = "";
	private String summary = "";
	private String description = "";

	public WizardPage1(final String email, final String summary,
			final String description, final Boolean addTrace, boolean sensitive) {
		super("First Page");
		this.privacy = sensitive;
		setTitle("Submit Bugreport");
		setDescription("Summary");

		this.email = email;
		this.summary = summary;
		this.description = description;
		this.addTrace = addTrace;
	}

	public void createControl(final Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		// Your Email:
		Label labelEmail = new Label(container, SWT.NULL);
		labelEmail.setText("Your email address:");
		textEmail = new Text(container, SWT.BORDER | SWT.SINGLE);
		textEmail.setText(this.email);
		textEmail.addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent e) {
			}

			public void keyReleased(final KeyEvent e) {
				setPageComplete(checkPageComplete());
			}
		});

		// Short Summary:
		Label labelSummary = new Label(container, SWT.NULL);
		labelSummary.setText("Short summary:");
		textSummary = new Text(container, SWT.BORDER | SWT.SINGLE);
		textSummary.setText(this.summary);
		textSummary.addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent e) {
			}

			public void keyReleased(final KeyEvent e) {
				setPageComplete(checkPageComplete());
			}
		});

		// Full Description:
		Label labelDescription = new Label(container, SWT.NULL);
		labelDescription.setText("Full description:");
		Label labelDummy = new Label(container, SWT.NULL);
		labelDummy.setText("");
		textDescription = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		textDescription.setText(this.description);

		// Add trace:
		Label labelAddTrace = new Label(container, SWT.NONE);
		labelAddTrace.setText("Add debugging information:");
		buttonAddTrace = new Button(container, SWT.CHECK);
		buttonAddTrace.setSelection(this.addTrace);

		buttonAddTrace.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent arg0) {
			}

			public void widgetSelected(final SelectionEvent arg0) {
				addTrace = buttonAddTrace.getSelection();
			}
		});

		// Private:
		Label labelPrivacy = new Label(container, SWT.NONE);
		labelPrivacy.setText("Private:");
		buttonPrivacy = new Button(container, SWT.CHECK);
		buttonPrivacy.setSelection(this.privacy);

		buttonPrivacy.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent arg0) {
			}

			public void widgetSelected(final SelectionEvent arg0) {
				privacy = buttonPrivacy.getSelection();
			}
		});

		// Resize Text-Fields
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textEmail.setLayoutData(gd);
		textSummary.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		textDescription.setLayoutData(gd);

		// Required to avoid a system-error
		setControl(container);

		setPageComplete(checkPageComplete());
	}

	@Override
	/*
	 * Override setVisible(boolean visible)-Method of DialogPage to be enabled
	 * to set focus at will
	 */
	public void setVisible(final boolean visible) {
		container.setVisible(visible);

		// Set Focus
		if (!validateEmail(this.getEmail())) {
			textEmail.setFocus();
		} else {
			if (this.getSummary().equals("")) {
				textSummary.setFocus();
			} else {
				textDescription.setFocus();
			}
		}
	}

	private boolean checkPageComplete() {
		Boolean emailValid = validateEmail(this.getEmail());
		Boolean summaryFilled = !textSummary.getText().equals("");

		if (emailValid && summaryFilled) {
			setErrorMessage(null);
			return true;
		} else {
			if (!emailValid) {
				setErrorMessage("Please enter a valid email address.");
				return false;
			}
			if (!summaryFilled) {
				setErrorMessage("Please enter a short summary of the bug.");
				return false;
			}
			return false;
		}
	}

	private Boolean validateEmail(final String email) {
		String regex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
		Boolean emailValid = email.matches(regex);
		return emailValid;
	}

	public String getEmail() {
		return textEmail.getText();
	}

	public String getSummary() {
		return textSummary.getText();
	}

	public String getDetailedDescription() {
		return textDescription.getText();
	}

	public Boolean isAddTrace() {
		return addTrace;
	}

	public Boolean isSensitive() {
		return privacy;
	}
	
	// private IConfigurationElement[] loadElements() {
	// IExtensionPoint point = Platform.getExtensionRegistry()
	//				.getExtensionPoint("org.eclipse.ui", "installationPages"); //$NON-NLS-1$ //$NON-NLS-2$
	// return point.getConfigurationElements();
	// }
}
