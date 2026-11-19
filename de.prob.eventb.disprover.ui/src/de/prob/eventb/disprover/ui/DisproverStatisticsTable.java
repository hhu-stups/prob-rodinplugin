/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.disprover.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.eventb.disprover.core.DisproverStatistics;
import de.prob.eventb.disprover.core.DisproverStatistics.Entry;
import de.prob.ui.TableSource;

/**
 * The Disprover Statistics table of the ProB Table view: per prover of ProB's
 * disprover and result, the number of calls and their times, for all proof
 * obligations and for the last one. Updated after each disprover call.
 */
public class DisproverStatisticsTable implements TableSource {

	/** its id in the plugin.xml */
	static final String ID = "de.prob.eventb.disprover.ui.statisticsTable";

	static final List<String> HEADERS = Collections.unmodifiableList(Arrays
			.asList("Prover", "Result", "Calls", "Runtime (ms)",
					"Total Runtime (ms)", "Walltime (ms)", "Last PO: Calls",
					"Last PO: Runtime (ms)", "Last PO: Walltime (ms)"));

	// names of ProB's provers
	private static final Map<String, String> PROVERS = new HashMap<String, String>();
	static {
		PROVERS.put("wd_prover", "WD prover");
		PROVERS.put("setlog_prover", "{log} prover");
		PROVERS.put("prob_all_hyps", "constraint solver, all hypotheses");
		PROVERS.put("prob_selected_hyps",
				"constraint solver, selected hypotheses");
		PROVERS.put("prob_check_negated_goal",
				"constraint solver, negated goal (double check)");
	}

	@Override
	public Table compute() {
		return table();
	}

	static Table table() {
		final List<List<String>> rows = new ArrayList<List<String>>();
		rows.add(HEADERS);
		final Boolean supported = DisproverStatistics.isSupported();
		if (Boolean.FALSE.equals(supported))
			return new Table(rows,
					"The disprover's probcli does not provide statistics: a newer ProB with cbc_disprover_statistics/2 is needed.");
		final List<Entry> total = DisproverStatistics.getTotal();
		final List<Entry> last = new ArrayList<Entry>(
				DisproverStatistics.getLastCall());
		for (Entry e : total) {
			Entry inLast = null;
			for (Entry l : last)
				if (l.sameKey(e))
					inLast = l;
			last.remove(inLast);
			rows.add(row(e, inLast));
		}
		for (Entry l : last)
			// not in the total, e.g. after a reset during the call
			rows.add(row(null, l));
		final StringBuilder note = new StringBuilder();
		final int calls = DisproverStatistics.getCalls();
		if (calls == 0)
			note.append(supported == null ? "The disprover has not been used since Rodin was started."
					: "No disprover calls since the statistics were reset.");
		else {
			note.append(calls).append(calls == 1 ? " disprover call" : " disprover calls")
					.append(" with statistics; last: ")
					.append(DisproverStatistics.getLastProofObligation())
					.append(", ").append(DisproverStatistics.getLastOutcome());
		}
		note.append(". The calls and times are those since the disprover's probcli started or the statistics were reset;"
				+ " Runtime is CPU time without garbage collection, Total Runtime with it.");
		return new Table(rows, note.toString());
	}

	private static List<String> row(final Entry total, final Entry last) {
		final Entry any = total != null ? total : last;
		final String prover = PROVERS.get(any.getProver());
		return Arrays.asList(prover == null ? any.getProver() : prover,
				any.getResult(),
				total == null ? "" : Long.toString(total.getCalls()),
				total == null ? "" : Long.toString(total.getRuntime()),
				total == null ? "" : Long.toString(total.getTotalRuntime()),
				total == null ? "" : Long.toString(total.getWalltime()),
				last == null ? "" : Long.toString(last.getCalls()),
				last == null ? "" : Long.toString(last.getRuntime()),
				last == null ? "" : Long.toString(last.getWalltime()));
	}

	@Override
	public void addChangeListener(final Runnable listener) {
		DisproverStatistics.addListener(listener);
	}

	@Override
	public void removeChangeListener(final Runnable listener) {
		DisproverStatistics.removeListener(listener);
	}
}
