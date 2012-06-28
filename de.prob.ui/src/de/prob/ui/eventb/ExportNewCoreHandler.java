package de.prob.ui.eventb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.Project;
import org.eventb.emf.persistence.ProjectResource;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.rodinp.core.IRodinProject;

import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;
import de.prob.logging.Logger;

public class ExportNewCoreHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IEventBRoot root = getSelectedEventBRoot(event);
		if (root != null) {
			final Preferences prefs = Platform.getPreferencesService()
					.getRootNode().node(InstanceScope.SCOPE)
					.node("prob_export_preferences");
			final Shell shell = HandlerUtil.getActiveShell(event);
			final String fileName = askForExportFile(prefs, shell, root);
			if (fileName != null) {
				exportToClassic(fileName, root);
			}
		}
		return null;
	}

	private IEventBRoot getSelectedEventBRoot(final ExecutionEvent event) {
		final ISelection fSelection = HandlerUtil.getCurrentSelection(event);
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1
					&& ssel.getFirstElement() instanceof IEventBRoot) {
				root = (IEventBRoot) ssel.getFirstElement();
			}
		}
		return root;
	}

	private String askForExportFile(final Preferences prefs, final Shell shell,
			final IEventBRoot root) {
		final String path = prefs.get("dir", System.getProperty("user.home"));

		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.eventb" });

		dialog.setFilterPath(path);
		final String subext = (root instanceof IMachineRoot) ? "_mch" : "_ctx";
		dialog.setFileName(root.getComponentName() + subext + ".eventb");
		String result = dialog.open();
		if (result != null) {
			final String newPath = dialog.getFilterPath();
			if (!path.equals(newPath)) {
				prefs.put("dir", newPath);
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					// Ignore, if preferences are not stored correctly we simply
					// ignore it (annoying, but not critical)
				}
			}
			if (!result.endsWith(".eventb")) {
				result += ".eventb";
			}
		}
		return result;
	}

	public static void exportToClassic(final String filename,
			final IEventBRoot root) {
		final boolean isSafeToWrite = isSafeToWrite(filename);

		IRodinProject rodinProject = root.getRodinProject();
		ProjectResource resource = new ProjectResource(rodinProject);
		try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Project project = (Project) resource.getContents().get(0);

		if (isSafeToWrite) {
			Writer fw = null;
			try {
				fw = new FileWriter(filename);
				TranslatorFactory.translate(root, new PrintWriter(fw));
				fw.append('\n');

				fw.append("emf_model('" + root.getComponentName() + "',\""
						+ serialize(project) + "\").");

			} catch (TranslationFailedException e) {
				e.notifyUserOnce();
			} catch (IOException e) {
				Logger.notifyUser("Unable to create file '" + filename + "'");
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	private static String serialize(Project project) {

		StringWriter sw = new StringWriter();
		Map<Object, Object> options = new HashMap<Object, Object>();
		options.put(XMLResource.OPTION_ROOT_OBJECTS,
				Collections.singletonList(project));
		options.put(XMLResource.OPTION_FORMATTED, false);
		XMLResourceImpl ri = new XMLResourceImpl();
		try {
			ri.save(sw, options);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String xml = Base64.encodeBase64String(sw.toString().getBytes());
		return xml;
	}

	private static boolean isSafeToWrite(final String filename) {
		if (new File(filename).exists()) {
			final MessageDialog dialog = new MessageDialog(null, "File exists",
					null, "The file exists. Do you want to overwrite it?",
					MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0);
			return dialog.open() == 0;
		} else
			return true;
	}
}
