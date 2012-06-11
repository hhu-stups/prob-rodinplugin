/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.ILanguageService;
import de.bmotionstudio.gef.editor.model.Visualization;

public class NewBMotionProjectWizardPage extends WizardPage {

	private static final String DEFAULT_PROJECT_NAME = "NewBMotionVisualization";

	private IProject selectedProject;
	private final MachineList machineList = new MachineList();

	private MachineEntry selectedEntry = null;
	private Text projectRootText;
	private Text projectText;
	private TableViewer tableViewer;

	private Map<String, ILanguageService> languages = new HashMap<String, ILanguageService>();

	protected NewBMotionProjectWizardPage(IStructuredSelection selection) {
		super("wizardPage");
		this.selectedProject = getSelectedFromSelection(selection);
		setTitle("New BMotion Studio Visualization");
		setDescription("Please select a B-Machine and enter a name for the new BMotion Studio Visualization.");
		setImageDescriptor(BMotionStudioImage
				.getImageDescriptor("icons/logo_bmotion_64.png"));
		initLanguageExtensions();
	}

	private void initLanguageExtensions() {
		// Get language loader
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.gef.editor.language");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				try {
					ILanguageService lang = (ILanguageService) configurationElement
							.createExecutableExtension("service");
					languages
							.put(configurationElement.getAttribute("id"), lang);
				} catch (CoreException e) {
					e.printStackTrace();
				}

			}
		}
	}

	private IProject getSelectedFromSelection(IStructuredSelection selection) {
		if (selection.size() == 1) {
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IProject) {
				return (IProject) firstElement;
			}
		}
		return null;
	}

	public IProject getProject() {
		return selectedProject;
	}

	public InputStream getInitialContents(String fileExtension)
			throws UnsupportedEncodingException {
		Visualization visualization = new Visualization(getSelectedEntry()
				.getMachineFile().getName(), getSelectedEntry()
				.getMachineLanguage(), Platform
				.getBundle(BMotionEditorPlugin.PLUGIN_ID).getHeaders()
				.get("Bundle-Version"));
		XStream xstream = new XStream();
		BMotionEditorPlugin.setAliases(xstream);
		String content = xstream.toXML(visualization);
		return new ByteArrayInputStream(content.getBytes("UTF-8"));
	}

	public String getFileName() {
		if (projectText.getText().length() > 0) {
			return projectText.getText();
		}
		return DEFAULT_PROJECT_NAME;
	}

	public MachineEntry getSelectedEntry() {
		return this.selectedEntry;
	}

	public String getProjectRoot() {
		return this.projectRootText.getText();
	}

	public String getBMotionProjectName() {
		return this.projectText.getText();
	}

	private void initContent() {

		machineList.clearList();

		if (selectedProject != null) {

			String basePath = (selectedProject.getLocation().toString())
					.replace("file:", "");
			File dir = new File(basePath);

			for (File f : dir.listFiles()) {

				for (Entry<String, ILanguageService> e : languages.entrySet()) {

					String langID = e.getKey();
					ILanguageService langService = e.getValue();

					IPath path = new Path(f.getAbsolutePath());
					IFile ifile = ResourcesPlugin.getWorkspace().getRoot()
							.getFile(path);
					if (langService.isLanguageFile(ifile)) {
						machineList.addEntry(new MachineEntry(ifile, langID));
					}

				}

			}

		}

		tableViewer.setInput(machineList.getChildren());

	}

	private boolean resourceExistsInProject(String resourceName) {
		IFile f = selectedProject.getFile(resourceName + "."
				+ BMotionEditorPlugin.FILEEXT_STUDIO);
		return f.exists();
	}

	public void validateInput() {
		StringBuffer errorMessages = new StringBuffer(150);

		if (getProjectRoot().length() == 0) {
			errorMessages.append("The Project name must not be empty.\n");
		}

		if (getBMotionProjectName().length() == 0) {
			errorMessages
					.append("The BMotion Studio Visualization filename must not be empty.\n");
		}

		// if (errorMessages.length() == 0) {
		// rodinProject = RodinCore.getRodinDB().getRodinProject(
		// getProjectRoot().replaceAll("/", ""));
		//
		// if (!rodinProject.exists()) {
		// errorMessages.append("The Project '" + getProjectRoot()
		// + "' does not exist.\n");
		// }
		// }

		if (getSelectedEntry() == null) {
			errorMessages.append("You have to select a Machine.\n");
		}

		if (errorMessages.length() == 0
				&& resourceExistsInProject(getBMotionProjectName())) {
			errorMessages
					.append("The BMotion Studio Visualization filename must be unique in a project.\n");
		}

		if (errorMessages.length() > 0) {
			setErrorMessage(errorMessages.toString());
			setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);

		final GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 3;
		layout.verticalSpacing = 20;

		setControl(container);

		final ModifyListener listener = new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				validateInput();
			}
		};

		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Project name:");

		projectRootText = new Text(container, SWT.BORDER | SWT.SINGLE);
		projectRootText.setLayoutData(gd);
		if (selectedProject != null) {
			projectRootText.setText(selectedProject.getFullPath().toOSString());
		}
		projectRootText.addModifyListener(listener);

		final Button button = new Button(container, SWT.NULL);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final ContainerSelectionDialog dialog = new ContainerSelectionDialog(
						getShell(), ResourcesPlugin.getWorkspace().getRoot(),
						false, "Select Project");
				if (dialog.open() == Window.OK) {
					final Object[] result = dialog.getResult();
					if (result.length == 1) {
						Path newPath = (Path) result[0];
						IProject newProject = selectedProject.getWorkspace()
								.getRoot().getProject(newPath.toString());
						selectedProject = newProject;
						initContent();
						projectRootText.setText(((Path) result[0]).toOSString());
						validateInput();
					}
				}
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&BMotion Studio Visualization filename:");

		projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		projectText.setText(DEFAULT_PROJECT_NAME);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(listener);

		gd = new GridData();
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 3;

		label = new Label(container, SWT.NULL);
		label.setText("&B-Machine:");
		label.setLayoutData(gd);

		gd = new GridData(GridData.FILL_VERTICAL);
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 3;

		tableViewer = new TableViewer(container, SWT.SINGLE
				| SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gd);

		final String[] columnsNames = new String[] { "Machine", "Language" };
		final int[] columnWidths = new int[] { 250, 100 };
		final int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT };

		for (int i = 0; i < columnsNames.length; i++) {
			final TableColumn tableColumn = new TableColumn(table,
					columnAlignments[i]);
			tableColumn.setText(columnsNames[i]);
			tableColumn.setWidth(columnWidths[i]);
		}

		tableViewer.setLabelProvider(new MachineLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		initContent();

		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						selectedEntry = (MachineEntry) selection
								.getFirstElement();
						validateInput();
					}
				});

		validateInput();

	}

	private static class MachineLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		public String getColumnText(final Object element, final int columnIndex) {
			if (element instanceof MachineEntry) {

				final MachineEntry currentEntry = (MachineEntry) element;

				switch (columnIndex) {
				case 0:
					return currentEntry.getMachineName();
				case 1:
					return currentEntry.getMachineLanguage();
				}

			}

			return null;
		}

	}

	private static class MachineList {
		private final ArrayList<MachineEntry> entries = new ArrayList<MachineEntry>();

		public MachineEntry[] getChildren() {
			return entries.toArray(new MachineEntry[entries.size()]);
		}

		public void addEntry(final MachineEntry entry) {
			if (!entries.contains(entry)) {
				entries.add(entry);
			}
		}

		public void clearList() {
			this.entries.clear();
		}

	}

	private static class MachineEntry {

		private final String machineName;
		private final String machineLanguage;
		private final IFile file;

		public MachineEntry(IFile file, String machineLanguage) {
			this.file = file;
			this.machineName = file.getName();
			this.machineLanguage = machineLanguage;
		}

		public String getMachineName() {
			return this.machineName;
		}

		public String getMachineLanguage() {
			return this.machineLanguage;
		}

		public IFile getMachineFile() {
			return this.file;
		}

	}

}
