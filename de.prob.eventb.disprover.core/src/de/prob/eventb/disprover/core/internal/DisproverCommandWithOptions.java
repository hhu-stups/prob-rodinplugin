package de.prob.eventb.disprover.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Predicate;

import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Like {@link DisproverCommand}, but passes additional options to ProB's
 * cbc_disprove/6, e.g.
 * <code>[disprover_option(wd_prover_timeout(50)),disprover_option(no_constraint_solving)]</code>
 * for the WD prover. The result is processed like the one of the normal
 * disprover.
 */
public class DisproverCommandWithOptions extends DisproverCommand {

	private final List<PrologTerm> options;

	public DisproverCommandWithOptions(Set<Predicate> allHypotheses,
			Set<Predicate> selectedHypotheses, Predicate goal, int timeout,
			Boolean exportPO, List<PrologTerm> options) {
		super(allHypotheses, selectedHypotheses, goal, timeout, exportPO);
		this.options = Collections
				.unmodifiableList(new ArrayList<PrologTerm>(options));
	}

	public List<PrologTerm> getOptions() {
		return options;
	}

	@Override
	protected void writeOptions(final IPrologTermOutput pto) {
		for (PrologTerm option : options) {
			pto.printTerm(option);
		}
		super.writeOptions(pto);
	}

}
