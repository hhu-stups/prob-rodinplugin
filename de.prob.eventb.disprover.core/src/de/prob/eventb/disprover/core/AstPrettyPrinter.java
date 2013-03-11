package de.prob.eventb.disprover.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AAddExpression;
import de.be4.classicalb.core.parser.node.AAssignSubstitution;
import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution;
import de.be4.classicalb.core.parser.node.ABoolSetExpression;
import de.be4.classicalb.core.parser.node.ACardExpression;
import de.be4.classicalb.core.parser.node.ACartesianProductExpression;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AEvent;
import de.be4.classicalb.core.parser.node.AFinitePredicate;
import de.be4.classicalb.core.parser.node.AIntegerSetExpression;
import de.be4.classicalb.core.parser.node.AIntervalExpression;
import de.be4.classicalb.core.parser.node.AInvariantModelClause;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.ANegationPredicate;
import de.be4.classicalb.core.parser.node.APowSubsetExpression;
import de.be4.classicalb.core.parser.node.ASkipSubstitution;
import de.be4.classicalb.core.parser.node.ATruthPredicate;
import de.be4.classicalb.core.parser.node.AVariablesModelClause;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSubstitution;
import de.be4.classicalb.core.parser.node.PWitness;
import de.be4.classicalb.core.parser.node.TBool;
import de.be4.classicalb.core.parser.node.TDoubleColon;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.be4.classicalb.core.parser.node.Token;

/**
 * WARNING! This is not a complete PrettyPrinter. It only prints a few things -
 * just enough for making testing easier. Consider yourself warned.
 * 
 * @author jastram
 * 
 */
public class AstPrettyPrinter extends DepthFirstAdapter {

	StringBuilder sb = new StringBuilder();
	boolean lastWasSpace = false;

	private void write(String s) {
		lastWasSpace = false;
		sb.append(s);
	}

	private void writeSpace() {
		if (lastWasSpace)
			return;
		lastWasSpace = true;
		sb.append(" ");
	}

	@Override
	public void defaultCase(Node node) {
		if (node instanceof Token) {
			write(((Token) node).getText());
			writeSpace();
		}
		super.defaultCase(node);
	}

	@Override
	public void caseABecomesElementOfSubstitution(
			ABecomesElementOfSubstitution node) {
		inABecomesElementOfSubstitution(node);
		{
			List<PExpression> copy = new ArrayList<PExpression>(
					node.getIdentifiers());
			for (PExpression e : copy) {
				e.apply(this);
			}
		}
		write(new TDoubleColon().getText());
		writeSpace();
		if (node.getSet() != null) {
			node.getSet().apply(this);
		}
		outABecomesElementOfSubstitution(node);
	}

	@Override
	public void caseABoolSetExpression(ABoolSetExpression node) {
		write(new TBool().getText());
		writeSpace();
		super.caseABoolSetExpression(node);
	}

	public String toString() {
		return sb.toString();
	}

	@Override
	public void caseANegationPredicate(ANegationPredicate node) {
		inANegationPredicate(node);
		write("not(");
		writeSpace();
		if (node.getPredicate() != null) {
			node.getPredicate().apply(this);
		}
		outANegationPredicate(node);
		writeSpace();
		write(")");
		writeSpace();
	}

	@Override
	public void caseATruthPredicate(ATruthPredicate node) {
		writeSpace();
		write("TRUE");
		writeSpace();
	}

	public void caseAEvent(AEvent node) {
		inAEvent(node);
		write("EVENT");
		writeSpace();

		if (node.getEventName() != null) {
			node.getEventName().apply(this);
		}
		{
			List<TIdentifierLiteral> copy = new ArrayList<TIdentifierLiteral>(
					node.getRefines());
			if (copy.size() > 0) {
				write("REFINES");
				writeSpace();
			}
			for (TIdentifierLiteral e : copy) {
				e.apply(this);
			}
		}
		{
			List<PExpression> copy = new ArrayList<PExpression>(
					node.getVariables());
			if (copy.size() > 0) {
				write("ANY");
				writeSpace();
			}
			applyAndmakeAndString(copy);
		}
		{
			List<PWitness> copy = new ArrayList<PWitness>(node.getWitness());
			if (copy.size() > 0) {
				write("WITH");
				writeSpace();
			}
			applyAndmakeAndString(copy);
		}
		{
			List<PPredicate> copy = new ArrayList<PPredicate>(node.getGuards());
			if (copy.size() > 0) {
				write("WHERE");
				writeSpace();
			}
			applyAndmakeAndString(copy);
		}
		{
			List<PSubstitution> copy = new ArrayList<PSubstitution>(
					node.getAssignments());
			if (copy.size() > 0) {
				write("THEN");
				writeSpace();
			}
			applyAndmakeAndString(copy);
		}
		outAEvent(node);
		write("END");
		writeSpace();
	}

