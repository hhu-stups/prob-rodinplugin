/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.translator;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Links in notices with ProB's messages about missing theory mappings, e.g.
 * "... add a Real.ptm file to your Theory project RealTheory with this line:
 * ...", which create the theory mapping files of the named projects (see
 * {@link TheoryMappingFiles}). Does not need the Theory plug-in.
 */
public final class TheoryMappingLinks {

	/** the label of the link */
	public static final String LABEL = "Create ProB Theory Mapping Files";

	private static final Pattern THEORY_PROJECT = Pattern
			.compile("\\.ptm file to your (?:Theory )?project ([^\\s;:,]+)");

	/** creates (or extends) the mapping files of a project's theories */
	private static volatile Consumer<IProject> creator;

	private TheoryMappingLinks() {
	}

	/** called by the UI plug-in, which creates and opens the files */
	public static void setCreator(final Consumer<IProject> c) {
		creator = c;
	}

	/**
	 * @return an action creating the mapping files of the theory projects
	 *         named in ProB's messages, run in the UI thread by a notice's
	 *         link; null if the messages name none (or there is no UI)
	 */
	public static Runnable creatorFor(final String messages) {
		final Consumer<IProject> c = creator;
		if (c == null || messages == null || !messages.contains(".ptm"))
			return null;
		final Set<IProject> projects = new LinkedHashSet<IProject>();
		final Matcher m = THEORY_PROJECT.matcher(messages);
		while (m.find()) {
			final IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(m.group(1));
			if (project.exists())
				projects.add(project);
		}
		if (projects.isEmpty())
			return null;
		return () -> {
			for (IProject project : projects)
				c.accept(project);
		};
	}
}
