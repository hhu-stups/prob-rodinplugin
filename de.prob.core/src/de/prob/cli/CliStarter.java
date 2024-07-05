/** 
 * (c) 2009-2023 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.cli;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.*;
import org.osgi.framework.Bundle;

import de.prob.core.internal.Activator;
import de.prob.logging.Logger;

public final class CliStarter {
	private static final Pattern CLI_PORT_PATTERN = Pattern.compile("^.*Port: (\\d+)$");
	private static final Pattern CLI_USER_INTERRUPT_REFERENCE_PATTERN = Pattern.compile("^.*user interrupt reference id: *(\\d+|off) *$");

	private Process prologProcess;

	private int port = -1;
	private long userInterruptReference = -1L;

	private OutputLoggerThread stdLogger;
	private OutputLoggerThread errLogger;

	public CliStarter() throws CliException {
		this(null);
	}

	public CliStarter(final File file) throws CliException {
		startProlog(file);
	}

	public int getPort() {
		return port;
	}

	public void shutdown() {
		try {
			prologProcess.destroy();
		} catch (RuntimeException e) {
			final String message = "XXXXX Error while stopping cli process: "
					+ e.getLocalizedMessage();
			Logger.notifyUser(message, e);
		} finally {
			stdLogger.shutdown();
			errLogger.shutdown();
		}
	}

	// Based on org.eventb.core.seqprover.xprover.BundledFileExtractor.BundledFileDescriptor#makeExecutable
	// (from rodin-b-sharp/rodincore/org.eventb.core.seqprover)
	private void setExecutable(final File path, final boolean executable) throws CliException {
		final IFileStore store = EFS.getLocalFileSystem().getStore(path.toURI());
		final IFileInfo info = store.fetchInfo();
		info.setAttribute(EFS.ATTRIBUTE_EXECUTABLE, executable);
		try {
			store.putInfo(info, EFS.SET_ATTRIBUTES, null);
		} catch (CoreException e) {
			throw new CliException("Failed to set executable permission", e, false);
		}
	}

	/**
	 * Return {@code process}'s exit code as an {@link Integer}, or {@link Optional#empty()} if it is still running.
	 *
	 * @param process the process whose exit code to get
	 * @return {@code process}'s exit code, or {@link Optional#empty()} if it is still running
	 */
	private static Optional<Integer> getProcessExitCode(Process process) {
		try {
			return Optional.of(process.exitValue());
		} catch (final IllegalThreadStateException ignored) {
			return Optional.empty();
		}
	}

	private void startProlog(final File file) throws CliException {
		prologProcess = null;

		final String os = Platform.getOS();
		final File applicationPath = getCliPath();

		final OsSpecificInfo osInfo = getOsInfo(os);

		final String osPath = applicationPath + File.separator + osInfo.subdir;
		final String executable = osPath + File.separator + osInfo.cliName;
		Logger.info("Starting ProB CLI for " + os + " ... Path is "
				+ executable);

		if (osInfo.needsExecutePermission) {
			setExecutable(new File(executable), true);
		}

		List<String> command = new ArrayList<String>();
		if (Platform.OS_MACOSX.equals(os)) {
			// Run universal probcli as arm64 if possible (i. e. if the host processor is arm64),
			// even if Rodin/Eclipse is running as x86_64.
			// (The macOS default behavior is to match the architecture of the parent process,
			// which is bad in our case,
			// because x86_64 probcli under Rosetta 2 is much slower than native arm64 probcli.)
			command.add("arch");
			// Use the generic -64 option instead of -arm64,
			// because -arm64 causes an "Unknown architecture" error on x86_64 systems
			// (contrary to what the arch(1) man page suggests).
			// -64 corresponds to -arm64 on arm64 systems and -x86_64 on x86_64 systems,
			// so it's always safe to use.
			command.add("-64");
		}
		command.add(executable);
		// command.add("-ll");
		command.add("-sf");
		command.add("-p");command.add("use_safety_ltl_model_checker");command.add("false");
		command.add("-prob_application_type");command.add("rodin"); // supported as of 9/11/2023
		 // disable LTL safety model check as the counter examples lead to assertion failures 
		 // in CounterExampleProposition in CounterExample.java

		if (file != null) {
			command.add(file.getAbsolutePath());
		}

		final ProcessBuilder pb = new ProcessBuilder();
		pb.command(command);
		pb.environment().put("NO_COLOR", "1");
		pb.environment().put("PROB_HOME", osPath);
		try {
			prologProcess = pb.start();
		} catch (IOException e) {
			throw new CliException("Problem while starting up ProB CLI. Tried to execute:" + executable, e, false);
		}

		Assert.isNotNull(prologProcess);

		final BufferedReader input = new BufferedReader(new InputStreamReader(
				prologProcess.getInputStream()));
		final BufferedReader output = new BufferedReader(new InputStreamReader(
				prologProcess.getErrorStream()));

		startErrorLogger(output);

		try {
			extractCliInformation(input);
		} catch (CliException e) {
			// Check if the CLI exited while extracting the information.
			final Optional<Integer> exitCode = getProcessExitCode(prologProcess);
			if (exitCode.isPresent()) {
				// CLI exited, report the exit code.
				throw new CliException("ProB CLI exited with status " + exitCode.get() + " before socket connection could be opened", e, false);
			} else {
				// CLI didn't exit, just rethrow the error.
				throw e;
			}
		}
		// log output from Prolog
		startOutputLogger(input);

		final Process p = prologProcess;

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				p.destroy();
			}
		}));

	}

	private OsSpecificInfo getOsInfo(final String os)
			throws CliException {
		if (os.equals(Platform.OS_WIN32)) {
			return new OsSpecificInfo("windows", "probcli.exe",
					"lib\\send_user_interrupt.exe", false);
		} else {
			final String subdir;
			if (os.equals(Platform.OS_MACOSX)) {
				subdir = "macos";
			} else if (os.equals(Platform.OS_LINUX)) {
				subdir = "linux64";
			} else {
				throw new CliException("ProB does not support the plattform: " + os);
			}

			return new OsSpecificInfo(subdir, "probcli.sh",
				"lib/send_user_interrupt", true);
		}
	}

	private void extractCliInformation(final BufferedReader input)
			throws CliException {
		Integer portTemp = null;
		Long userInterruptReferenceTemp = null;
		try {
			for (String line; (line = input.readLine()) != null;) {
				Logger.info("probcli startup output: " + line);

				Matcher portMatcher = CLI_PORT_PATTERN.matcher(line);
				if (portMatcher.matches()) {
					portTemp = Integer.parseInt(portMatcher.group(1));
					Logger.info("Received port number from CLI: " + portTemp);
				}

				Matcher userInterruptReferenceMatcher = CLI_USER_INTERRUPT_REFERENCE_PATTERN.matcher(line);
				if (userInterruptReferenceMatcher.matches()) {
					String userInterruptReferenceString = userInterruptReferenceMatcher.group(1);
					if ("off".equals(userInterruptReferenceString)) {
						userInterruptReferenceTemp = -1L;
						Logger.info("This ProB build has user interrupt support disabled. Interrupting ProB may not work as expected.");
					} else {
						userInterruptReferenceTemp = Long.parseLong(userInterruptReferenceString);
						Logger.info("Received user interrupt reference from CLI: " + userInterruptReferenceTemp);
					}
				}

				if ((portTemp != null && userInterruptReferenceTemp != null) || line.contains("starting command loop")) {
					break;
				}
			}
		} catch (IOException | NumberFormatException e) {
			throw new CliException("Error while reading information from CLI", e, false);
		}

		if (portTemp == null) {
			throw new CliException("Did not receive port number from CLI");
		} else if (userInterruptReferenceTemp == null) {
			throw new CliException("Did not receive user interrupt reference from CLI");
		}
		port = portTemp;
		userInterruptReference = userInterruptReferenceTemp;
	}

	private void startOutputLogger(final BufferedReader input) {
		stdLogger = new OutputLoggerThread("(Output " + port + ")", input, false);
		stdLogger.start();
	}

	private void startErrorLogger(final BufferedReader output) {
		errLogger = new OutputLoggerThread("(Error " + port + ")", output, true);
		errLogger.start();
	}

	private File getCliPath() throws CliException {
		final Bundle bundle = Activator.getDefault().getBundle();
		final URL entry = bundle.getEntry("prob");

		if (entry == null) {
			throw new CliException(
					"Unable to find directory with prob executables.");
		}

		URL fileUrl;
		try {
			fileUrl = FileLocator.toFileURL(entry);
		} catch (IOException e) {
			throw new CliException("Input/output error when trying to find '" + entry + "'", e, false);
		}

		try {
			// Eclipse has a long-standing bug where Bundle.getEntry etc. don't correctly escape spaces, non-ASCII characters, etc. in file URLs.
			// This makes it impossible to use the standard URL.toURI() method - it will throw an URISyntaxException for these unescaped characters.
			// As a workaround, use Eclipse's URIUtil.toURI(URL), which escapes such unescaped characters.
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=145096
			// https://probjira.atlassian.net/browse/PROBPLUGIN-87
			return new File(URIUtil.toURI(fileUrl));
		} catch (URISyntaxException e) {
			throw new CliException("Unable to construct file '" + entry.getPath() + "'", e, false);
		}
	}

	private static class OutputLoggerThread extends Thread {

		private final BufferedReader in;

		private final String prefix;

		private final boolean logToLog;

		private volatile boolean shutingDown = false;

		public OutputLoggerThread(final String name, final BufferedReader in, boolean logToLog) {
			super();
			prefix = "[" + name + "] ";
			this.in = in;
			this.logToLog = logToLog;
		}

		@Override
		public void run() {
			try {
				while (!shutingDown) {
					final String line = in.readLine();

					if (line == null) {
						break;
					}
					if (logToLog) {
						Logger.log(IStatus.INFO, prefix + line, null);
					}
					System.err.println(prefix + line);
				}
			} catch (IOException e) {
				if (!"Stream closed".equals(e.getMessage())) {
					final String message = "OutputLogger died with error";
					Logger.log(IStatus.INFO, message, e);
				}
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		}

		public void shutdown() {
			shutingDown = true;

			if (isAlive()) {
				interrupt();
			}
		}
	}

	public void sendUserInterruptReference() {
		if (userInterruptReference != -1) {
			try {
				final OsSpecificInfo osInfo = getOsInfo(Platform.getOS());
				final String command = getCliPath() + File.separator
						+ osInfo.subdir + File.separator
						+ osInfo.userInterruptCmd;

				if (osInfo.needsExecutePermission) {
					setExecutable(new File(command), true);
				}

				Runtime.getRuntime().exec(
						new String[] { command,
								String.valueOf(userInterruptReference) });
			} catch (CliException e) {
				Logger.info("getting the os specific info failed with exception: "
						+ e.getLocalizedMessage());
			} catch (IOException e) {
				Logger.info("calling the send_user_interrupt command failed: "
						+ e.getLocalizedMessage());
			}
		}
	}

	private static class OsSpecificInfo {
		final String subdir;
		final String cliName;
		final String userInterruptCmd;
		final boolean needsExecutePermission;

		public OsSpecificInfo(final String subdir, final String cliName,
				final String userInterruptCmd, final boolean needsExecutePermission) {
			this.subdir = subdir;
			this.cliName = cliName;
			this.userInterruptCmd = userInterruptCmd;
			this.needsExecutePermission = needsExecutePermission;
		}

	}
}
