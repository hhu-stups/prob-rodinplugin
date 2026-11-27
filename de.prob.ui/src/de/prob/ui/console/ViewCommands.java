/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;

import de.prob.core.Animator;
import de.prob.core.command.GetCommandsCommand;
import de.prob.core.command.GetCommandsCommand.Category;
import de.prob.core.command.GetCommandsCommand.CommandInfo;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

/**
 * ProB's tables and graphs (the commands of its meta interface, e.g.
 * <code>state_space</code>) for the console's <code>:table "NAME"</code> and
 * <code>:graph "NAME"</code>: their names are cached for the completion,
 * until a new model is loaded.
 */
final class ViewCommands {

	/** ":graph " or ":table " at the start, ':' maybe translated to '∈' */
	private static final Pattern COMMAND = Pattern
			.compile("^\\s*[:∈](graph|table)\\s");
	/** the same, followed by an opening quote: where a name is completed */
	private static final Pattern NAME_START = Pattern
			.compile("^\\s*[:∈](graph|table)\\s+\"$");

	private static final Map<Category, List<CommandInfo>> CACHE = new ConcurrentHashMap<Category, List<CommandInfo>>();
	private static final Set<Category> LOADING = ConcurrentHashMap.newKeySet();

	private ViewCommands() {
	}

	static Category category(final boolean table) {
		return table ? Category.TABLE : Category.DOT;
	}

	/** forgets the names, e.g. when a new model is loaded */
	static void clear() {
		CACHE.clear();
	}

	/**
	 * @return the tables or graphs that ProB can compute in the current state
	 *         (asks ProB; not in the UI thread)
	 */
	static List<CommandInfo> load(final Category category)
			throws ProBException {
		final Animator animator = Animator.getAnimator();
		final State state = animator.getCurrentState();
		final List<CommandInfo> commands = GetCommandsCommand.getCommands(
				animator, category, state == null ? "root" : state.getId());
		CACHE.put(category, commands);
		return commands;
	}

	/**
	 * gets the names in the background if the input starts with ":graph " or
	 * ":table " and they are not known yet, so that they can be completed
	 */
	static void prefetchFor(final String input) {
		final Matcher m = COMMAND.matcher(input);
		if (!m.find())
			return;
		final Category category = category(m.group(1).equals("table"));
		if (CACHE.containsKey(category)
				|| !Animator.getAnimator().isMachineLoaded()
				|| !LOADING.add(category))
			return;
		final Job job = new Job("ProB Console: available "
				+ (category == Category.TABLE ? "tables" : "graphs")) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					load(category);
				} catch (ProBException e) {
					// no completion of the names then
				} finally {
					LOADING.remove(category);
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * @return the proposals for the name being typed after
	 *         <code>:graph "</code> or <code>:table "</code>, with the closing
	 *         quote (and a space if the command needs a formula); null if the
	 *         text before the name is not such a command
	 */
	static IContentProposal[] proposals(final String beforeName,
			final String prefix) {
		final Matcher m = NAME_START.matcher(beforeName);
		if (!m.find())
			return null;
		final Category category = category(m.group(1).equals("table"));
		final List<CommandInfo> commands = CACHE.get(category);
		if (commands == null) {
			prefetchFor(beforeName);
			return new IContentProposal[0];
		}
		final List<IContentProposal> result = new ArrayList<IContentProposal>();
		for (CommandInfo c : commands) {
			final String name = c.getCommand();
			if (!name.startsWith(prefix))
				continue;
			final String label = name + "  –  " + c.getName()
					+ (c.isAvailable() ? "" : "  (not available)");
			String description = c.getDescription();
			if (c.getFormulaArguments() > 0)
				description += "\nNeeds a formula: :"
						+ m.group(1) + " \"" + name + "\" FORMULA";
			if (!c.isAvailable())
				description += "\nNot available: " + c.getUnavailableReason();
			result.add(new ContentProposal(name.substring(prefix.length())
					+ "\"" + (c.getFormulaArguments() > 0 ? " " : ""), label,
					description));
		}
		return result.toArray(new IContentProposal[result.size()]);
	}

	/** @return the command with the name, or null */
	static CommandInfo find(final List<CommandInfo> commands, final String name) {
		for (CommandInfo c : commands)
			if (c.getCommand().equals(name))
				return c;
		for (CommandInfo c : commands)
			if (c.getCommand().equalsIgnoreCase(name)
					|| c.getName().equalsIgnoreCase(name))
				return c;
		return null;
	}

	/** @return the names of the available commands, sorted */
	static List<String> availableNames(final List<CommandInfo> commands) {
		final List<String> names = new ArrayList<String>();
		for (CommandInfo c : commands)
			if (c.isAvailable())
				names.add(c.getCommand());
		Collections.sort(names);
		return names;
	}
}
