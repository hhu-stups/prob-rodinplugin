/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Asks ProB for information about preferences given by their eclipse names,
 * e.g. the preferences relevant for a graph or table (see
 * {@link GetCommandsCommand.CommandInfo#getPreferences()}), using
 * <code>list_all_eclipse_preferences/1</code> (which includes the advanced
 * preferences) and <code>list_current_eclipse_preferences/1</code>.
 */
public final class GetPreferenceInfosCommand implements IComposableCommand {

	/** a preference with its current value */
	public static final class Info {
		private final String name;
		private final List<String> values;
		private final String type;
		private final String description;
		private final String defaultValue;
		private final String currentValue;

		Info(final String name, final List<String> values, final String type,
				final String description, final String defaultValue,
				final String currentValue) {
			this.name = name;
			this.values = values;
			this.type = type;
			this.description = description;
			this.defaultValue = defaultValue;
			this.currentValue = currentValue;
		}

		public String getName() {
			return name;
		}

		/**
		 * @return the valid values, e.g. of an enumeration, or an empty list
		 *         if any value of {@link #getType()} is allowed
		 */
		public List<String> getValues() {
			return values;
		}

		/**
		 * @return the type, e.g. bool, int, nat, nat1, string, rgb_color,
		 *         range(1,10), or null if the values are listed
		 */
		public String getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public String getCurrentValue() {
			return currentValue;
		}
	}

	private static final String INFOS = "Infos";

	private final List<String> names;
	private final GetCurrentPreferencesCommand current = new GetCurrentPreferencesCommand();
	private List<Info> infos = Collections.emptyList();

	private GetPreferenceInfosCommand(final List<String> names) {
		this.names = names;
	}

	/**
	 * @return the preferences known to ProB among the names, in their order;
	 *         preferences without an eclipse name are left out
	 */
	public static List<Info> getInfos(final Animator animator,
			final List<String> names) throws ProBException {
		final GetPreferenceInfosCommand command = new GetPreferenceInfosCommand(
				names);
		animator.execute(new ComposedCommand(command, command.current));
		return command.merge();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("list_all_eclipse_preferences").printVariable(INFOS)
				.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<Info> result = new ArrayList<Info>();
		// preference(Name,Type,Description,Category,Default)
		for (PrologTerm t : BindingGenerator.getList(bindings, INFOS)) {
			if (!t.hasFunctor("preference", 5))
				continue;
			final PrologTerm type = t.getArgument(2);
			final List<String> values = new ArrayList<String>();
			if (type instanceof ListPrologTerm)
				for (PrologTerm v : (ListPrologTerm) type)
					values.add(GetCommandsCommand.text(v));
			result.add(new Info(GetCommandsCommand.text(t.getArgument(1)),
					Collections.unmodifiableList(values),
					type instanceof ListPrologTerm ? null : GetCommandsCommand
							.text(type), GetCommandsCommand.text(t
							.getArgument(3)), GetCommandsCommand.text(t
							.getArgument(5)), null));
		}
		infos = result;
	}

	/** selects the asked preferences and adds their current values */
	private List<Info> merge() {
		final Map<String, Info> byName = new HashMap<String, Info>();
		for (Info info : infos)
			byName.put(info.name, info);
		final Map<String, String> values = current.getValues();
		final List<Info> result = new ArrayList<Info>();
		for (String name : names) {
			final Info info = byName.get(name);
			if (info == null)
				continue;
			final String value = values.get(name);
			result.add(new Info(info.name, info.values, info.type,
					info.description, info.defaultValue,
					value != null ? value : info.defaultValue));
		}
		return Collections.unmodifiableList(result);
	}
}
