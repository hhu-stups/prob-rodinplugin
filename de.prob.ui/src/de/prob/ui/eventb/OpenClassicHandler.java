package de.prob.ui.eventb;

import de.prob.core.LimitedLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
//import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;
//import org.eventb.core.IMachineRoot;
import org.osgi.service.prefs.Preferences;

import de.prob.logging.Logger;

public class OpenClassicHandler extends AbstractHandler implements IHandler {

	private ISelection fSelection;
	
	private static final String PROB_CLASSIC_NAME = "ProB Tcl/Tk";
	private static final String PROB_STANDALONE_NAME = "ProB Standalone";
	private static final String PROB2_NAME = "ProB2-UI";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		fSelection = HandlerUtil.getCurrentSelection(event);

		final String prob_location = getBinaryLocation();
		if (prob_location == null) {
			Logger.notifyUser("You need to specify a location for " + PROB_STANDALONE_NAME +". See Preferences -> ProB Standalone.");
		} else {
			final IEventBRoot root = getSelection();
			if (root != null) {
				if(prob_location.endsWith(".jar")) {
					// we can open directly the .bum or .buc files with ProB2; we need to get it from root
					// we could also get a platform URI: URI fileURI = URI.createPlatformResourceURI(root.getResource().getFullPath().toString(), true);
					String bum_buc_path = //ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + root.getResource().getFullPath().toString();
						root.getResource().getRawLocation().toString();
					runProB2(prob_location, bum_buc_path);
				} else {
					final File temp = createTempFile();
					if (temp == null) {
						// already reported
						return null;
					}
					final String tmp = temp.getAbsolutePath();
					// the temporary file was just created by us, so don't ask
					// whether to overwrite it
					if (ExportNewCoreHandler.writeClassicFile(tmp, root)) {
						runProBClassic(prob_location, tmp);
					}
				}
			} else {
				Logger.notifyUser("You need to select a context or machine to open with " + PROB_STANDALONE_NAME);
			}
		}
		return null;
	}

	private static final class ClassicConsole implements Runnable {
		// number of output lines shown to the user if the program fails
		private static final int LINES_TO_SHOW = 15;

		private final Process process;
		private final BufferedReader output;
		private final String name;
		private final boolean reportFailure;
		private final Deque<String> lastLines = new ArrayDeque<String>();

		private ClassicConsole(final Process process,
				final BufferedReader output, final String name,
				final boolean reportFailure) {
			this.process = process;
			this.output = output;
			this.name = name;
			this.reportFailure = reportFailure;
		}

		@Override
		public void run() {
			try {
				while (true) {
					final String line = output.readLine();
					if (line == null) {
						break;
					}
					System.out.println(name + ": " + line);
					LimitedLogger.getLogger().log(line,name,null);
					lastLines.addLast(line);
					if (lastLines.size() > LINES_TO_SHOW)
						lastLines.removeFirst();
				}
			} catch (IOException e) {
			} finally {
				try {
					output.close();
				} catch (IOException e1) {
				}
			}
			if (reportFailure) {
				reportExitStatus();
			}
		}

		private void reportExitStatus() {
			final int exitCode;
			try {
				exitCode = process.waitFor();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			if (exitCode != 0) {
				final StringBuilder sb = new StringBuilder();
				sb.append(name).append(" terminated with exit code ")
						.append(exitCode).append(".\n");
				if (lastLines.isEmpty()) {
					sb.append("It did not produce any output.\n");
				} else {
					sb.append("Its last output was:\n\n");
					for (String line : lastLines) {
						sb.append(line).append('\n');
					}
				}
				sb.append("\nPlease check the location in Preferences -> ")
						.append(PROB_STANDALONE_NAME)
						.append(". The full output is in the ProB performance log.");
				Logger.notifyUser(sb.toString());
			}
		}
	}

	/**
	 * Copies the output of the process to the log in a background thread that
	 * does not keep Rodin from exiting
	 * 
	 * @param reportFailure
	 *            tell the user if the process ends with an error
	 */
	private static void startConsole(final Process process, final String name,
			final boolean reportFailure) {
		final BufferedReader output = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		final Thread thread = new Thread(new ClassicConsole(process, output,
				name, reportFailure), name + " output");
		thread.setDaemon(true);
		thread.start();
	}

	private void runProBClassic(final String probBinary, final String modelFile) {
		try {
			final ProcessBuilder pb;
			if (new File(probBinary).isFile()) {
				// pass paths as single arguments, they may contain spaces
				pb = new ProcessBuilder(probBinary, modelFile);
			} else {
				// not a plain file, the location may contain a command with
				// arguments; split it at white space like before
				final List<String> command = new ArrayList<String>(
						Arrays.asList(probBinary.trim().split("\\s+")));
				command.add(modelFile);
				pb = new ProcessBuilder(command);
			}
			// merge stderr, otherwise it is never read and may block the process
			final Process process = pb.redirectErrorStream(true).start();
			startConsole(process, PROB_CLASSIC_NAME, true);

		} catch (IOException e) {
			Logger.notifyUser("You need to specify a correct location for "
				+ PROB_CLASSIC_NAME + ". See Preferences -> ProB Standalone.\n"
				+ PROB_CLASSIC_NAME + " location: "+ probBinary + 
				"\nModel file: " + modelFile +
				"\nError message: "+ e.getLocalizedMessage());
		}
	}

	private void runProB2(final String probBinary, final String modelFile) {
		// call prob2-ui jar file
		// from command-line it is: java -jar prob2-ui-1.0.1-SNAPSHOT-all.jar --machine-file  FILE
		// waiting for "java -version" must not block the UI thread, so launch
		// in the background
		final Thread launcher = new Thread(new Runnable() {
			@Override
			public void run() {
				launchProB2(probBinary, modelFile);
			}
		}, PROB2_NAME + " launcher");
		launcher.setDaemon(true);
		launcher.start();
	}

	private static void launchProB2(final String probBinary,
			final String modelFile) {
		try {
			// get some version info first:
			final String[] vcommand = {"java", "-version"};
			ProcessBuilder vpb = new ProcessBuilder(vcommand).redirectErrorStream(true);
			Process vprocess = vpb.start();
			// failures are reported below
			startConsole(vprocess, "java -version", false);
			vprocess.waitFor();
			if (vprocess.exitValue() != 0) {
				Logger.notifyUser("Failed to start java -version. Exit code: " + vprocess.exitValue());
			}
			
			final String[] command = {"java", "-jar", probBinary, "--machine-file", modelFile};
			System.out.println("Launching ProB2UI using: java -jar " + probBinary);
			LimitedLogger.getLogger().log("Launching ProB2UI using: java -jar " + probBinary + " --machine-file " + modelFile,"ProB2UI",null);
			ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(true);
			Process process = pb.start();
			startConsole(process, PROB2_NAME, true);
			
		} catch (IOException e) {
			notifyLaunchFailed(probBinary, modelFile, e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void notifyLaunchFailed(final String probBinary,
			final String modelFile, final Exception e) {
		Logger.notifyUser("You need to specify a correct location for "
			+ PROB2_NAME + ". See Preferences -> ProB Standalone.\n"
			+ PROB2_NAME + " location: "+ probBinary + 
			"\nModel file: " + modelFile +
			"\nError message: "+ e.getLocalizedMessage());
	}
	
	private String getBinaryLocation() {
		Preferences preferences = Platform.getPreferencesService()
				.getRootNode().node(InstanceScope.SCOPE)
				.node("prob_classic_preferences");
		final String location = preferences.get("location", null);
		if (location == null || location.trim().isEmpty())
			return null;
		return unescapeSpaces(location.trim());
	}

	/**
	 * Older versions of the preference page stored spaces in the location
	 * as "\ "; those mean a plain space in a file name
	 */
	static String unescapeSpaces(final String location) {
		final String unescaped = location.replace("\\ ", " ");
		// only if this names an existing file, a lone backslash could be
		// part of a Windows path
		return new File(unescaped).exists() ? unescaped : location;
	}

	private File createTempFile() {
		File temp = null;
		try {
			temp = File.createTempFile("prob_", ".eventb");
			temp.deleteOnExit();
		} catch (IOException e) {
			Logger.notifyUser("Something went wrong while saving temp file.\n"
					+ e.getLocalizedMessage());
		}
		return temp;
	}

	private IEventBRoot getSelection() {
		if (!(fSelection instanceof IStructuredSelection))
			return null;
		final IStructuredSelection ssel = (IStructuredSelection) fSelection;
		if (ssel.size() != 1)
			return null;
		if (!(ssel.getFirstElement() instanceof IEventBRoot)) // was IMachineRoot
			return null;
		return (IEventBRoot) ssel.getFirstElement();
	}

}
