/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceLabelProvider;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.IEventBRoot;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;

import de.prob.core.Animator;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * A dialog similar to the <code>LaunchConfigurationsDialog</code> for managing
 * custom configurations to animate machines
 * 
 * @see PreferenceDialog
 * @author Lukas Diekmann
 */
public class AnimationPreferencesDialog extends PreferenceDialog {

	private Preferences configurationStorage;

	public static final String pluginId = "de.prob.ui";
	public static final String default_configuration_name = "General Configuration";
	public static final String preferencesName = "prob_configurations";
	public static final String okButtonText = "Animate";
	public static final String nodeTitleKey = "title";
	public static final String nodeTitleKeyError = "TitleNotFound";
	public static final String proBParentNodeId = "ProB Configurations";

	private static final String shellTitle = "Manage ProB Configurations";
	private static final String flushingErrorMessage = "Problem while storing configurations. ";
	private static final String loadConfigurationsError = "Problem while loading configurations. ";
	private static final String readingErrorMessage = "Problem while reading configuration. ";

	private IEventBRoot selectedEventBRoot;

	public AnimationPreferencesDialog(final Shell parentShell,
			final PreferenceManager manager) {
		super(parentShell, manager);
		getPreferenceManager().removeAll();
		addProBParentNode();
	}

	@Override
	public void updateButtons() {
		getButton(IDialogConstants.OK_ID).setEnabled(isCurrentPageValid());
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(shellTitle);
	}

	/**
	 * When a configuration name has been changed, update page title and refresh
	 * TreeViewer
	 */
	@Override
	public void updateTitle() {
		super.updateTitle();
		getTreeViewer().refresh();
	}

	/**
	 * Get the configuration that was selected last when the dialog closed.
	 * 
	 * @return
	 */
	public Preferences getSelectedConfiguration() {
		return getConfigurationStorage().node(getSelectedNodePreference());
	}

