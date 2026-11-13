/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.disprover.ui;

import de.prob.eventb.disprover.core.DisproverStatistics;
import de.prob.exceptions.ProBException;
import de.prob.ui.TableSource;

/**
 * "Reset Disprover Statistics" in the ProB Table view: resets the statistics
 * (also in the disprover's probcli) and continues with the
 * {@link DisproverStatisticsTable}, which is updated after each disprover call.
 */
public class ResetDisproverStatisticsTable implements TableSource {

	@Override
	public Table compute() throws ProBException {
		// waits if the disprover is running
		DisproverStatistics.reset();
		return DisproverStatisticsTable.table();
	}

	/** then the statistics are shown, so that Refresh does not reset them */
	@Override
	public String getFollowUpTable() {
		return DisproverStatisticsTable.ID;
	}
}
