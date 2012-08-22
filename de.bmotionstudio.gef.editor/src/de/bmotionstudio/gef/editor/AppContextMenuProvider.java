/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

import de.bmotionstudio.gef.editor.action.OpenSchedulerEventAction;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.part.AppAbstractEditPart;
import de.bmotionstudio.gef.editor.part.VisualizationPart;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;

public class AppContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	private IExtensionRegistry registry = Platform.getExtensionRegistry();

	private String[] eventIDs = { AttributeConstants.EVENT_MOUSECLICK };

	public AppContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {

		IAction action;

		GEFActionConstants.addStandardActionGroups(menu);

		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = actionRegistry.getAction(ActionFactory.COPY.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

		action = actionRegistry.getAction(ActionFactory.PASTE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		buildCustomMenu(menu);

		buildObserverMenu(menu);

		buildEventMenu(menu);

	}

	private void buildCustomMenu(IMenuManager menu) {

		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.gef.editor.installMenu");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("menu".equals(configurationElement.getName())) {

					try {

						IInstallMenu installMenuClass = (IInstallMenu) configurationElement
								.createExecutableExtension("class");

						installMenuClass.installMenu(menu, getActionRegistry());

					} catch (final CoreException e) {
						e.printStackTrace();
					}

				}

			}

		}

	}

	private void buildObserverMenu(IMenuManager menu) {

		final MenuManager handleObserverMenu = new MenuManager("Observer",
				BMotionStudioImage.getImageDescriptor(
						BMotionEditorPlugin.PLUGIN_ID,
						"icons/icon_observer.gif"), "observerMenu");
		menu.appendToGroup(GEFActionConstants.GROUP_ADD, handleObserverMenu);

		IStructuredSelection selection = (IStructuredSelection) BMotionEditorPlugin
				.getActiveEditor().getEditorSite().getSelectionProvider()
				.getSelection();

		if (selection.getFirstElement() instanceof AppAbstractEditPart) {

			BControl bcontrol = (BControl) ((AppAbstractEditPart) selection
					.getFirstElement()).getModel();

			IExtensionPoint extensionPoint = registry
					.getExtensionPoint("de.bmotionstudio.gef.editor.observer");
			for (IExtension extension : extensionPoint.getExtensions()) {
				for (IConfigurationElement configurationElement : extension
						.getConfigurationElements()) {

					if ("observer".equals(configurationElement.getName())) {

						final String observerClassName = configurationElement
								.getAttribute("class");
						final String observerName = configurationElement
								.getAttribute("name");

						if (checkIncludeObserver(observerClassName, bcontrol)) {

							IAction action = getActionRegistry().getAction(
									"de.bmotionstudio.gef.editor.observerAction."
											+ observerClassName);
							action.setText(observerName);
							action.setToolTipText(observerName);

							if (bcontrol.hasObserver(observerClassName)) {
								action.setImageDescriptor(BMotionStudioImage
										.getImageDescriptor(
												BMotionEditorPlugin.PLUGIN_ID,
												"icons/icon_chop.gif"));
							} else {
								action.setImageDescriptor(null);
							}
							handleObserverMenu.add(action);

						}

					}

				}

			}

		}

	}

	private boolean checkIncludeObserver(String observerID, BControl control) {

		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.gef.editor.includeObserver");

		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("include".equals(configurationElement.getName())) {

					String langID = configurationElement
							.getAttribute("language");

					if (langID != null
							&& langID.equals(control.getVisualization()
									.getLanguage())) {

						for (IConfigurationElement cObserver : configurationElement
								.getChildren("observer")) {

							String oID = cObserver.getAttribute("id");

							if (observerID.equals(oID)) {

								for (IConfigurationElement configBControl : cObserver
										.getChildren("control")) {

									String bID = configBControl
											.getAttribute("id");

									if (control.getType().equals(bID)) {
										return true;
									}

								}

							}

						}

					}

				}

			}
		}

		return false;

	}

	private void buildEventMenu(IMenuManager menu) {

		MenuManager handleEventMenu = new MenuManager("Events",
				BMotionStudioImage.getImageDescriptor(
						BMotionEditorPlugin.PLUGIN_ID, "icons/icon_event.png"),
				"eventMenu");
		menu.appendToGroup(GEFActionConstants.GROUP_ADD, handleEventMenu);

		IStructuredSelection selection = (IStructuredSelection) BMotionEditorPlugin
				.getActiveEditor().getEditorSite().getSelectionProvider()
				.getSelection();

		if ((selection.getFirstElement() instanceof AppAbstractEditPart)
				&& !(selection.getFirstElement() instanceof VisualizationPart)) {

			BControl bcontrol = (BControl) ((AppAbstractEditPart) selection
					.getFirstElement()).getModel();

			// Has event
			if (bcontrol.hasEvent(eventIDs[0])) {

				SchedulerEvent event = bcontrol.getEvent(eventIDs[0]);

				OpenSchedulerEventAction action = (OpenSchedulerEventAction) getActionRegistry()
						.getAction(
								"de.bmotionstudio.gef.editor.SchedulerEventAction."
										+ event.getID());
				action.setEventID(eventIDs[0]);
				action.setText(event.getName());
				action.setImageDescriptor(BMotionStudioImage
						.getImageDescriptor(BMotionEditorPlugin.PLUGIN_ID,
								"icons/icon_chop.gif"));
				handleEventMenu.add(action);

			} else { // Has no event

				IExtensionPoint schedulerExtensionPoint = registry
						.getExtensionPoint("de.bmotionstudio.gef.editor.schedulerEvent");
				for (IExtension schedulerExtension : schedulerExtensionPoint
						.getExtensions()) {
					for (IConfigurationElement configSchedulerElement : schedulerExtension
							.getConfigurationElements()) {

						if ("schedulerEvent".equals(configSchedulerElement
								.getName())) {

							String sClassName = configSchedulerElement
									.getAttribute("class");
							Boolean show = Boolean
									.valueOf(configSchedulerElement
											.getAttribute("menu"));

							if (show) {

								OpenSchedulerEventAction action = (OpenSchedulerEventAction) getActionRegistry()
										.getAction(
												"de.bmotionstudio.gef.editor.SchedulerEventAction."
														+ sClassName);
								action.setEventID(eventIDs[0]);
								action.setText(configSchedulerElement
										.getAttribute("name"));

								// if (bcontrol.hasEvent(eventIDs[0])) {
								// action
								// .setImageDescriptor(BMotionStudioImage
								// .getImageDescriptor(
								// BMotionEditorPlugin.PLUGIN_ID,
								// "icons/icon_chop.gif"));
								// } else {

								action.setImageDescriptor(null);

								// }

								handleEventMenu.add(action);

							}

						}

					}

				}

			}

		}

	}

	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}