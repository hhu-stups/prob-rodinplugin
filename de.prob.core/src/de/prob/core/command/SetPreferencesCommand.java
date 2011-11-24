/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class SetPreferencesCommand implements IComposableCommand {

	private static final String PROB_ANIMATION_PREFERENCES = "prob_animation_preferences";
	private static final Collection<String> INVALID_PROPERTIES = Collections
			.unmodifiableCollection(new HashSet<String>(Arrays.asList("title",
					"project", "machine")));

	private final Preferences preferences;
	private final IComposableCommand cmds;

	public static Preferences getPreferences() {
		return Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node(PROB_ANIMATION_PREFERENCES);
	}

	public SetPreferencesCommand() {
		this(getPreferences());
	}

	public SetPreferencesCommand(final Preferences customConfiguration) {
		preferences = customConfiguration;
		String[] names;
		try {
			names = preferences.keys();
		} catch (BackingStoreException e) {
			names = new String[0];
			Logger.notifyUser("Error while storing ProB Preferences", e);
		}
		final List<IComposableCommand> commands = new ArrayList<IComposableCommand>(
				names.length);
		for (String k : names) {
			if (validCommand(k)) {
				String value = preferences.get(k, null);
				commands.add(new SetPreferenceCommand(k, value));
			}
		}
		this.cmds = new ComposedCommand(
				commands.toArray(IComposableCommand.EMPTY_ARRAY));
	}

	public static void setPreferences(final Animator a) throws ProBException {
		final SetPreferencesCommand c = createSetPreferencesCommand(a);
		a.execute(c);
	}

	public static SetPreferencesCommand createSetPreferencesCommand(
			final Animator a) {
		SetPreferencesCommand c;
		if (a.getCustomConfiguration() != null) {
			c = new SetPreferencesCommand(a.getCustomConfiguration());
		} else {
			c = new SetPreferencesCommand();
		}
		return c;
	}

	private boolean validCommand(final String key) {
		return !INVALID_PROPERTIES.contains(key);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		cmds.processResult(bindings);
	}

	public void writeCommand(final IPrologTermOutput pto) throws CommandException {
		cmds.writeCommand(pto);
	}
}
