package de.prob.ui.eventb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
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

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		fSelection = HandlerUtil.getCurrentSelection(event);

		final String location = getBinaryLocation();
		if (location == null) {
			Logger.notifyUserWithoutBugreport("You need to specify a location for the ProB Tcl/Tk version. See Preferences -> ProB Classic. Details: "+ e.getLocalizedMessage());
		} else {
			final IEventBRoot root = getSelection();
			if (root != null) {
				final File temp = createTempFile();
				final String tmp = temp.getAbsolutePath();
				ExportNewCoreHandler.exportToClassic(tmp, root);
				runProBClassic(location, tmp);
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
					System.out.println("ProB Classic: " + line);
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
			Logger.notifyUserWithoutBugreport("You need to specify a location for the ProB Tcl/Tk version. See Preferences -> ProB Classic. Detail: "+ e.getLocalizedMessage());
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
