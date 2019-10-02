package de.prob.ui.eventb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
import org.eventb.core.IMachineRoot;
import org.osgi.service.prefs.Preferences;

import de.prob.logging.Logger;

public class OpenClassicHandler extends AbstractHandler implements IHandler {

	private ISelection fSelection;
	
	private static final String PROB_CLASSIC_NAME = "ProB Tcl/Tk";
	private static final String PROB_STANDALONE_NAME = "ProB Standalone";
	private static final String PROB2_NAME = "ProB2-UI";

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		fSelection = HandlerUtil.getCurrentSelection(event);

		final String prob_location = getBinaryLocation();
		if (prob_location == null) {
			Logger.notifyUserWithoutBugreport("You need to specify a location for" + PROB_STANDALONE_NAME +". See Preferences -> ProB Standalone.");
		} else {
			final IEventBRoot root = getSelection();
			if (root != null) {
				if(prob_location.endsWith(".jar")) {
					//  we can open directly the .bum or .buc files with ProB2; we need to get it from root
					// we could also get a platform URI: URI fileURI = URI.createPlatformResourceURI(root.getResource().getFullPath().toString(), true);
					String bum_buc_path = //ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + root.getResource().getFullPath().toString();
							              root.getResource().getRawLocation().toString();
					runProB2(prob_location, bum_buc_path);
				} else {	
					final File temp = createTempFile();
					final String tmp = temp.getAbsolutePath();
					ExportNewCoreHandler.exportToClassic(tmp, root);
					runProBClassic(prob_location, tmp);
				}
			}
		}
		return null;
	}

	private static final class ClassicConsole implements Runnable {
		private final BufferedReader output;

		private ClassicConsole(final BufferedReader output) {
			this.output = output;
		}

		public void run() {
			try {
				while (true) {
					final String line = output.readLine();
					if (line == null) {
						break;
					}
					System.out.println(PROB_STANDALONE_NAME + ": " + line);
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

	private void runProBClassic(final String probBinary, final String modelFile) {
		Process process = null;
		try {
			final String command = probBinary + " " + modelFile;
			process = Runtime.getRuntime().exec(command);
			final BufferedReader output = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			new Thread(new ClassicConsole(output)).start();

		} catch (IOException e) {
			Logger.notifyUserWithoutBugreport("You need to specify a correct location for "
			+ PROB_CLASSIC_NAME + ". See Preferences -> ProB Standalone.\n"
			+ PROB_CLASSIC_NAME + " location: "+ probBinary + 
			 "\nModel file: " + modelFile +
			 "\nError message: "+ e.getLocalizedMessage());
		}
	}

	private void runProB2(final String probBinary, final String modelFile) {
	 // call prob2-ui jar file
	 // from command-line it is: java -jar prob2-ui-1.0.1-SNAPSHOT-all.jar --machine-file  FILE
		Process process = null;
		try {
			final String command = "java -jar " + probBinary + " --machine-file " + modelFile;
			process = Runtime.getRuntime().exec(command);
			final BufferedReader output = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			new Thread(new ClassicConsole(output)).start();
			
			// TODO: maybe use
			//ProcessBuilder pb = new ProcessBuilder("/path/to/java", "-jar", probBinary, "--machine-file",modelFile);
            // pb.directory(new File("preferred/working/directory"));
            // Process p = pb.start();

		} catch (IOException e) {
			Logger.notifyUserWithoutBugreport("You need to specify a correct location for "
			+ PROB2_NAME + ". See Preferences -> ProB Standalone.\n"
			+ PROB2_NAME + " location: "+ probBinary + 
			 "\nModel file: " + modelFile +
			 "\nError message: "+ e.getLocalizedMessage());
		}
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
			Logger.notifyUserWithoutBugreport("Something went wrong while saving temp file.\n"
					+ e.getLocalizedMessage());
		}
		return temp;
	}

	private IMachineRoot getSelection() {
		if (!(fSelection instanceof IStructuredSelection))
			return null;
		final IStructuredSelection ssel = (IStructuredSelection) fSelection;
		if (ssel.size() != 1)
			return null;
		if (!(ssel.getFirstElement() instanceof IMachineRoot))
			return null;
		return (IMachineRoot) ssel.getFirstElement();
	}

}
