/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioEditor;
import de.bmotionstudio.gef.editor.ILanguageService;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.Animator;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;

public class VisualizationProgressBar extends ProgressBarDialog {

	private Animator animator;
	private IFile f;
	private Visualization visualization;
	private Animation animation;
	private int confirm = -1;
	private SelectOperationDialog dialog;
	private BMotionStudioEditor activeEditor;

	public VisualizationProgressBar(Shell parent, Animator animator,
			BMotionStudioEditor activeEditor, IFile f) {
		super(parent);
		this.animator = animator;
		this.activeEditor = activeEditor;
		this.f = f;
	}

	@Override
	public void initGuage() {
		this.setExecuteTime(6);
		this.setMayCancel(true);
		this.setProcessMessage("Starting Visualization ...");
		this.setShellTitle("Starting Visualization");
	}

	@Override
	protected String process(int i) {

		switch (i) {
		case 1:
			try {
				createVisualizationRoot();
			} catch (CoreException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			} catch (IOException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			} catch (ParserConfigurationException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			} catch (SAXException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			}
			return "Starting ProB Animator";
		case 2:
			startProbAnimator();
			return "Setup Constants";
		case 3:
			try {
				setupOperation("SETUP_CONTEXT");
			} catch (InterruptedException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			}
			return "Create Visualization";
		case 4:
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					createShell();
				}
			});
			return "Initialize machine";
		case 5:
			try {
				setupOperation("INITIALISATION");
			} catch (InterruptedException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			}
			return "Starting Visualization";
		}

		return "Starting BMotion Studio Visualization";

	}

	// private void startOperationScheduler() {
	// visualization.startOperationScheduler();
	// }

	private void createVisualizationRoot() throws CoreException, IOException,
			ParserConfigurationException, SAXException {
		XStream xstream = new XStream(new DomDriver()) {
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					@Override
					public boolean shouldSerializeMember(
							@SuppressWarnings("rawtypes") Class definedIn,
							String fieldName) {
						if (definedIn == Object.class) {
							return false;
						}
						return super
								.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};
		BMotionEditorPlugin.setAliases(xstream);
		visualization = (Visualization) xstream.fromXML(f.getContents());
		visualization.setProjectFile(f);
	}

	@Override
	protected void cleanUp() {
		if (animation != null)
			animation.unregister();
	}

	private void startProbAnimator() {
		animation = new Animation(animator, visualization);
		ILanguageService langService = getGenericLoadMachine(visualization
				.getLanguage());
		if (langService != null) {
			try {
				langService.startProBAnimator(visualization);
			} catch (ProBException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			}
		} else {
			openErrorDialog("Unknown formal language: "
					+ visualization.getLanguage());
			setClose(true);
		}
	}

	private void setupOperation(String opName) throws InterruptedException {

		final List<Operation> ops = animation.getState().getEnabledOperations();

		if (ops.size() > 1) {

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					dialog = new SelectOperationDialog(getShell(), ops);
					confirm = dialog.open();
				}
			});

			while (true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					openErrorDialog(e.getMessage());
					setClose(true);
				}

				if (confirm == Window.OK) {
					Operation op = dialog.getSelectedOperation();
					if (op != null)
						try {
							ExecuteOperationCommand.executeOperation(animator,
									op);
						} catch (ProBException e) {
							openErrorDialog(e.getMessage());
							setClose(true);
						}
					confirm = -1;
					break;
				} else if (confirm == Window.CANCEL) {
					setClose(true);
					confirm = -1;
					break;
				}

			}

		} else {
			Operation op = animation.getCurrentStateOperation(opName);
			if (op != null)
				try {
					ExecuteOperationCommand.executeOperation(animator, op);
				} catch (ProBException e) {
					openErrorDialog(e.getMessage());
					setClose(true);
				}
		}

		visualization.setIsRunning(true);

	}

	private void createShell() {
		activeEditor.createRunPage(visualization, animation);
	}

	private ILanguageService getGenericLoadMachine(String language) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.gef.editor.language");
		ILanguageService langService = null;

		// Get GenericLoadMachine command for language
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("language".equals(configurationElement.getName())) {

					String languageEx = configurationElement.getAttribute("id");
					if (language.toLowerCase(Locale.ENGLISH).equals(
							languageEx.toLowerCase(Locale.ENGLISH))) {

						try {
							langService = (ILanguageService) configurationElement
									.createExecutableExtension("service");
						} catch (final CoreException e) {
							openErrorDialog(e.getMessage());
							setClose(true);
						}

					}

				}

			}
		}
		return langService;
	}

	public void kill() {
		// if (shell != null)
		// if (shell.getShell() != null)
		// shell.getShell().dispose();
	}

	public void openErrorDialog(final String msg) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(getParent(), "Error",
						"Error creating visualization", new Status(
								IStatus.ERROR, BMotionEditorPlugin.PLUGIN_ID,
								msg));
			}
		});
	}

}