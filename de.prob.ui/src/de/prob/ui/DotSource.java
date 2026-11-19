/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui;

import java.io.File;

import de.prob.exceptions.ProBException;

/**
 * Writes a GraphViz dot file for the ProB Graph view, e.g. with a probcli
 * other than the animator's; see {@link ProbUiPlugin#showDotGraph}.
 */
public interface DotSource {
	void writeDot(File dotFile) throws ProBException;
}
