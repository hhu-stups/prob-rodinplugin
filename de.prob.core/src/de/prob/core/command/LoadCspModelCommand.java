/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ProBPreference;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Command to load a new Event B Model for animation.
 */
public final class LoadCspModelCommand {

	private static boolean preferencesAlreadyCleanedUp = false;

	private LoadCspModelCommand() {
		throw new UnsupportedOperationException("Do not instantiate this class");
	}

	private LoadCspModelCommand(final IEventBRoot model) {
	}

	private static void removeObsoletePreferences(final Animator animator)
			throws ProBException {
		if (!preferencesAlreadyCleanedUp) {
			// get all preference names from ProB
			Collection<ProBPreference> prefs = GetPreferencesCommand
					.getPreferences(animator);
			Set<String> probPrefNames = new HashSet<String>();
			for (ProBPreference probpref : prefs) {
				probPrefNames.add(probpref.name);
			}
			// now check all stored (in Eclipse's store) preferences
			// if they still exist
			Preferences preferences = SetPreferencesCommand.getPreferences();
			try {
				boolean foundObsoletePreference = false;
				for (String prefname : preferences.keys()) {
					if (!probPrefNames.contains(prefname)) {
						// preference does not exists anymore
						preferences.remove(prefname);
						foundObsoletePreference = true;
						String message = "removed obsolete preference from preferences store: "
								+ prefname;
						Logger.info(message);
					}
				}
				if (foundObsoletePreference) {
					preferences.flush();
				}
			} catch (BackingStoreException e) {
				Logger.notifyUser("Error while accessing ProB Preferences", e);
			}
			preferencesAlreadyCleanedUp = true;
		}
	}

	public static void load(final Animator animator, final File model,
			String name) throws ProBException {
		animator.resetDirty();
		removeObsoletePreferences(animator);

		final ClearMachineCommand clear = new ClearMachineCommand();
		final SetPreferencesCommand setPrefs = SetPreferencesCommand
				.createSetPreferencesCommand(animator);
		final IComposableCommand load = getLoadCommand(model, name);
		final StartAnimationCommand start = new StartAnimationCommand();
		final ExploreStateCommand explore = new ExploreStateCommand("root");

		final ComposedCommand composed = new ComposedCommand(clear, setPrefs,
				load, start, explore);

		animator.execute(composed);

		final State commandResult = explore.getState();
		animator.announceCurrentStateChanged(commandResult,
				Operation.NULL_OPERATION);

	}

	private static IComposableCommand getLoadCommand(final File model,
			final String name) throws ProBException {
		return new IComposableCommand() {

			@Override
			public void writeCommand(final IPrologTermOutput pto)
					throws CommandException {
				pto.openTerm("load_cspm_spec_from_cspm_file");
				pto.printAtom(model.getAbsolutePath());
				pto.closeTerm();
				pto.printAtom("start_animation");
			}

			@Override
			public void processResult(
					final ISimplifiedROMap<String, PrologTerm> bindings) {
				Animator.getAnimator().announceReset();
			}
		};

	}


}