	/**
	 * Adds a new composite that divides the left dialog area into two rows and
	 * inserts a <code>ToolBar</code> for creating, duplicating and deleting
	 * <code>ProBAnimationPreferences</code>
	 * 
	 * @see org.eclipse.jface.preference.PreferenceDialog#createTreeAreaContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createTreeAreaContents(final Composite parent) {

		// add Composite to divide area
		Composite treeAreaContentsDivider = new Composite(parent, SWT.NONE);

		// set Layout, LayoutData and Color of the Composite
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = gl.marginWidth = 0;
		treeAreaContentsDivider.setLayout(gl);
		treeAreaContentsDivider.setBackground(parent.getBackground());
		GridData gd = new GridData(GridData.FILL_BOTH);
		treeAreaContentsDivider.setLayoutData(gd);

		// TODO: use ToolBarManager
		// Create ToolBar and set Layout

		ToolBar tb = new ToolBar(treeAreaContentsDivider, SWT.SHADOW_ETCHED_IN);
		tb.setLayoutData(new GridData(SWT.HORIZONTAL));

		// add ToolBar Items
		ToolItem tiNew = new ToolItem(tb, SWT.PUSH);
		tiNew.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_FILE));
		tiNew.setToolTipText("Create new configuration");
		tiNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				addPreference("New Configuration", false);
			}
		});

		ToolItem tiDup = new ToolItem(tb, SWT.PUSH);
		tiDup.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_COPY));
		tiDup.setToolTipText("Duplicate selected configuration");
		tiDup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				duplicatePreference();
			}
		});

		ToolItem tiDel = new ToolItem(tb, SWT.PUSH);
		tiDel.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		tiDel.setToolTipText("Delete selected configuration");
		tiDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				deletePreference();
			}
		});

		// Build the tree and put it into the composite.
		// inherited from PreferencesDialog
		setTreeViewer(createTreeViewer(treeAreaContentsDivider));
		getTreeViewer().setInput(getPreferenceManager());
		updateTreeFont(JFaceResources.getDialogFont());

		// set TreeViewer layout to fill the Composite
		getTreeViewer().getControl().setLayoutData(
				new GridData(GridData.FILL_BOTH));

		// set a custom LabelProvider (to show icons) and the custom Comparator
		// (for sorting elements)
		getTreeViewer().setLabelProvider(new ProBLabelProvider());
		getTreeViewer().setComparator(new ViewerComparator());
		getTreeViewer().expandAll();

		layoutTreeAreaControl(treeAreaContentsDivider);
		return treeAreaContentsDivider;
	}

	/**
	 * Changes the text of the okButton to "Animate"
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		super.getShell().getDefaultButton().setText(okButtonText);
	}

	/**
	 * Creates a new composite used as TitleArea similar to the TitleArea in
	 * <code>TitleAreaDialog</code> and <code>LaunchConfigurationsDialog</code>
	 * 
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {

		// define some default values
		Display display = getParentShell().getDisplay();
		Color bgColor = JFaceColors.getBannerBackground(display);

		// Create TitleArea
		Composite newTitleArea = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = gl.marginWidth = 0;
		gl.numColumns = 2;
		gl.marginLeft = 5;
		newTitleArea.setLayout(gl);
		newTitleArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newTitleArea.setBackground(bgColor);

		// add TitleMessage
		Label titleMessage = new Label(newTitleArea, SWT.NONE);
		titleMessage.setText("Create, manage and run ProB configurations");
		titleMessage.setFont(JFaceResources.getBannerFont());
		titleMessage.setBackground(bgColor);

		Label titleImageLabel = new Label(newTitleArea, SWT.NONE);
		titleImageLabel.setAlignment(SWT.RIGHT);
		titleImageLabel.setBackground(bgColor);
		ImageDescriptor imageDescriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(pluginId,
						"icons/prob_animation_dialog.png");
		if (imageDescriptor != null) {
			titleImageLabel.setImage(imageDescriptor.createImage());
		}
		titleImageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label titleBarSeparator = new Label(parent, SWT.HORIZONTAL
				| SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return super.createDialogArea(parent);
	}

	/**
	 * Adds a new <code>ProBAnimationPreference</code> to the Dialog and stores
	 * it in the persistent store
	 * 
	 * @param name
	 *            of the new configuration
	 * @param duplicate
	 *            true if the new configuration should duplicate the settings
	 *            from another configuration
	 */
	private void addPreference(final String name, final boolean duplicate) {

		// look if the name is already used and
		// add a number to the name in that case
		String nodeTitle = getValidName(name);
		if (nodeTitle != null) {

			// create the new configuration
			String uniqueId = UUID.randomUUID().toString();
			Preferences newPreferences = getConfigurationStorage().node(
					uniqueId);

			// Duplicate keys
			if (duplicate) {
				try {
					for (String s : getSelectedConfiguration().keys()) {
						newPreferences.put(s,
								getSelectedConfiguration().get(s, null));
					}
				} catch (BackingStoreException e) {
					notifyBackingStoreError(readingErrorMessage, e);
				}
			} else {
				if (selectedEventBRoot != null) {
					newPreferences.put("project", selectedEventBRoot
							.getEventBProject().toString());
					newPreferences.put("machine", selectedEventBRoot
							.getRodinFile().getElementName());
				}
			}

			// add Title (overwrite inherited title if duplicated)
			newPreferences.put("title", nodeTitle);

			// Create PreferencePage and save it at once (in case the
			// user hits cancel after adding a new node)
			ProBAnimationPreferencePage preferencePage = new ProBAnimationPreferencePage(
					uniqueId, getConfigurationStorage());
			preferencePage.performOk();

			// add the PreferncePage to the TreeView
			PreferenceNode node = new PreferenceNode(uniqueId, preferencePage);
			getPreferenceManager().addTo(proBParentNodeId, node);

			getTreeViewer().refresh();
			setSelectedNodePreference(preferencePage.getConfigurationId());
			selectSavedItem();
		}
	}

	/**
	 * Creates a duplicate of the selected configuration
	 */
	private void duplicatePreference() {
		String name;
		if (getSelectedConfiguration() == null) {
			name = default_configuration_name;
			addPreference(name, false);
		} else {
			name = getSelectedConfiguration().get("title", null);
			addPreference(name, true);
		}
	}

