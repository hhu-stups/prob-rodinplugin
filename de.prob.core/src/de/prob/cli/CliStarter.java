/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.cli;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.osgi.framework.Bundle;

import de.prob.cli.clipatterns.*;
import de.prob.core.internal.Activator;
import de.prob.logging.Logger;

public final class CliStarter {
	private static final String[] JARS = new String[] { "BParser.jar",
			"ParserAspects.jar", "aspectjrt.jar", "prolog.jar" };

	private Process prologProcess;
	private String debuggingKey;

	private int port = -1;
	private Long userInterruptReference = null;

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

	public String getDebuggingKey() {
		return debuggingKey;
	}

	private void startProlog(final File file) throws CliException {
		prologProcess = null;
		debuggingKey = null;

		final String os = Platform.getOS();
		final String arch = Platform.getOSArch();
		final File applicationPath = getCliPath();

		final String fullcp = createFullClasspath(os, applicationPath);

		final OsSpecificInfo osInfo = getOsInfo(os, arch);

		final String osPath = applicationPath + File.separator + osInfo.subdir;
		final String executable = osPath + File.separator + osInfo.cliName;
		Logger.info("Starting ProB CLI for " + os + " ... Path is "
				+ executable);

		List<String> command = new ArrayList<String>();
		if (osInfo.helperCmd != null) {
			command.add(osInfo.helperCmd);
		}
		command.add(executable);
		// command.add("-ll");
		command.add("-sf");
		command.add("-parsercp");
		command.add(fullcp);

		if (file != null) {
			command.add(file.getAbsolutePath());
		}

		createDebuggingKey();

		final ProcessBuilder pb = new ProcessBuilder();
		pb.command(command);
		pb.environment().put("PROB_DEBUGGING_KEY", debuggingKey);
		pb.environment().put("TRAILSTKSIZE", "1M");
		pb.environment().put("PROLOGINCSIZE", "50M");
		pb.environment().put("PROB_HOME", osPath);
		try {
			prologProcess = pb.start();
		} catch (IOException e) {
			final String message = "Problem while starting up ProB CLI. Tried to execute:"
					+ executable;
			Logger.notifyUser(message, e);
			throw new CliException(message, e, true);
		}

		Assert.isNotNull(prologProcess);

		final BufferedReader input = new BufferedReader(new InputStreamReader(
				prologProcess.getInputStream()));
		final BufferedReader output = new BufferedReader(new InputStreamReader(
				prologProcess.getErrorStream()));

		startErrorLogger(output);

		extractCliInformation(input);
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

	private OsSpecificInfo getOsInfo(final String os, String architecture)
			throws CliException {
		if (os.equals(Platform.OS_MACOSX)) {
			return new OsSpecificInfo("macos", "probcli.sh", "sh",
					"send_user_interrupt");
		}
		if (os.equals(Platform.OS_WIN32)) {
			return new OsSpecificInfo("windows", "probcli.exe", null,
					"send_user_interrupt.exe");
		}

		if (os.equals(Platform.OS_LINUX)) {
			String linux = "linux";
			if (architecture.equals(Platform.ARCH_X86_64)) {
				linux = "linux64";
			}
			return new OsSpecificInfo(linux, "probcli.sh", "sh",
					"send_user_interrupt");
		}
		final CliException cliException = new CliException(
				"ProB does not support the plattform: " + os);
		cliException.notifyUserOnce();
		throw cliException;
	}

	@SuppressWarnings("unchecked")
	private void extractCliInformation(final BufferedReader input)
			throws CliException {
		final PortPattern portPattern = new PortPattern();
		final InterruptRefPattern intPattern = new InterruptRefPattern();
		analyseStdout(input, Arrays.asList(portPattern, intPattern));
		port = portPattern.getValue();
		userInterruptReference = intPattern.getValue();
	}

	private static String createFullClasspath(final String os, final File path)
			throws CliException {
		final File base = new File(path.getParentFile().getParentFile(),
				"de.prob.common");
		final File common = new File(base, "common");
		final File lib = new File(common, "lib");
		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (final String jar : JARS) {
			final File entry = new File(lib, jar);
			if (!isFirst) {
				sb.append(File.pathSeparator);
			}
			sb.append(entry.getPath());
			isFirst = false;
		}
		return sb.toString();
	}

	private void startOutputLogger(final BufferedReader input) {
		stdLogger = new OutputLoggerThread("(Output " + port + ")", input);
		stdLogger.start();
	}

	private void startErrorLogger(final BufferedReader output) {
		errLogger = new OutputLoggerThread("(Error " + port + ")", output);
		errLogger.start();
	}

	private void createDebuggingKey() {
		Random random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			random = new Random();
		}
		debuggingKey = Long.toHexString(random.nextLong());
	}

