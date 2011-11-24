package de.prob.ui.ticket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.internal.ConfigurationInfo;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.prob.core.Animator;
import de.prob.logging.Logger;
import de.prob.ui.ProbUiPlugin;

@SuppressWarnings("restriction")
public class BugReportWizard extends Wizard {

	private WizardPage1 page1;
	// private WizardPage2 page2;
	private WizardPage3 page3;

	private String email = "";
	private String summary = "";
	private Boolean addTrace = true;
	private String description = "";

	private static final Preferences TICKET_PREFS = Platform
			.getPreferencesService().getRootNode().node(InstanceScope.SCOPE)
			.node("prob_ticket_preferences");

	public BugReportWizard() {
		super();
		setNeedsProgressMonitor(true);
		this.email = TICKET_PREFS.get("email", "");
	}

	public BugReportWizard(final String summary, final Boolean addTrace,
			final String description) {
		super();
		setNeedsProgressMonitor(true);

		this.email = TICKET_PREFS.get("email", "");
		this.summary = summary;
		this.addTrace = addTrace;
		this.description = description;
	}

	@Override
	public void addPages() {
		page1 = new WizardPage1(email, summary, description, addTrace, true);
		// page2 = new WizardPage2(description);
		page3 = new WizardPage3();
		addPage(page1);
		// addPage(page2);
		addPage(page3);

	}

	@Override
	public boolean performFinish() {

		TICKET_PREFS.put("email", page1.getEmail());
		try {
			TICKET_PREFS.flush();
		} catch (BackingStoreException e) {
			Logger.notifyUserWithoutBugreport(
					"Problem while storing preferences. "
							+ e.getLocalizedMessage(), e);
		}

		Ticket ticket = new Ticket(page1.getEmail(), page1.getSummary(), "",
				page1.getDetailedDescription(), page1.isSensitive());

		for (Attachment a : page3.getAttachments()) {
			ticket.addAttachment(a);
		}

		if (page1.isAddTrace()) {
			addTraceFileToTicket(ticket);
			addInstallationDetailsToTicket(ticket);
		}

		try {
			ticket.send();
		} catch (XmlRpcException e) {
			Logger.notifyUserWithoutBugreport("Error sending bug report", e);
		}

		return true;
	}

	private void addInstallationDetailsToTicket(final Ticket ticket) {
		// Installation Details
		try {
			File[] installationDetailsFiles = new File[] { fetchPlugIns(),
					fetchConfiguration(), fetchErrorLog() };
			File zipFile;
			zipFile = File.createTempFile("InstallationDetails", ".tmp");
			compressFiles(installationDetailsFiles, zipFile);

			Attachment a = new Attachment(zipFile.getAbsolutePath().toString(),
					"installation details");
			a.setFilename("InstallationDetails.zip");
			ticket.addAttachment(a);

		} catch (IOException e) {
			Logger.notifyUserWithoutBugreport(
					"Error adding installation details", e);
		}
	}

	private void addTraceFileToTicket(final Ticket ticket) {
		// Trace File
		if (Animator.getAnimator().isMachineLoaded()) {
			try {
				File tmpFile = File.createTempFile("ProBTrace", ".tmp");
				tmpFile.deleteOnExit();

				String data = Animator.getAnimator().getTrace()
						.getTraceAsString();
				String fileName = tmpFile.getAbsoluteFile().toString();

				OutputStreamWriter writer;
				writer = new OutputStreamWriter(new FileOutputStream(fileName));
				BufferedWriter out = new BufferedWriter(writer);
				out.write(data, 0, data.length());
				out.close();

				Attachment a = new Attachment(tmpFile.getAbsolutePath()
						.toString(), "current trace");
				a.setFilename("ProBTraceFile.txt");
				ticket.addAttachment(a);

			} catch (IOException e) {
				Logger.notifyUserWithoutBugreport("Error adding trace file", e);
			}
		}
	}

	private File fetchErrorLog() {
		// Error Log
		String filename = Platform.getLogFileLocation().toOSString();

		try {
			File errorFile = File.createTempFile("ErrorLog", ".txt");
			errorFile.deleteOnExit();

			BufferedReader input = new BufferedReader(new FileReader(filename));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(errorFile));
			BufferedWriter output = new BufferedWriter(writer);

			String line;
			boolean doCopy = false;
			String today = String.format("%1$tY-%1$tm-%1$td",
					Calendar.getInstance()); // current Date similar to
												// YYYY-MM-DD

			while ((line = input.readLine()) != null) {
				if (doCopy || line.startsWith("!SESSION " + today)) {
					doCopy = true;
					output.write(line);
					output.newLine();
				}
			}

			input.close();
			output.close();

			return errorFile;

		} catch (IOException e) {
			Logger.notifyUserWithoutBugreport("Error while fetching Error Log",
					e);
		}
		return null;
	}

	private File fetchConfiguration() {
		// Configuration
		String summary = "";
		try {
			summary = ConfigurationInfo.getSystemSummary();
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer(
					"Exception while getting System Summary.\n");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			sb.append(sw.toString());
			summary = sb.toString();
		}
		try {
			File configFile = File.createTempFile("Configuration", ".txt");
			configFile.deleteOnExit();

			PrintWriter out = new PrintWriter(configFile);
			out.print(summary);
			out.close();

			return configFile;

		} catch (IOException e) {
			Logger.notifyUserWithoutBugreport(
					"Error while fetching Configuration", e);
		}
		return null;
	}

	private File fetchPlugIns() {
		// Plug-ins
		try {
			File plugInsFile = File.createTempFile("PlugIns", ".txt");
			plugInsFile.deleteOnExit();

			OutputStreamWriter writer;
			writer = new OutputStreamWriter(new FileOutputStream(plugInsFile));
			BufferedWriter output = new BufferedWriter(writer);

			for (Bundle b : ProbUiPlugin.getDefault().getInstalledBundles()) {
				output.write(b.toString());
				output.newLine();
			}

			output.close();
			return plugInsFile;

		} catch (IOException e) {
			Logger.notifyUserWithoutBugreport("Error while fetching Plug-ins",
					e);
		}
		return null;
	}

	private void compressFiles(final File[] inputFiles, final File zipFile) {
		try {
			byte[] buf = new byte[4096];
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipFile));
			for (int i = 0; i < inputFiles.length; i++) {
				File inFile = inputFiles[i];
				FileInputStream inStream = new FileInputStream(inFile);
				out.putNextEntry(new ZipEntry(inFile.getName()));
				int len;
				while ((len = inStream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				inStream.close();
			}
			out.close();

		} catch (IOException e) {
			Logger.notifyUserWithoutBugreport("Error while compressing Files",
					e);
		}
	}

}