	/**
	 * Deletes the selected configuration and automatically selects the next
	 * configuration.
	 */
	private void deletePreference() {
		if (getSelectedPage() instanceof ProBAnimationPreferencePage) {
			ProBAnimationPreferencePage deletePage = (ProBAnimationPreferencePage) getSelectedPage();
			String deleteId = deletePage.getConfigurationId();

			try {
				getConfigurationStorage().node(deleteId).removeNode();

			} catch (BackingStoreException e) {
				// do nothing
			} finally {

				saveConfigurations();

				// get selected Item and find its parent
				TreeItem selectedItem = getTreeViewer().getTree()
						.getSelection()[0];
				TreeItem parent = selectedItem.getParentItem();

				// Find next item to be selected
				int nextIndex = -1;
				int currentIndex = parent.indexOf(selectedItem);
				if (currentIndex < parent.getItemCount() - 1) {
					nextIndex = currentIndex;
				} else if (currentIndex > 0) {
					nextIndex = currentIndex - 1;
				}

				// remove page from PreferenceManager
				getPreferenceManager().find(parent.getText()).remove(deleteId);

				getTreeViewer().refresh();

				// select next Page
				if (parent.getItemCount() > 0) {
					PreferenceNode nextNode = (PreferenceNode) parent.getItem(
							nextIndex).getData();
					setSelectedNodePreference(nextNode.getId());
					selectSavedItem();
				} else {
					setSelectedNode(parent.getText());
					selectSavedItem();
				}
			}
		}
	}

	/**
	 * Checks if a node with this name already exists and returns a new name if
	 * necessary.
	 * 
	 * @param name
	 *            original name
	 * @return a safe name
	 */
	private String getValidName(final String name) {

		String bareName = name.replaceAll(" \\([0-9]+\\)", "");
		String safeName = name;

		try {

			// get all numbers that are not available
			ArrayList<Integer> numbers = new ArrayList<Integer>();
			for (String s : getConfigurationStorage().childrenNames()) {
				String title = getConfigurationStorage().node(s).get("title",
						null);
				if (title != null
						&& title.replaceAll(" \\([0-9]+\\)", "").equals(
								bareName)) {

					// get number from title
					char ch = title.charAt(title.length() - 2);
					if (Character.isDigit(ch)) {
						numbers.add(Character.getNumericValue(ch));
					} else {
						numbers.add(0);
					}
				}
			}

			// find an available number
			int count = 0;
			while (numbers.contains(count)) {
				count++;
			}

			// create name
			if (count > 0) {
				safeName = bareName + " (" + count + ")";
			}
		} catch (BackingStoreException e) {
			notifyBackingStoreError(readingErrorMessage, e);
			return null;
		}

		return safeName;
	}

	/**
	 * Sets the element that was selected before opening the dialog. Needed for
	 * setting an initial project and machine when creating a new configuration.
	 * 
	 * @param element
	 */
	public void setSelectedEventBRoot(final IEventBRoot element) {
		selectedEventBRoot = element;
	}

	/**
	 * Adds the parent node for the ProB configurations.
	 */
	public void addProBParentNode() {
		InfoPage infoPage = new InfoPage();
		infoPage.setTitle(proBParentNodeId);
		PreferenceNode proBParentNode = new PreferenceNode(proBParentNodeId,
				infoPage);
		getPreferenceManager().addToRoot(proBParentNode);
	}

	/**
	 * loads all user defined <code>ProBAnimationPreferencePages</code> to show
	 * them in the TreeViewer of the <code>ProBAnimationPreferencesDialog</code>
	 */
	public void loadConfigurations() {

		Preferences preferences = getConfigurationStorage();
		try {
			for (String nodeName : preferences.childrenNames()) {
				ProBAnimationPreferencePage preference = new ProBAnimationPreferencePage(
						nodeName, getConfigurationStorage());
				PreferenceNode node = new PreferenceNode(nodeName, preference);
				// getPreferenceManager().addToRoot(node);
				getPreferenceManager().addTo(proBParentNodeId, node);
			}
		} catch (BackingStoreException e) {
			notifyBackingStoreError(loadConfigurationsError, e);
		}
	}

	/**
	 * returns the ConfigurationScope where the data is stored
	 */
	public Preferences getConfigurationStorage() {
		if (configurationStorage == null) {
			configurationStorage = Platform.getPreferencesService()
					.getRootNode().node(InstanceScope.SCOPE)
					.node(preferencesName);
		}
		return configurationStorage;
	}