	private void analyseStdout(final BufferedReader input,
			Collection<? extends CliPattern<?>> patterns) throws CliException {
		patterns = new ArrayList<CliPattern<?>>(patterns);
		try {
			String line;
			boolean endReached = false;
			while (!endReached && (line = input.readLine()) != null) { // NOPMD
				applyPatterns(patterns, line);
				endReached = patterns.isEmpty()
						|| line.contains("starting command loop");
			}
		} catch (IOException e) {
			final String message = "Problem while starting ProB. Cannot read from input stream.";
			Logger.notifyUser(message, e);
			throw new CliException(message, e, true);
		}
		for (CliPattern<?> p : patterns) {
			p.notFound();
		}
	}

	private void applyPatterns(
			final Collection<? extends CliPattern<?>> patterns,
			final String line) {
		for (Iterator<? extends CliPattern<?>> it = patterns.iterator(); it
				.hasNext();) {
			final CliPattern<?> p = it.next();
			if (p.matchesLine(line)) {
				it.remove();
			}
		}
	}

	private File getCliPath() throws CliException {
		final Bundle bundle = Activator.getDefault().getBundle();
		final String fileURL = "prob";
		final URL entry = bundle.getEntry(fileURL);

		if (entry == null) {
			throw new CliException(
					"Unable to find directory with prob executables.");
		}

		try {
			URL resolvedUrl = FileLocator.resolve(entry);

			// We need to use the 3-arg constructor of URI in order to properly
			// escape file system chars.
			URI resolvedUri = new URI(resolvedUrl.getProtocol(),
					resolvedUrl.getPath(), null);

			return new File(resolvedUri);
		} catch (URISyntaxException e) {
			throw new CliException("Unable to construct file '"
					+ entry.getPath() + "'");
		} catch (IOException e) {
			throw new CliException("Input/output error when trying t find '"
					+ fileURL + "'");
		}

		// final Path path = new Path("prob");
		// final URL fileURL = FileLocator.find(
		// Activator.getDefault().getBundle(), path, null);
		// if (fileURL == null) {
		// throw new CliException(
		// "Unable to find directory with prob executables.");
		// }
		// URL resolved;
		// try {
		// resolved = FileLocator.resolve(fileURL);
		// } catch (IOException e2) {
		// throw new CliException("Input/output error when trying t find '"
		// + fileURL + "'");
		// }
		// URI uri;
		// try {
		// uri = new URI(resolved.getProtocol(), resolved.getPath(), null);
		// } catch (URISyntaxException e1) {
		// throw new CliException("Unable to construct file '"
		// + resolved.getPath() + "'");
		// }
		//
		// return new File(uri);
	}

	private static class OutputLoggerThread extends Thread {

		private final BufferedReader in;

		private final String prefix;

		private volatile boolean shutingDown = false;

		public OutputLoggerThread(final String name, final BufferedReader in) {
			super();
			prefix = "[" + name + "] ";
			this.in = in;
		}

		@Override
		public void run() {
			try {
				while (!shutingDown) {
					final String line = in.readLine();

					if (line == null) {
						break;
					}
					// Logger.log(IStatus.INFO, IStatus.OK, prefix + line,
					// null);
					System.err.println(prefix + line);
				}
			} catch (IOException e) {
				if (!"Stream closed".equals(e.getMessage())) {
					final String message = "OutputLogger died with error";
					Logger.log(IStatus.INFO, Logger.DEBUG, message, e);
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
		if (userInterruptReference != null) {
			try {
				final OsSpecificInfo osInfo = getOsInfo(Platform.getOS(),
						Platform.getOSArch());
				final String command = getCliPath() + File.separator
						+ osInfo.subdir + File.separator
						+ osInfo.userInterruptCmd;
				Runtime.getRuntime().exec(
						new String[] { command,
								userInterruptReference.toString() });
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
		final String helperCmd;
		final String userInterruptCmd;

		public OsSpecificInfo(final String subdir, final String cliName,
				final String helperCmd, final String userInterruptCmd) {
			this.subdir = subdir;
			this.cliName = cliName;
			this.helperCmd = helperCmd;
			this.userInterruptCmd = userInterruptCmd;
		}

	}
}
