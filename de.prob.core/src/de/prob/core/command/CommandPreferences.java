/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * The ProB preferences changed for a graph or table (e.g. in the ProB Graph
 * view), set with <code>set_eclipse_preference/2</code>. Most of them are
 * advanced preferences, which are not on the ProB preference page (whose node
 * only keeps the preferences shown there), so they are stored in a node of
 * their own. They are set again whenever a model is loaded, after the other
 * preferences.
 */
public final class CommandPreferences {

	private static final String NODE = "prob_command_preferences";

	private CommandPreferences() {
	}

	private static Preferences node() {
		return Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node(NODE);
	}

	/**
	 * Sets the preferences in ProB and stores them. A preference that is also
	 * stored by the ProB preference page is updated there as well.
	 */
	public static void set(final Animator animator,
			final Map<String, String> values) throws ProBException {
		final List<IComposableCommand> commands = new ArrayList<IComposableCommand>();
		for (Map.Entry<String, String> e : values.entrySet())
			commands.add(new SetPreferenceCommand(e.getKey(), e.getValue()));
		animator.execute(new ComposedCommand(commands
				.toArray(IComposableCommand.EMPTY_ARRAY)));
		final Preferences node = node();
		final Preferences general = SetPreferencesCommand.getPreferences();
		for (Map.Entry<String, String> e : values.entrySet()) {
			node.put(e.getKey(), e.getValue());
			if (general.get(e.getKey(), null) != null)
				general.put(e.getKey(), e.getValue());
		}
		try {
			node.flush();
			general.flush();
		} catch (BackingStoreException e) {
			Logger.notifyUser("Could not store the ProB preferences: "
					+ e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Sets the stored preferences in ProB, e.g. after loading a model. Only
	 * logs problems, e.g. a preference that ProB does not know any more.
	 */
	public static void apply(final Animator animator) {
		final Preferences node = node();
		final String[] keys;
		try {
			keys = node.keys();
		} catch (BackingStoreException e) {
			return;
		}
		for (final String key : keys) {
			final String value = node.get(key, null);
			if (value == null)
				continue;
			try {
				Animator.callQuietly(new Animator.ProBCall<Void>() {
					@Override
					public Void call() throws ProBException {
						animator.execute(new SetPreferenceCommand(key, value));
						return null;
					}
				});
			} catch (ProBException e) {
				Logger.info("Could not set the ProB preference " + key
						+ " to " + value + ": " + e.getMessage());
			}
		}
	}
}