	public void saveConfigurations() {
		try {
			getConfigurationStorage().flush();
		} catch (BackingStoreException e) {
			notifyBackingStoreError(flushingErrorMessage, e);
		}
	}

	public void notifyBackingStoreError(final String m, final Exception e) {
		final String message = m + e.getLocalizedMessage();
		Logger.notifyUser(message, e);
	}

	/**
	 * Adds an icon to the elements of the <code>TreeViewer</code>
	 * 
	 */
	private static class ProBLabelProvider extends PreferenceLabelProvider {
		@Override
		public Image getImage(final Object element) {
			return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId,
					"icons/prob.png").createImage();
		}
	}

	/**
	 * Opens the ConfigurationDialog for animating machines.
	 * 
	 * @param currentSelection
	 *            : defines the machine and its project; used as default when
	 *            adding a new configuration
	 */
	public static void openAndAnimate(final IEventBRoot currentSelection) {
		Shell shell = new Shell();
		AnimationPreferencesDialog dialog = new AnimationPreferencesDialog(
				shell, new PreferenceManager());

		dialog.loadConfigurations();
		if (currentSelection != null) {
			dialog.setSelectedEventBRoot(currentSelection);
		}

		if (dialog.open() == AnimationPreferencesDialog.OK) {

			Preferences selConfig = dialog.getSelectedConfiguration();

			// cancel if no configuration page was selected
			if (selConfig == null)
				return;

			// get project and machine
			String projectName = selConfig.get("project", "");
			String machineName = selConfig.get("machine", "");

			/* get the machine-file that has to be animated */
			IInternalElement animateElement = null;
			if (!projectName.equals("")
					&& ResourcesPlugin.getWorkspace().getRoot()
							.getProject(projectName).exists()) {
				if (!machineName.equals("")
						&& ResourcesPlugin.getWorkspace().getRoot()
								.getProject(projectName).getFile(machineName)
								.exists()) {
					IFile animateFile = ResourcesPlugin.getWorkspace()
							.getRoot().getProject(projectName)
							.getFile(machineName);
					animateElement = RodinCore.valueOf(animateFile).getRoot();
				}
			}

			/* if machine cannot be found, show error message and throw error */
			if (animateElement == null) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR
						| SWT.OK);
				messageBox.setText("Error");
				messageBox.setMessage("Cannot animate machine: Project '"
						+ projectName + "' or machine '" + machineName
						+ "' do not exist.");
				messageBox.open();

				throw new UnsupportedOperationException(
						"Cannot animate machine: Project '" + projectName
								+ "' or machine '" + machineName
								+ "' do not exist.");
			}

			/*
			 * if everything is fine, get Animator and try to animate the
			 * machine
			 */
			final Animator animator = Animator.getAnimator();

			// tell animator which configuration to use
			animator.setCustomConfiguration(selConfig);

			try {
				LoadEventBModelCommand.load(animator,
						(IEventBRoot) animateElement);
			} catch (ProBException e) {
				// We cannot recover from this, but the user has been informed
				return;
			}
		}
	}

	/**
	 * A PreferencePage for ParentNodes to display some information.
	 * 
	 */
	private static class InfoPage extends PreferencePage {

		@Override
		protected Control createContents(final Composite parent) {

			// remove buttons
			this.noDefaultAndApplyButton();

			Label lbInfo = new Label(parent, SWT.NONE);
			lbInfo.setText("Configure animation settings from this dialog:");

			CLabel lbNew = new CLabel(parent, SWT.NONE);
			lbNew.setImage(PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FILE));
			lbNew.setText("- Press the 'New' button to create a new configuration.");

			CLabel lbDup = new CLabel(parent, SWT.LEFT);
			lbDup.setText("- Press the 'Duplicate' button to copy the selected configuration.");
			lbDup.setImage(PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_TOOL_COPY));

			CLabel lbDel = new CLabel(parent, SWT.NONE);
			lbDel.setImage(PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_TOOL_DELETE));
			lbDel.setText("- Press the 'Delete' button to remove the selected configuration.");

			Label lbAdditionalInfo = new Label(parent, SWT.NONE);
			lbAdditionalInfo
					.setText("There is also a general setting for animating machines under Preferences->ProB.");

			return parent;
		}

		@Override
		public boolean isValid() {
			// This Page is never valid
			return true;
		}

	}

}
