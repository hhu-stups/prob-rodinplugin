/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.disprover.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import de.prob.core.Animator;
import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Statistics about the provers ProB's disprover uses (e.g. the WD prover and
 * the constraint solver) and their results, from
 * <code>cbc_disprover_statistics/2</code> of the disprover's probcli. They are
 * got together with each disprover call, so the values here are those after
 * the last call; for all calls since probcli started (or since
 * {@link #reset()}) and for the last call only.
 */
public final class DisproverStatistics {

	/** the statistics of one prover and one of its results */
	public static final class Entry {
		private final String prover;
		private final String result;
		private final long calls;
		private final long runtime;
		private final long totalRuntime;
		private final long walltime;

		Entry(final String prover, final String result, final long calls,
				final long runtime, final long totalRuntime,
				final long walltime) {
			this.prover = prover;
			this.result = result;
			this.calls = calls;
			this.runtime = runtime;
			this.totalRuntime = totalRuntime;
			this.walltime = walltime;
		}

		/** e.g. wd_prover, setlog_prover or a context of the solver */
		public String getProver() {
			return prover;
		}

		/** e.g. contradiction_found, solution, time_out or failed */
		public String getResult() {
			return result;
		}

		public long getCalls() {
			return calls;
		}

		/** CPU time in ms, without garbage collection */
		public long getRuntime() {
			return runtime;
		}

		/** CPU time in ms, including garbage collection */
		public long getTotalRuntime() {
			return totalRuntime;
		}

		/** elapsed time in ms */
		public long getWalltime() {
			return walltime;
		}

		/** whether it is about the same prover and result */
		public boolean sameKey(final Entry other) {
			return prover.equals(other.prover) && result.equals(other.result);
		}
	}

	private static List<Entry> total = Collections.emptyList();
	private static List<Entry> lastCall = Collections.emptyList();
	private static String lastProofObligation;
	private static String lastOutcome;
	private static int calls;

	/** whether an animator's probcli knows cbc_disprover_statistics/2 */
	private static final Map<Animator, Boolean> SUPPORTED = new WeakHashMap<Animator, Boolean>();

	private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<Runnable>();

	private DisproverStatistics() {
	}

	/**
	 * @return the statistics of all calls since probcli started or since
	 *         {@link #reset()}
	 */
	public static synchronized List<Entry> getTotal() {
		return total;
	}

	/** @return the statistics of the last disprover call */
	public static synchronized List<Entry> getLastCall() {
		return lastCall;
	}

	/**
	 * @return the proof obligation of the last call, e.g.
	 *         <code>inv1/INV (M1)</code>; <code>null</code> if there was none
	 */
	public static synchronized String getLastProofObligation() {
		return lastProofObligation;
	}

	/** @return the outcome of the last call, e.g. "proven" */
	public static synchronized String getLastOutcome() {
		return lastOutcome;
	}

	/**
	 * @return the number of disprover calls with statistics since Rodin was
	 *         started or since {@link #reset()}
	 */
	public static synchronized int getCalls() {
		return calls;
	}

	/**
	 * @return whether the disprover's probcli is known to support the
	 *         statistics: {@link Boolean#FALSE} for an older probcli,
	 *         <code>null</code> if the disprover has not been used yet
	 */
	public static Boolean isSupported() {
		synchronized (SUPPORTED) {
			return SUPPORTED.get(Animator.getAuxAnimator());
		}
	}

	/** called when the statistics changed, in the thread that changed them */
	public static void addListener(final Runnable listener) {
		LISTENERS.add(listener);
	}

	public static void removeListener(final Runnable listener) {
		LISTENERS.remove(listener);
	}

	/**
	 * Resets the statistics, also those of the disprover's probcli (if it has
	 * been started and supports them).
	 */
	public static void reset() throws ProBException {
		final Animator animator = Animator.getAuxAnimator();
		if (Boolean.TRUE.equals(isSupported(animator)))
			animator.execute(new IComposableCommand() {
				@Override
				public void writeCommand(final IPrologTermOutput pto) {
					pto.printAtom("cbc_reset_disprover_statistics");
				}

				@Override
				public void processResult(
						final ISimplifiedROMap<String, PrologTerm> bindings) {
				}
			});
		synchronized (DisproverStatistics.class) {
			total = Collections.emptyList();
			lastCall = Collections.emptyList();
			lastProofObligation = null;
			lastOutcome = null;
			calls = 0;
		}
		fireChanged();
	}

	private static Boolean isSupported(final Animator animator) {
		synchronized (SUPPORTED) {
			return SUPPORTED.get(animator);
		}
	}

	/**
	 * Finds out once per animator whether its probcli supports the
	 * statistics; an older probcli does not know cbc_disprover_statistics/2,
	 * and a call of it would make the whole disprover call fail. Executes a
	 * command, so it must not be called while the animator executes another
	 * one in the same thread.
	 * <p>
	 * For the disprover's commands only.
	 */
	public static boolean checkSupported(final Animator animator) {
		final Boolean known = isSupported(animator);
		if (known != null)
			return known;
		boolean supported;
		try {
			Animator.callQuietly(new Animator.ProBCall<Void>() {
				@Override
				public Void call() throws ProBException {
					animator.execute(new StatisticsCommand(null, null));
					return null;
				}
			});
			supported = true;
		} catch (ProBException e) {
			supported = false;
			Logger.info("probcli does not provide statistics about the disprover's provers (no cbc_disprover_statistics/2): "
					+ e.getMessage());
		}
		synchronized (SUPPORTED) {
			SUPPORTED.put(animator, supported);
		}
		return supported;
	}

	/**
	 * @return the command that gets the statistics after a disprover call
	 *         (in the same composed command), or a command doing nothing if
	 *         probcli does not support them
	 * @param proofObligation
	 *            the name of the proof obligation, for the table
	 * @param outcome
	 *            the outcome of the disprover call; evaluated after the call's
	 *            result has been processed
	 * <p>
	 * For the disprover's commands only.
	 */
	public static IComposableCommand command(final boolean supported,
			final String proofObligation, final Supplier<String> outcome) {
		if (!supported)
			return new IComposableCommand() {
				@Override
				public void writeCommand(final IPrologTermOutput pto) {
					pto.printAtom("true");
				}

				@Override
				public void processResult(
						final ISimplifiedROMap<String, PrologTerm> bindings) {
				}
			};
		return new StatisticsCommand(proofObligation, outcome);
	}

	private static void fireChanged() {
		for (Runnable listener : LISTENERS) {
			try {
				listener.run();
			} catch (RuntimeException e) {
				Logger.info("Updating the disprover statistics failed: " + e);
			}
		}
	}

	/** <code>cbc_disprover_statistics(Total,LastCall)</code> */
	private static final class StatisticsCommand implements IComposableCommand {
		private static final String TOTAL = "Total";
		private static final String LAST = "LastCall";

		/** null: only checks whether probcli supports it */
		private final String proofObligation;
		private final Supplier<String> outcome;

		StatisticsCommand(final String proofObligation,
				final Supplier<String> outcome) {
			this.proofObligation = proofObligation;
			this.outcome = outcome;
		}

		@Override
		public void writeCommand(final IPrologTermOutput pto) {
			pto.openTerm("cbc_disprover_statistics");
			pto.printVariable(TOTAL);
			pto.printVariable(LAST);
			pto.closeTerm();
		}

		@Override
		public void processResult(
				final ISimplifiedROMap<String, PrologTerm> bindings)
				throws CommandException {
			final List<Entry> newTotal = entries(bindings.get(TOTAL));
			final List<Entry> newLast = entries(bindings.get(LAST));
			if (outcome == null)
				return; // only the check
			final String outcomeText = outcome.get();
			synchronized (DisproverStatistics.class) {
				total = newTotal;
				lastCall = newLast;
				lastProofObligation = proofObligation;
				lastOutcome = outcomeText;
				calls++;
			}
			fireChanged();
		}

		/** prover_stats(Prover,Result,Nr,timer(Runtime,TotRuntime,Walltime)) */
		private static List<Entry> entries(final PrologTerm list)
				throws CommandException {
			if (!(list instanceof ListPrologTerm))
				throw new CommandException(
						"Unexpected disprover statistics: " + list);
			final List<Entry> result = new ArrayList<Entry>();
			for (PrologTerm t : (ListPrologTerm) list) {
				if (!t.hasFunctor("prover_stats", 4))
					throw new CommandException(
							"Unexpected disprover statistics: " + t);
				final PrologTerm timer = t.getArgument(4);
				final boolean hasTimer = timer.hasFunctor("timer", 3);
				result.add(new Entry(atom(t.getArgument(1)), atom(t
						.getArgument(2)), number(t.getArgument(3)),
						hasTimer ? number(timer.getArgument(1)) : 0,
						hasTimer ? number(timer.getArgument(2)) : 0,
						hasTimer ? number(timer.getArgument(3)) : 0));
			}
			return Collections.unmodifiableList(result);
		}

		private static String atom(final PrologTerm t) {
			return t.isAtom() ? t.getFunctor() : t.toString();
		}

		private static long number(final PrologTerm t) {
			if (t instanceof IntegerPrologTerm)
				return ((IntegerPrologTerm) t).getValue().longValue();
			try {
				return Long.parseLong(t.toString());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}
}