	private void applyAndmakeAndString(List<? extends Node> list) {
		for (Iterator<? extends Node> i = list.iterator(); i.hasNext();) {
			Node e = i.next();
			e.apply(this);
			if (i.hasNext()) {
				writeSpace();
				write("AND");
				writeSpace();
			}
		}
	}

	public void caseASkipSubstitution(ASkipSubstitution node) {
		write("SKIP");
		writeSpace();
	}

	@Override
	public void caseAConjunctPredicate(AConjunctPredicate node) {
		inAConjunctPredicate(node);
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		writeSpace();
		write("and");
		writeSpace();
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		outAConjunctPredicate(node);
	}

	public void caseAVariablesModelClause(AVariablesModelClause node) {
		inAVariablesModelClause(node);
		{
			List<PExpression> copy = new ArrayList<PExpression>(
					node.getIdentifiers());
			if (copy.size() > 0) {
				write("VARIABLES");
				writeSpace();
			}
			for (PExpression e : copy) {
				e.apply(this);
			}
		}
		outAVariablesModelClause(node);
	}

	public void caseAInvariantModelClause(AInvariantModelClause node) {
		inAInvariantModelClause(node);
		{
			List<PPredicate> copy = new ArrayList<PPredicate>(
					node.getPredicates());
			if (copy.size() > 0) {
				write("INVARIANTS");
				writeSpace();
			}
			for (PPredicate e : copy) {
				e.apply(this);
			}
		}
		outAInvariantModelClause(node);
	}

	@Override
	public void caseAMemberPredicate(AMemberPredicate node) {
		inAMemberPredicate(node);
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		writeSpace();
		write(":");
		writeSpace();
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		outAMemberPredicate(node);
	}

	@Override
	public void caseAAssignSubstitution(AAssignSubstitution node) {
		inAAssignSubstitution(node);
		{
			List<PExpression> copy = new ArrayList<PExpression>(
					node.getLhsExpression());
			for (PExpression e : copy) {
				e.apply(this);
			}
		}
		writeSpace();
		write(":=");
		writeSpace();
		{
			List<PExpression> copy = new ArrayList<PExpression>(
					node.getRhsExpressions());
			for (PExpression e : copy) {
				e.apply(this);
			}
		}
		outAAssignSubstitution(node);
	}

	@Override
	public void caseAIntegerSetExpression(AIntegerSetExpression node) {
		write("INT");
		writeSpace();
	}

	@Override
	public void caseAIntervalExpression(AIntervalExpression node) {
		inAIntervalExpression(node);
		if (node.getLeftBorder() != null) {
			node.getLeftBorder().apply(this);
		}
		writeSpace();
		write("..");
		writeSpace();
		if (node.getRightBorder() != null) {
			node.getRightBorder().apply(this);
		}
		outAIntervalExpression(node);
	}

	@Override
	public void caseAAddExpression(AAddExpression node) {
		inAAddExpression(node);
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		writeSpace();
		write("+");
		writeSpace();
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		outAAddExpression(node);
	}

	@Override
	public void caseACardExpression(ACardExpression node) {
		inACardExpression(node);
		write("card(");
		writeSpace();
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		writeSpace();
		write(")");
		writeSpace();
		outACardExpression(node);
	}

	@Override
	public void caseAPowSubsetExpression(APowSubsetExpression node) {
		inAPowSubsetExpression(node);
		write("POW(");
		writeSpace();
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		writeSpace();
		write(")");
		writeSpace();
		outAPowSubsetExpression(node);
	}

	@Override
	public void caseAEqualPredicate(AEqualPredicate node) {
		inAEqualPredicate(node);
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		writeSpace();
		write("=");
		writeSpace();
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		outAEqualPredicate(node);
	}

	@Override
	public void caseAFinitePredicate(AFinitePredicate node) {
		inAFinitePredicate(node);
		write("finite(");
		writeSpace();
		if (node.getSet() != null) {
			node.getSet().apply(this);
		}
		writeSpace();
		write(")");
		writeSpace();
		outAFinitePredicate(node);
	}

	@Override
	public void caseACartesianProductExpression(ACartesianProductExpression node) {
		inACartesianProductExpression(node);
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		writeSpace();
		write("x");
		writeSpace();

		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		outACartesianProductExpression(node);
	}

}
