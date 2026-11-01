package de.prob.ui.eventb;

import de.prob.core.LimitedLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
			Logger.notifyUser("You need to specify a location for" + PROB_STANDALONE_NAME +". See Preferences -> ProB Standalone.");
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
					ExportNewCoreHandler.exportToClassic(tmp, root);
					runProBClassic(prob_location, tmp);
				}
			} else {
				Logger.notifyUser("You need to select a context or machine to open with " + PROB_STANDALONE_NAME);
			}
		}
		return null;
	}

	private static final class ClassicConsole implements Runnable {
		private final BufferedReader output;

		private ClassicConsole(final BufferedReader output) {
			this.output = output;
		}

		@Override
		public void run() {
			try {
				while (true) {
					final String line = output.readLine();
					if (line == null) {
						break;
					}
					System.out.println(PROB_STANDALONE_NAME + ": " + line);
					LimitedLogger.getLogger().log(line,PROB_STANDALONE_NAME,null);
				}
			} catch (IOException e) {
			} finally {
				try {
					output.close();
				} catch (IOException e1) {
				}
			}

		}
	}

	/**
	 * Copies the output of the process to the log in a background thread that
	 * does not keep Rodin from exiting
	 */
	private static void startConsole(final Process process, final String name) {
		final BufferedReader output = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		final Thread thread = new Thread(new ClassicConsole(output), name
				+ " output");
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
			startConsole(process, PROB_CLASSIC_NAME);

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
			startConsole(vprocess, "java -version");
			vprocess.waitFor();
			if (vprocess.exitValue() != 0) {
				Logger.notifyUser("Failed to start java -version. Exit code: " + vprocess.exitValue());
			}
			
			final String[] command = {"java", "-jar", probBinary, "--machine-file", modelFile};
			System.out.println("Launching ProB2UI using: java -jar " + probBinary);
			LimitedLogger.getLogger().log("Launching ProB2UI using: java -jar " + probBinary + " --machine-file " + modelFile,"ProB2UI",null);
			ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(true);
			Process process = pb.start();
			startConsole(process, PROB2_NAME);
			
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
		return preferences.get("location", null);
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
