/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.prob.ui.ProBGeneralPreferences;

/**
 * Provides an extension to <code>ProBGeneralPreferences</code> with an own
 * storage place and an extra field for changing the title.
 * 
 * @see ProBGeneralPreferences
 * @author Lukas Diekmann
 */
public class ProBAnimationPreferencePage extends ProBGeneralPreferences {

	private final static String TITLEERROR = "Unkown name";
	private final Preferences configurationScope;
	private final String configurationId;

	// Name Field
	private ConfigNameFieldEditor fdName;
	private MachineFieldEditor fdMachine;
	private ProjectFieldEditor fdProject;

	/**
	 * @param configurationName
	 *            titlename, also used for finding this config in the
	 *            configurationStore
	 * @param configurationStore
	 *            general storage path
	 */
	public ProBAnimationPreferencePage(final String configurationName,
			final Preferences configurationStore) {
		super(configurationStore.node(configurationName));
		this.configurationId = configurationName;
		this.configurationScope = configurationStore;
	}

	@Override
	protected Control createContents(final Composite parent) {

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("General Settings");

		// since the fields somehow destroy the margins of Group, use an
		// additional composite here
		Composite content1 = new Composite(group, SWT.NONE);
		content1.setLayout(new GridLayout(3, false));
		content1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fdName = new ConfigNameFieldEditor("configname", "Name:", content1);
		fdName.setPage(this);
		fdName.setPropertyChangeListener(this);
		fdName.fillIntoGrid(content1, 3);

		addField(fdName); // add to FieldEditor list for correct validation
		getPreferenceStore().setValue("configname",
				getConfigurationScope().node(configurationId).get("title", ""));
		getPreferenceStore().setDefault("configname", "");

		fdProject = new ProjectFieldEditor("projectname", "Project:", content1);
		fdProject.setPage(this);
		fdProject.setPropertyChangeListener(this);

		addField(fdProject); // add to FieldEditor list for correct validation
		getPreferenceStore().setValue(
				"projectname",
				getConfigurationScope().node(configurationId)
						.get("project", ""));
		getPreferenceStore().setDefault("projectname", "");

		fdMachine = new MachineFieldEditor("machinename", "Machine:", content1);
		fdMachine.setPage(this);
		fdMachine.setPropertyChangeListener(this);

		addField(fdMachine);
		getPreferenceStore().setValue(
				"machinename",
				getConfigurationScope().node(configurationId)
						.get("machine", ""));
		getPreferenceStore().setDefault("machinename", "");

		return super.createContents(parent);
	}

	@Override
	public boolean performOk() {
		if (fdName != null) {
			getConfigNode().put("title", fdName.getStringValue());
			getContainer().updateTitle();
		}

		// Save machine and project
		if (fdProject != null) {
			getConfigurationScope().node(configurationId).put("project",
					fdProject.getStringValue());
		}
		if (fdMachine != null) {
			getConfigurationScope().node(configurationId).put("machine",
					fdMachine.getStringValue());
		}

		// Save ProB Settings
		super.performOk();

		return true;
	}

	public String getConfigurationId() {
		return configurationId;
	}

	@Override
	public String getTitle() {

		String title = getConfigurationScope().node(configurationId).get(
				"title", TITLEERROR);
		if (title == TITLEERROR) {
			title = configurationId;
		}
		return title;
	}

	private Preferences getConfigNode() {
		return getConfigurationScope().node(configurationId);
	}

	private Preferences getConfigurationScope() {
		return configurationScope;
	}

	/**
	 * A FieldEditor with a StringField and a button, to select a machine from a
	 * certain project
	 * 
	 * @author Lukas Diekmann
	 * 
	 */
	private class MachineFieldEditor extends StringButtonFieldEditor {

		private String lastmachine = "";

		public MachineFieldEditor(final String name, final String labelText,
				final Composite parent) {
			init(name, labelText);
			setChangeButtonText(JFaceResources.getString("Search..."));
			setErrorMessage("Machine not specified.");
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			createControl(parent);
		}

