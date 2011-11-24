package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.command.LtlCheckingCommand.Result;
import de.prob.core.domainobjects.Operation;
import de.prob.logging.Logger;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Provides a counter-example.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExample {
	private final static PrologTerm NONE = new CompoundPrologTerm("none");

	private final CounterExampleProposition propositionRoot;
	private final List<CounterExampleProposition> propositions = new ArrayList<CounterExampleProposition>();
	private final List<CounterExampleState> states = new ArrayList<CounterExampleState>();
	private final int loopEntry;
	private List<ArrayList<Boolean>> predicateValues;
	private final List<Operation> initPath;

	private final ListPrologTerm atomics;
	private final ListPrologTerm example;
	private final PathType pathType;

	public CounterExample(final Result modelCheckingResult) {
		atomics = modelCheckingResult.getAtomics();
		example = modelCheckingResult.getCounterexample();
		loopEntry = modelCheckingResult.getLoopEntry();
		pathType = modelCheckingResult.getPathType();
		initPath = Collections.unmodifiableList(Arrays
				.asList(modelCheckingResult.getInitPathOps()));

		createStates(example);

		propositionRoot = createExample(modelCheckingResult.getStructure());
		propositionRoot.setVisible(true);
		Collections.reverse(propositions);

	}

	private void createStates(final ListPrologTerm example) {
		final boolean isLoopType = pathType == PathType.INFINITE;
		int index = 0;
		for (PrologTerm exampleElement : example) {
			CompoundPrologTerm state = (CompoundPrologTerm) exampleElement;
			int stateId = ((IntegerPrologTerm) state.getArgument(1)).getValue()
					.intValue();
			final ListPrologTerm values = ((ListPrologTerm) state
					.getArgument(2));
			final CompoundPrologTerm operationTerm = (CompoundPrologTerm) state
					.getArgument(3);

			if (predicateValues == null) {
				predicateValues = new ArrayList<ArrayList<Boolean>>();

				for (int i = 0; i < values.size(); i++) {
					predicateValues.add(new ArrayList<Boolean>());
				}
			}

			for (int i = 0; i < values.size(); i++) {
				int value = ((IntegerPrologTerm) values.get(i)).getValue()
						.intValue();
				predicateValues.get(i).add(value == 0 ? false : true);
			}

			final boolean inLoop = isLoopType && index >= loopEntry;
			final Operation operation = NONE.equals(operationTerm) ? null
					: Operation.fromPrologTerm(operationTerm);
			final CounterExampleState ceState = new CounterExampleState(index,
					stateId, operation, inLoop);
			states.add(ceState);
			index++;
		}
	}

	private CounterExampleProposition createExample(final PrologTerm structure) {
		CounterExampleProposition proposition = null;

		CompoundPrologTerm term = (CompoundPrologTerm) structure;
		String functor = term.getFunctor();
		int arity = term.getArity();

		CounterExampleValueType[] values = new CounterExampleValueType[states
				.size()];

		if (arity == 0) {
			if (functor.equals("true")) {
				Arrays.fill(values, CounterExampleValueType.TRUE);
			} else if (functor.equals("false")) {
				Arrays.fill(values, CounterExampleValueType.FALSE);
			}

			proposition = new CounterExamplePredicate(functor, pathType,
					loopEntry, Arrays.asList(values));
		} else if (arity == 1) {
			if (functor.equals("ap") || functor.equals("tp")) {
				IntegerPrologTerm atomic = (IntegerPrologTerm) term
						.getArgument(1);
				int atomicId = atomic.getValue().intValue();

				CompoundPrologTerm atomicTerm = (CompoundPrologTerm) atomics
						.get(atomicId);
				atomicTerm = (CompoundPrologTerm) atomicTerm.getArgument(1);
				String name = atomicTerm.getFunctor();

				Logger.assertProB("CounterExample invalid",
						values.length == predicateValues.get(atomicId).size());

				for (int i = 0; i < predicateValues.get(atomicId).size(); i++) {
					values[i] = predicateValues.get(atomicId).get(i) ? CounterExampleValueType.TRUE
							: CounterExampleValueType.FALSE;
				}

				proposition = functor.equals("ap") ? new CounterExamplePredicate(
						name, pathType, loopEntry, Arrays.asList(values))
						: new CounterExampleTransition(name, pathType,
								loopEntry, Arrays.asList(values));
			} else {
				CounterExampleProposition argument = createExample(term
						.getArgument(1));
				if (functor.equals("globally")) {
					proposition = new CounterExampleGlobally(pathType,
							loopEntry, argument);
				} else if (functor.equals("finally")) {
					proposition = new CounterExampleFinally(pathType,
							loopEntry, argument);
				} else if (functor.equals("next")) {
					proposition = new CounterExampleNext(pathType, loopEntry,
							argument);
				} else if (functor.equals("not")) {
					proposition = new CounterExampleNegation(pathType, loopEntry,
							argument);
				} else if (functor.equals("once")) {
					proposition = new CounterExampleOnce(pathType, loopEntry,
							argument);
				} else if (functor.equals("yesterday")) {
					proposition = new CounterExampleYesterday(pathType,
							loopEntry, argument);
				} else if (functor.equals("historically")) {
					proposition = new CounterExampleHistory(pathType,
							loopEntry, argument);
				}

				argument.setParent(proposition);
			}
		} else if (arity == 2) {
			CounterExampleProposition firstArgument = createExample(term
					.getArgument(1));
			CounterExampleProposition secondArgument = createExample(term
					.getArgument(2));

			if (functor.equals("and")) {
				proposition = new CounterExampleConjunction(pathType, loopEntry,
						firstArgument, secondArgument);
			} else if (functor.equals("or")) {
				proposition = new CounterExampleDisjunction(pathType, loopEntry,
						firstArgument, secondArgument);
			} else if (functor.equals("implies")) {
				proposition = new CounterExampleImplication(pathType, loopEntry,
						firstArgument, secondArgument);
			} else if (functor.equals("until")) {
				proposition = new CounterExampleUntil(pathType, loopEntry,
						firstArgument, secondArgument);
			} else if (functor.equals("weakuntil")) {
				proposition = new CounterExampleWeakUntil(pathType, loopEntry,
						firstArgument, secondArgument);
			} else if (functor.equals("release")) {
				proposition = new CounterExampleRelease(pathType, loopEntry,
						firstArgument, secondArgument);
			} else if (functor.equals("since")) {
				proposition = new CounterExampleSince(pathType, loopEntry,
						firstArgument, secondArgument);
			} else if (functor.equals("trigger")) {
				proposition = new CounterExampleTrigger(pathType, loopEntry,
						firstArgument, secondArgument);
			}

			firstArgument.setParent(proposition);
			secondArgument.setParent(proposition);
		}

		propositions.add(proposition);

		return proposition;
	}

	public CounterExampleProposition getPropositionRoot() {
		return propositionRoot;
	}

	public List<CounterExampleProposition> getPropositions() {
		return propositions;
	}

	public List<CounterExampleState> getStates() {
		return states;
	}

	public PathType getPathType() {
		return pathType;
	}

	public List<Operation> getFullPath() {
		List<Operation> fullPath = new ArrayList<Operation>(initPath);
		for (final CounterExampleState ceState : states) {
			fullPath.add(ceState.getOperation());
		}
		return fullPath;
	}
}
