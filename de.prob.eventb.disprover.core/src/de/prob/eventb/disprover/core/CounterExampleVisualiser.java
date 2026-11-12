/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.disprover.core;

import java.io.File;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

/**
 * Shows the visualisation of a counter-example, for the "Visualise" link of
 * the disprover's notice; set by the disprover's UI plug-in with
 * {@link DisproverReasoner#setCounterExampleVisualiser}, e.g. to show it in
 * the ProB Graph view.
 */
public interface CounterExampleVisualiser {

	/** writes the visualisation as a GraphViz dot file */
	interface DotWriter {
		void writeDot(File dotFile) throws ProBException;
	}

	/**
	 * called in the UI thread
	 * 
	 * @param animator
	 *            the animator the writer uses, e.g. to interrupt it
	 */
	void show(String title, Animator animator, DotWriter writer);
}