		@Override
		protected String changePressed() {

			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(fdProject.getStringValue());
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					getShell(), new MachineLabelProvider());
			dialog.setTitle("Select Machine");
			dialog.setMessage("Select a machine:");

			try {
				ArrayList<IResource> resources = new ArrayList<IResource>();
				for (IResource r : project.members()) {
					if (r.getType() == IResource.FILE
							&& (r.getFileExtension().equals("bum") || r
									.getFileExtension().equals("buc"))) {
						resources.add(r);
					}
				}
				dialog.setElements(resources.toArray());

			} catch (CoreException e2) {
				// FIXME: display message?
			} finally {
				dialog.open();
				if (dialog.getResult() != null && dialog.getResult().length > 0) {
					lastmachine = ((IResource) dialog.getResult()[0]).getName();
				}
			}
			return lastmachine;
		}
	}

	private static class MachineLabelProvider extends LabelProvider {

		@Override
		public Image getImage(final Object element) {
			if (((IResource) element).getFileExtension().equals("bum")) {
				return AbstractUIPlugin.imageDescriptorFromPlugin(
						AnimationPreferencesDialog.pluginId,
						"icons/mch_obj.gif").createImage();
			} else if (((IResource) element).getFileExtension().equals("buc")) {
				return AbstractUIPlugin.imageDescriptorFromPlugin(
						AnimationPreferencesDialog.pluginId,
						"icons/ctx_obj.gif").createImage();
			}
			return null;
		}

		@Override
		public String getText(final Object element) {
			if (element instanceof IResource) {
				return ((IResource) element).getName();
			} else {
				return element == null ? "" : element.toString();
			}
		}
	}

	/**
	 * A field editor with a StringField and a Button, to select a project from
	 * the workspace
	 * 
	 * @author Lukas Diekmann
	 * 
	 */
	private static class ProjectFieldEditor extends StringButtonFieldEditor {

		private String lastproject;

		public ProjectFieldEditor(final String name, final String labelText,
				final Composite parent) {
			init(name, labelText);
			setChangeButtonText(JFaceResources.getString("Browse..."));
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			setErrorMessage("Project not specified.");
			createControl(parent);
		}

		@Override
		protected String changePressed() {
			ContainerSelectionDialog dialog = new ContainerSelectionDialog(
					getShell(), null, true, "Select a project:");
			dialog.setTitle("Select Project");
			dialog.open();
			if (dialog.getResult() != null && dialog.getResult().length > 0) {
				lastproject = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(dialog.getResult()[0].toString()).getName();
			}
			return lastproject;
		}

	}

	/**
	 * A <code>StringFieldEditor</code> extension with an additional validation
	 * method, checking for duplicate configuration names or empty strings
	 * 
	 * @author Lukas Diekmann
	 */
	private class ConfigNameFieldEditor extends StringFieldEditor {

		private static final String nameExistsError = "A configuration with this name already exists!";
		private static final String emptyStringError = "A name is required for this configuration!";

		public ConfigNameFieldEditor(final String name, final String labelText,
				final Composite parent) {
			super(name, labelText, UNLIMITED, VALIDATE_ON_KEY_STROKE, parent);
		}

		@Override
		protected boolean doCheckState() {

			if (getStringValue().equals(getConfigNode().get("title", null))) {
				return true;
			}

			if ("".equals(getStringValue())) {
				setErrorMessage(emptyStringError);
				return false;
			}

			boolean nameExists = false;
			// check if the name is used by another config
			try {
				for (String s : getConfigurationScope().childrenNames()) {

					Preferences node = getConfigurationScope().node(s);
					if (node.get("title", null) != null
							&& node.get("title", null).equals(getStringValue())) {
						nameExists = true;
					}

				}
			} catch (BackingStoreException e) {
				nameExists = true;
			}

			// return false if the name already exists in ANOTHER configuration
			if (getStringValue().equals(
					AnimationPreferencesDialog.default_configuration_name)
					|| (nameExists && !getStringValue().equals(
							getPreferenceStore().getDefaultString(
									getPreferenceName())))) {
				setErrorMessage(nameExistsError);
				return false;
			} else {
				return true;
			}
		}
	}

}
