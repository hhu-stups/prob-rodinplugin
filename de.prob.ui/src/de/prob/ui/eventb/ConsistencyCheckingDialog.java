/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.command.ConsistencyCheckingSearchOption;
import de.prob.core.command.SymmetryReductionOption;
import de.prob.ui.DialogHelpers;

public class ConsistencyCheckingDialog extends Dialog {

	private final class StartButtonSelectionListener implements
			SelectionListener {
		private final Button[] checks;
		private final Button[] symmetry;

		private StartButtonSelectionListener(final Button[] checks,
				final Button[] symmetry) {
			this.checks = checks;
			this.symmetry = symmetry;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			String symmetryOption = selectSymmetryOption(symmetry);
			Set<ConsistencyCheckingSearchOption> selectSettings = selectSettings(checks);
			scheduleJob(selectSettings, symmetryOption);
			close();
		}

		private Set<ConsistencyCheckingSearchOption> selectSettings(
				final Button[] checks) {
			HashSet<ConsistencyCheckingSearchOption> result = new HashSet<ConsistencyCheckingSearchOption>();
			for (int i = 0; i < checks.length; i++) {
				if (checks[i].getSelection()) {
					result.add(ConsistencyCheckingSearchOption.get(i));
				}
			}
			return result;
		}

		private String selectSymmetryOption(final Button[] symmetry) {
			int symmetryOption = 0;
			for (int i = 0; i < symmetry.length; i++) {
				if (symmetry[i].getSelection()) {
					symmetryOption = i;
					break;
				}
			}
			return SymmetryReductionOption.get(symmetryOption).name();
		}

		private void scheduleJob(
				final Set<ConsistencyCheckingSearchOption> selectSettings,
				final String symmetryOption) {
			job = new ConsistencyCheckingJob("Model Checking", selectSettings,
					symmetryOption);
			job.setUser(true);
			job.addJobChangeListener(new ConsistencyCheckingFinishedListener(
					shell));
			job.schedule();
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			// Do nothing
		}
	}

	private Job job;
	private final Shell shell;
	private Button startButton;

	protected ConsistencyCheckingDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		Composite comp = (Composite) super.createDialogArea(parent);

		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 1;

		GridLayout glayout = new GridLayout(2, true);
		Composite c = new Composite(comp, SWT.NONE);
		c.setLayout(glayout);

		final Button[] checks = createSettingsGroup(c);
		final Button[] symmetry = createSymmetryGroup(c);

		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		startButton = new Button(comp, SWT.PUSH);
		startButton.setText("Start Model Checking");
		startButton.setLayoutData(data);
		startButton.addSelectionListener(new StartButtonSelectionListener(
				checks, symmetry));
		return comp;

	}

	private Button[] createSymmetryGroup(final Composite c) {
		Group group = DialogHelpers.createGroup(c, "Symmetry Reduction");
		SymmetryReductionOption[] symmetryOptions = SymmetryReductionOption
				.values();
		final Button[] symmetry = new Button[symmetryOptions.length];

		for (int i = 0; i < symmetryOptions.length; i++) {
			symmetry[i] = new Button(group, SWT.RADIO);
			symmetry[i].setText(symmetryOptions[i].getDescription());
			symmetry[i].setSelection(symmetryOptions[i].isDefault());
		}
		return symmetry;
	}

	private Button[] createSettingsGroup(final Composite c) {
		Group group = DialogHelpers.createGroup(c, "Settings");
		final ConsistencyCheckingSearchOption[] options = ConsistencyCheckingSearchOption
				.values();
		final Button[] checks = new Button[options.length];

		for (int i = 0; i < options.length; i++) {
			checks[i] = new Button(group, SWT.CHECK);
			checks[i].setText(options[i].getDescription());
			checks[i].setSelection(options[i].isEnabledByDefault());
		}
		return checks;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Model Checking");
	}

}
