package de.prob.standalone;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction helpContentsAction;
	private IWorkbenchAction helpSearchAction;
	private IWorkbenchAction dynamicHelpAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction openPreferencesAction;
	private IWorkbenchAction deleteAction;
	private IWorkbenchAction copyAction;
	private IWorkbenchAction pasteAction;

	public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);
		register(helpContentsAction);

		helpSearchAction = ActionFactory.HELP_SEARCH.create(window);
		register(helpSearchAction);

		dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);
		register(dynamicHelpAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		// aboutAction
		// .setImageDescriptor(IDEInternalWorkbenchImages
		// .getImageDescriptor(IDEInternalWorkbenchImages.IMG_OBJS_DEFAULT_PROD));
		register(aboutAction);

		openPreferencesAction = ActionFactory.PREFERENCES.create(window);
		register(openPreferencesAction);

		deleteAction = ActionFactory.DELETE.create(window);
		register(deleteAction);

		copyAction = ActionFactory.COPY.create(window);
		register(copyAction);
		pasteAction = ActionFactory.PASTE.create(window);
		register(pasteAction);

	}

	@Override
	protected void fillMenuBar(final IMenuManager menuBar) {
		menuBar.add(createFileMenu());
		menuBar.add(createHelpMenu());
	}

	private MenuManager createFileMenu() {
		MenuManager menu = new MenuManager("File",
				IWorkbenchActionConstants.M_FILE);
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		return menu;
	}

	private MenuManager createHelpMenu() {
		MenuManager menu = new MenuManager("Help",
				IWorkbenchActionConstants.M_HELP);
		addSeparatorOrGroupMarker(menu, "group.intro"); //$NON-NLS-1$
		// FIXME Intro?

		// See if a welcome or intro page is specified
		// if (introAction != null) {
		// menu.add(introAction);
		// } else if (quickStartAction != null) {
		// menu.add(quickStartAction);
		// }
		menu.add(new GroupMarker("group.intro.ext")); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.main"); //$NON-NLS-1$
		menu.add(helpContentsAction);
		menu.add(helpSearchAction);
		menu.add(dynamicHelpAction);
		addSeparatorOrGroupMarker(menu, "group.assist"); //$NON-NLS-1$
		// See if a tips and tricks page is specified
		// if (tipsAndTricksAction != null) {
		// menu.add(tipsAndTricksAction);
		// }
		// HELP_START should really be the first item, but it was after
		// quickStartAction and tipsAndTricksAction in 2.1.
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
		menu.add(new GroupMarker("group.main.ext")); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.tutorials"); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.tools"); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.updates"); //$NON-NLS-1$
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
		addSeparatorOrGroupMarker(menu, IWorkbenchActionConstants.MB_ADDITIONS);
		// about should always be at the bottom
		menu.add(new Separator("group.about")); //$NON-NLS-1$

		ActionContributionItem aboutItem = new ActionContributionItem(
				aboutAction);
		aboutItem.setVisible(!Util.isMac());
		menu.add(aboutItem);
		menu.add(new GroupMarker("group.about.ext")); //$NON-NLS-1$
		return menu;
	}

	private void addSeparatorOrGroupMarker(final MenuManager menu,
			final String groupId) {
		//		String prefId = "useSeparator." + menu.getId() + "." + groupId; //$NON-NLS-1$ //$NON-NLS-2$
		boolean addExtraSeparators = true;
		// IDEWorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(prefId);
		if (addExtraSeparators) {
			menu.add(new Separator(groupId));
		} else {
			menu.add(new GroupMarker(groupId));
		}
	}

}
