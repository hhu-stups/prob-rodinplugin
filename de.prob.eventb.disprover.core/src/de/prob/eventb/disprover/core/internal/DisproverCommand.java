package de.prob.eventb.disprover.core.internal;

import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Predicate;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.ClearMachineCommand;
import de.prob.core.command.CommandException;
import de.prob.core.command.ComposedCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.core.command.SetPreferencesCommand;
import de.prob.core.command.StartAnimationCommand;
import de.prob.core.command.internal.InternalLoadCommand;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.eventb.disprover.core.ICounterExample;
import de.prob.eventb.translator.PredicateVisitor;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * The DisproverCommand takes two sets of ASTs (one for the machine and a list
 * for the contexts) and tries to set them up with ProB. If setup is possible,
 * the arguments from that operation are joined with the provided variables and
 * returned as an {@link ICounterExample}.
 * <p>
 * 
 * This command is probably not useful without {@link DisproverReasoner}, which
 * calls it.
 * 
 * @author jastram
 */
public class DisproverCommand implements IComposableCommand {

	private static final String RESULT = "Result";
	/**
	 * Lifted from {@link LoadEventBModelCommand}
	 */

	// private final AEventBModelParseUnit machineAst;
	// private final List<AEventBContextParseUnit> contextAsts;
	// private final List<DisproverIdentifier> vars;
	private CounterExample counterExample;
	private Set<Predicate> hypotheses;
	private Predicate goal;
	private IEventBRoot root;

	public DisproverCommand(Set<Predicate> hypotheses, Predicate goal,
			IEventBRoot root) {
		this.hypotheses = hypotheses;
		this.goal = goal;
		this.root = root;

	}

	public static ICounterExample disprove(Animator animator,
			Set<Predicate> hypotheses, Predicate goal, IEventBRoot root)
			throws ProBException {

		final ClearMachineCommand clear = new ClearMachineCommand();
		final SetPreferencesCommand setPrefs = SetPreferencesCommand
				.createSetPreferencesCommand(animator);
		final InternalLoadCommand load = new InternalLoadCommand(root);

		final StartAnimationCommand start = new StartAnimationCommand();

		DisproverCommand disprove = new DisproverCommand(hypotheses, goal, root);

		final ComposedCommand composed = new ComposedCommand(clear, setPrefs,
				load, start, disprove);

		animator.execute(composed);

		return disprove.getResult();
	}

	private ICounterExample getResult() {
		return counterExample;
	}

	public void writeCommand(final IPrologTermOutput pto) {

		pto.openTerm("cbc_disprove");

		Predicate pred = goal;
		translatePredicate(pto, pred);
		pto.openList();
		for (Predicate p : this.hypotheses) {
			translatePredicate(pto, p);
		}
		pto.closeList();
		pto.printVariable(RESULT);
		pto.closeTerm();
	}

	private void translatePredicate(final IPrologTermOutput pto, Predicate pred) {
		PredicateVisitor v = new PredicateVisitor();
		pred.accept(v);
		ASTProlog p = new ASTProlog(pto, null);
		v.getPredicate().apply(p);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {

		PrologTerm term = bindings.get(RESULT);

		counterExample = null;

		if ("timeout".equals(term.getFunctor())) {
			counterExample = new CounterExample(true, true);
		}
		if ("no_counter_example".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, false);
		}

		if ("proven".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, false);
			counterExample.setProof(true);
		}

		if ("counter_example".equals(term.getFunctor())) {
			counterExample = new CounterExample(true, false);
			ListPrologTerm vars = (ListPrologTerm) term.getArgument(1);

			for (PrologTerm e : vars) {
				counterExample.addVar(e.getArgument(1).getFunctor(), e
						.getArgument(3).getFunctor());
			}
		}

		if (counterExample == null) {
			throw new CommandException(
					"Internal Error in ProB. Unexpected result:"
							+ term.toString());
		}
	}

}
