/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.AIntegerPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class ComputeCoverageCommand implements IComposableCommand {

	private ComputeCoverageResult coverageResult;

	public final static class ComputeCoverageResult {
		private final BigInteger totalNumberOfNodes;
		private final BigInteger totalNumberOfTransitions;
		private final List<String> ops = new ArrayList<String>();
		private final List<String> nodes = new ArrayList<String>();
		private final List<String> uncovered = new ArrayList<String>();

		@Deprecated
		public ComputeCoverageResult(final IntegerPrologTerm totalNumberOfNodes,
				final IntegerPrologTerm totalNumberOfTransitions, final ListPrologTerm ops, final ListPrologTerm nodes,
				final ListPrologTerm uncovered) {
			this((AIntegerPrologTerm) totalNumberOfNodes, totalNumberOfTransitions, ops, nodes, uncovered);
		}

		public ComputeCoverageResult(final AIntegerPrologTerm totalNumberOfNodes,
				final AIntegerPrologTerm totalNumberOfTransitions, final ListPrologTerm ops, final ListPrologTerm nodes,
				final ListPrologTerm uncovered) {
			this.totalNumberOfNodes = totalNumberOfNodes.getValue();
			this.totalNumberOfTransitions = totalNumberOfTransitions.getValue();
			for (PrologTerm op : ops) {
				this.getOps().add(op.getFunctor());
			}
			for (PrologTerm node : nodes) {
				this.getNodes().add(node.getFunctor());
			}
			for (PrologTerm unc : uncovered) {
				this.getUncovered().add(unc.getFunctor());
			}

		}

		public BigInteger getTotalNumberOfNodes() {
			return totalNumberOfNodes;
		}

		public BigInteger getTotalNumberOfTransitions() {
			return totalNumberOfTransitions;
		}

		public List<String> getOps() {
			return ops;
		}

		public List<String> getNodes() {
			return nodes;
		}

		public List<String> getUncovered() {
			return uncovered;
		}
	}

	private ComputeCoverageCommand() {
	}

	public static ComputeCoverageResult getCoverage(final Animator a) throws ProBException {
		ComputeCoverageCommand computeCoverageCommand = new ComputeCoverageCommand();
		a.execute(computeCoverageCommand);
		return computeCoverageCommand.getResult();
	}

	private ComputeCoverageResult getResult() {
		return coverageResult;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) throws CommandException {

		AIntegerPrologTerm totalNodeNr = (AIntegerPrologTerm) bindings.get("TotalNodeNr");
		AIntegerPrologTerm totalTransNr = (AIntegerPrologTerm) bindings.get("TotalTransSum");

		ListPrologTerm ops = (ListPrologTerm) bindings.get("OpStat");
		ListPrologTerm nodes = (ListPrologTerm) bindings.get("NodeStat");
		ListPrologTerm uncovered = (ListPrologTerm) bindings.get("Uncovered");
		coverageResult = new ComputeCoverageResult(totalNodeNr, totalTransNr, ops, nodes, uncovered);

	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("compute_coverage").printVariable("TotalNodeNr").printVariable("TotalTransSum")
				.printVariable("NodeStat").printVariable("OpStat").printVariable("Uncovered").closeTerm();
	}

}
