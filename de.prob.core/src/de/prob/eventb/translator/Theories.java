package de.prob.eventb.translator;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.prolog.output.IPrologTermOutput;

public class Theories {

	private static Map<String, IDeployedTheoryRoot> theories = new TreeMap<String, IDeployedTheoryRoot>();

	public static final ITypeEnvironment global_te = FormulaFactory
			.getDefault().makeTypeEnvironment();

	public static void addOrigin(Object origin) {

		if (origin instanceof ISCNewOperatorDefinition) {
			final IDeployedTheoryRoot theory = (IDeployedTheoryRoot) ((ISCNewOperatorDefinition) origin)
					.getParent();
			final String name = theory.getElementName();
			theories.put(name, theory);
		}

	}

	public static void translate(IPrologTermOutput pto) throws RodinDBException {
		for (IDeployedTheoryRoot theory : theories.values()) {
			printTranslation(theory, pto);
		}
	}

	private static void printTranslation(IDeployedTheoryRoot theory,
			IPrologTermOutput pto) throws RodinDBException {

		pto.openTerm("theory");
		printOperatorDefs(theory, pto);
		pto.closeTerm();
	}

	private static void printOperatorDefs(IDeployedTheoryRoot theory,
			IPrologTermOutput pto) throws RodinDBException {
		pto.openList();
		for (ISCNewOperatorDefinition opdef : theory
				.getSCNewOperatorDefinitions()) {
			printOperator(opdef, theory, pto);

		}
		pto.closeList();
	}

	private static void printOperator(ISCNewOperatorDefinition opDef,
			IDeployedTheoryRoot theory, IPrologTermOutput prologOutput)
			throws RodinDBException {

		prologOutput.openTerm("operator");
		prologOutput.printAtom(opDef.getLabel());

		final FormulaFactory ff = theory.getFormulaFactory();
		final ITypeEnvironment te = theory.getTypeEnvironment(ff);

		// Arguments
		prologOutput.openList();
		ISCOperatorArgument[] operatorArguments = opDef.getOperatorArguments();
		for (ISCOperatorArgument argument : operatorArguments) {
			FreeIdentifier identifier = argument.getIdentifier(ff);
			te.add(identifier);
			String arg = identifier.getName();
			Expression type = identifier.getType().toExpression(ff);
			prologOutput.openTerm("argument");
			prologOutput.printAtom(arg);
			printExpression(ff, te, prologOutput, type);
			prologOutput.closeTerm();
		}
		prologOutput.closeList();

		// WD Condition
		Predicate wdCondition = opDef.getWDCondition(ff, te);
		printPredicate(ff, te, prologOutput, wdCondition);

		// Direct Definitions
		prologOutput.openList();
		processDefinitions(ff, te, prologOutput,
				opDef.getDirectOperatorDefinitions());
		prologOutput.closeList();

		// Recursive Definitions
		prologOutput.openList();
		ISCRecursiveOperatorDefinition[] definitions = opDef
				.getRecursiveOperatorDefinitions();
		for (ISCRecursiveOperatorDefinition definition : definitions) {
			ISCRecursiveDefinitionCase[] recursiveDefinitionCases = definition
					.getRecursiveDefinitionCases();
			for (ISCRecursiveDefinitionCase c : recursiveDefinitionCases) {
				Expression ex = c.getExpression(ff, te);
				printExpression(ff, te, prologOutput, ex);
			}
		}
		prologOutput.closeList();

		prologOutput.closeTerm();
	}

	private static void processDefinitions(FormulaFactory ff,
			ITypeEnvironment te, IPrologTermOutput prologOutput,
			ISCDirectOperatorDefinition[] directOperatorDefinitions)
			throws RodinDBException {
		for (ISCDirectOperatorDefinition def : directOperatorDefinitions) {
			Formula<?> scFormula = def.getSCFormula(ff, te);

			if (scFormula instanceof Predicate) {
				Predicate pp = (Predicate) scFormula;
				printPredicate(ff, te, prologOutput, pp);
			}
			if (scFormula instanceof Expression) {
				Expression pp = (Expression) scFormula;
				printExpression(ff, te, prologOutput, pp);
			}

		}
	}

	private static void printExpression(FormulaFactory ff, ITypeEnvironment te,
			IPrologTermOutput prologOutput, Expression pp) {
		ExpressionVisitor visitor = new ExpressionVisitor(
				new LinkedList<String>(), ff, te);
		pp.accept(visitor);
		PExpression ex = visitor.getExpression();
		ASTProlog pv = new ASTProlog(prologOutput, null);
		ex.apply(pv);
	}

	private static void printPredicate(FormulaFactory ff, ITypeEnvironment te,
			IPrologTermOutput prologOutput, Predicate pp) {
		PredicateVisitor visitor = new PredicateVisitor(
				new LinkedList<String>(), ff, te);
		pp.accept(visitor);
		PPredicate predicate = visitor.getPredicate();
		ASTProlog pv = new ASTProlog(prologOutput, null);
		predicate.apply(pv);
	}

}
