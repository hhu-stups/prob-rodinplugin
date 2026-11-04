package de.prob.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;

import de.prob.core.Animator;
import de.prob.core.command.GetCommandsCommand;
import de.prob.core.command.GetCommandsCommand.Category;
import de.prob.core.command.GetCommandsCommand.CommandInfo;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

/**
 * Toolbar drop-down menus, one per group, for ProB's table or graph commands
 * (see {@link de.prob.core.command.GetCommandsCommand}). Unavailable
 * commands are shown greyed out, with the reason.
 */
public final class CommandMenus {

	/** called when a command is chosen in a menu */
	public interface Handler {
		void run(CommandInfo command);
	}

	private static final String GROUP_MARKER = "de.prob.ui.commandMenus";

	/** waits for further state changes, e.g. while executing random steps */
	private static final long UPDATE_DELAY_MS = 300;

	private final IViewSite site;
	private final String idPrefix;
	private final String kind;
	private final Map<String, String> groupLabels;
	private final Set<String> hiddenGroups;
	private final Set<String> hiddenCommands;
	private final Handler handler;
	private final List<Action> groupActions = new ArrayList<Action>();
	private List<CommandInfo> commands = Collections.emptyList();
	private Job updateJob;
	private volatile boolean disposed;

	/**
	 * @param idPrefix
	 *            for the ids of the menus' actions
	 * @param kind
	 *            for the tool tips, e.g. "tables"
	 * @param groupLabels
	 *            the menus' names for ProB's groups, in the order of the
	 *            menus; other groups follow, named after ProB's group (without
	 *            "_info" or "_visualisation")
	 * @param hiddenGroups
	 *            ProB's groups that are not offered
	 * @param hiddenCommands
	 *            ProB's commands that are not offered
	 */
	public CommandMenus(final IViewSite site, final String idPrefix,
			final String kind, final Map<String, String> groupLabels,
			final Set<String> hiddenGroups, final Set<String> hiddenCommands,
			final Handler handler) {
		this.site = site;
		this.idPrefix = idPrefix;
		this.kind = kind;
		this.groupLabels = groupLabels;
		this.hiddenGroups = hiddenGroups;
		this.hiddenCommands = hiddenCommands;
		this.handler = handler;
	}

	/** marks the place of the menus in the toolbar */
	public void addTo(final IToolBarManager toolBar) {
		toolBar.add(new Separator(GROUP_MARKER));
	}

	/** @return the commands from ProB, including hidden ones */
	public List<CommandInfo> getCommands() {
		return commands;
	}

	/** @return the command with this name, or <code>null</code> */
	public CommandInfo find(final String command) {
		for (CommandInfo c : commands)
			if (c.getCommand().equals(command))
				return c;
		return null;
	}

	/** shows the commands in the menus, replacing the previous ones */
	public void setCommands(final List<CommandInfo> commands) {
		this.commands = commands;
		final IToolBarManager toolBar = site.getActionBars()
				.getToolBarManager();
		for (Action action : groupActions)
			toolBar.remove(action.getId());
		groupActions.clear();

		final Map<String, List<CommandInfo>> groups = new LinkedHashMap<String, List<CommandInfo>>();
		for (String group : groupLabels.keySet())
			groups.put(group, new ArrayList<CommandInfo>());
		for (CommandInfo command : commands) {
			if (hiddenGroups.contains(command.getGroup())
					|| hiddenCommands.contains(command.getCommand()))
				continue;
			List<CommandInfo> list = groups.get(command.getGroup());
			if (list == null) {
				list = new ArrayList<CommandInfo>();
				groups.put(command.getGroup(), list);
			}
			list.add(command);
		}
		for (Map.Entry<String, List<CommandInfo>> group : groups.entrySet()) {
			if (group.getValue().isEmpty())
				continue;
			final Action action = createGroupAction(group.getKey(),
					group.getValue());
			groupActions.add(action);
			toolBar.appendToGroup(GROUP_MARKER, action);
		}
		toolBar.update(true);
		site.getActionBars().updateActionBars();
	}

	/**
	 * Gets the commands for the current state again in the background, as
	 * whether they are available depends on the state (e.g. some need an
	 * initialised state). Several calls in quick succession get them only
	 * once. If ProB fails, the menus are kept. May be called from any thread.
	 */
	public synchronized void updateForCurrentState(final Category category) {
		if (disposed)
			return;
		if (updateJob != null)
			updateJob.cancel();
		updateJob = new Job("ProB: available " + kind) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final Animator animator = Animator.getAnimator();
				if (monitor.isCanceled() || !animator.isMachineLoaded())
					return Status.CANCEL_STATUS;
				final State state = animator.getCurrentState();
				final List<CommandInfo> loaded;
				try {
					loaded = GetCommandsCommand.getCommands(animator,
							category, state == null ? "root" : state.getId());
				} catch (ProBException e) {
					return Status.OK_STATUS;
				}
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				final Display display = Display.getDefault();
				if (!display.isDisposed())
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (!disposed)
								setCommands(loaded);
						}
					});
				return Status.OK_STATUS;
			}
		};
		updateJob.setSystem(true);
		updateJob.schedule(UPDATE_DELAY_MS);
	}

	/** to be called when the view is disposed */
	public synchronized void dispose() {
		disposed = true;
		if (updateJob != null)
			updateJob.cancel();
	}

	private Action createGroupAction(final String group,
			final List<CommandInfo> groupCommands) {
		final String label = groupLabel(group);
		final Action action = new Action(label, IAction.AS_DROP_DOWN_MENU) {
			@Override
			public void runWithEvent(final Event event) {
				// a click on the button itself also opens the menu
				if (event.widget instanceof ToolItem) {
					final ToolItem item = (ToolItem) event.widget;
					final Menu menu = getMenuCreator().getMenu(
							item.getParent());
					final Rectangle bounds = item.getBounds();
					menu.setLocation(item.getParent().toDisplay(bounds.x,
							bounds.y + bounds.height));
					menu.setVisible(true);
				}
			}
		};
		action.setId(idPrefix + ".group." + group);
		action.setToolTipText(label + " " + kind);
		action.setMenuCreator(new IMenuCreator() {
			private Menu menu;

			@Override
			public Menu getMenu(final Control parent) {
				dispose();
				menu = new Menu(parent);
				for (final CommandInfo command : groupCommands) {
					final MenuItem item = new MenuItem(menu, SWT.PUSH);
					String text = command.getName();
					if (!command.isAvailable())
						text += "  (" + command.getUnavailableReason() + ")";
					item.setText(text);
					item.setEnabled(command.isAvailable());
					item.setToolTipText(command.getDescription());
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent e) {
							handler.run(command);
						}
					});
				}
				return menu;
			}

			@Override
			public Menu getMenu(final Menu parent) {
				return null;
			}

			@Override
			public void dispose() {
				if (menu != null && !menu.isDisposed())
					menu.dispose();
				menu = null;
			}
		});
		return action;
	}

	private String groupLabel(final String group) {
		final String label = groupLabels.get(group);
		if (label != null)
			return label;
		// e.g. state_space_visualisation --> State Space
		final StringBuilder result = new StringBuilder();
		for (String word : group.replaceFirst("_(info|visualisation)$", "")
				.split("_")) {
			if (word.isEmpty())
				continue;
			if (result.length() > 0)
				result.append(' ');
			result.append(Character.toUpperCase(word.charAt(0))).append(
					word.substring(1));
		}
		return result.toString();
	}
}
