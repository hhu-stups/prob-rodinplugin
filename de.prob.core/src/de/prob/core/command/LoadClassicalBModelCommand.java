/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ProBPreference;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Command to load a new Event B Model for animation.
 */
public final class LoadClassicalBModelCommand {

	private static boolean preferencesAlreadyCleanedUp = false;

	private LoadClassicalBModelCommand() {
		throw new UnsupportedOperationException("Do not instantiate this class");
	}

	private LoadClassicalBModelCommand(final IEventBRoot model) {
	}

	private static void removeObsoletePreferences(final Animator animator) throws ProBException {
		if (!preferencesAlreadyCleanedUp) {
			// get all preference names from ProB
			Collection<ProBPreference> prefs = GetPreferencesCommand.getPreferences(animator);
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
						String message = "removed obsolete preference from preferences store: " + prefname;
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

	public static void load(final Animator animator, final File model, String name) throws ProBException {
		animator.resetDirty();
		animator.resetRodinProjectHasErrorsOrWarnings(); // classical B machines have no Rodin project
		removeObsoletePreferences(animator);

		final ClearMachineCommand clear = new ClearMachineCommand();
		final SetPreferencesCommand setPrefs = SetPreferencesCommand.createSetPreferencesCommand(animator);
		final IComposableCommand load = getLoadCommand(model, name);
		final StartAnimationCommand start = new StartAnimationCommand();
		final ExploreStateCommand explore = new ExploreStateCommand("root");

		final ComposedCommand composed = new ComposedCommand(clear, setPrefs, load, start, explore);

		animator.execute(composed);

		final State commandResult = explore.getState();
		animator.announceCurrentStateChanged(commandResult, Operation.NULL_OPERATION);
	}

	private static IComposableCommand getLoadCommand(final File model, final String name) throws ProBException {
		return new IComposableCommand() {

			@Override
			public void writeCommand(final IPrologTermOutput pto) throws CommandException {
				pto.openTerm("load_b_project");
				pto.printAtom(name);
				pto.printTerm(getLoadTerm(model));
				pto.printVariable("Errors");
				pto.closeTerm();
				pto.printAtom("start_animation");
			}

			@Override
			public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
				Animator.getAnimator().announceReset();
				ListPrologTerm e = (ListPrologTerm) bindings.get("Errors");
				if (!e.isEmpty()) {
					StringBuffer errormsg = new StringBuffer("Error from Prolog: ");
					for (PrologTerm prologTerm : e) {
						errormsg.append(prologTerm);
						errormsg.append('\n');
					}
					Logger.notifyUser(errormsg.toString());
				}
			}
		};

	}

	private static PrologTerm getLoadTerm(final File model) throws CommandException {
		BParser bParser = new BParser();
		try {
			Start ast = bParser.parseFile(model, false);
			final RecursiveMachineLoader rml = new RecursiveMachineLoader(model.getParent(), null);
			rml.loadAllMachines(model, ast, bParser.getDefinitions());
			StructuredPrologOutput output = new StructuredPrologOutput();
			StructuredPrologOutput out = new StructuredPrologOutput();
			rml.printAsProlog(output);

			Collection<PrologTerm> sentences = output.getSentences();
			out.openList();
			Iterator<PrologTerm> iterator = sentences.iterator();
			iterator.next();
			iterator.next();
			while (iterator.hasNext()) {
				CompoundPrologTerm prologTerm = (CompoundPrologTerm) iterator.next();
				out.printTerm(prologTerm.getArgument(1));
			}
			out.closeList();
			out.fullstop();
			return out.getSentences().iterator().next();
		} catch (IOException e) {
			Logger.notifyUser("IO Error", e);
			throw new CommandException(e.getLocalizedMessage(), e);
		} catch (BCompoundException e) {
			Logger.notifyUser("Parser Error " + e.getLocalizedMessage(), e);
			throw new CommandException(e.getLocalizedMessage(), e);
		}
	}
}
