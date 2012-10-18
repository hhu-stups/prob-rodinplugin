package de.prob.eventb.translator;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ISCTypeParameter;
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
		} else {
			System.out.println("Did not register origin: "
					+ origin.getClass().getName());
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
		printTypeParameters(theory, pto);
		printDataTypes(theory, pto);
		printOperatorDefs(theory, pto);
		pto.closeTerm();
	}

	private static void printTypeParameters(IFormulaExtensionsSource theory,
			IPrologTermOutput pto) throws RodinDBException {
		pto.openList();
		for (ISCTypeParameter parameter : theory.getSCTypeParameters()) {
			pto.printAtom(parameter.getIdentifierString());
		}
		pto.closeList();
	}

	private static void printDataTypes(IDeployedTheoryRoot theory,
			IPrologTermOutput pto) throws RodinDBException {
		final FormulaFactory ff = theory.getFormulaFactory();
		pto.openList();
		for (ISCDatatypeDefinition def : theory.getSCDatatypeDefinitions()) {
			printDataType(def, ff, pto);
		}
		pto.closeList();
	}

	private static void printDataType(ISCDatatypeDefinition def,
			FormulaFactory ff, IPrologTermOutput pto) throws RodinDBException {
		pto.openTerm("datatype");
		pto.printAtom(def.getIdentifierString());
		pto.openList();
		for (ISCTypeArgument arg : def.getTypeArguments()) {
			printType(arg.getSCGivenType(ff), ff, pto);
		}
		pto.closeList();
		pto.openList();
		for (ISCDatatypeConstructor cons : def.getConstructors()) {
			printConstructor(cons, ff, pto);
		}
		pto.closeList();
		pto.closeTerm();

	}

	private static void printConstructor(ISCDatatypeConstructor cons,
			FormulaFactory ff, IPrologTermOutput pto) throws RodinDBException {
		pto.openTerm("constructor");
		pto.printAtom(cons.getIdentifierString());
		pto.openList();
		for (ISCConstructorArgument arg : cons.getConstructorArguments()) {
			printTypedIdentifier("destructor", arg, ff, pto);
		}
		pto.closeList();
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
			printTypedIdentifier("argument", argument, ff, prologOutput);
		}
		prologOutput.closeList();

		// WD Condition
		Predicate wdCondition = opDef.getWDCondition(ff, te);
		printPredicate(prologOutput, wdCondition);

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
				printExpression(prologOutput, ex);
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
				printPredicate(prologOutput, pp);
			}
			if (scFormula instanceof Expression) {
				Expression pp = (Expression) scFormula;
				printExpression(prologOutput, pp);
			}

		}
	}

	private static void printTypedIdentifier(final String functor,
			final ISCIdentifierElement id, final FormulaFactory ff,
			final IPrologTermOutput pto) throws RodinDBException {
		pto.openTerm(functor);
		pto.printAtom(id.getIdentifierString());
		printType(id.getType(ff), ff, pto);
		pto.closeTerm();
	}

	private static void printType(final Type type, final FormulaFactory ff,
			final IPrologTermOutput pto) {
		printExpression(pto, type.toExpression(ff));
	}

	private static void printExpression(IPrologTermOutput prologOutput,
			Expression pp) {
		ExpressionVisitor visitor = new ExpressionVisitor(
				new LinkedList<String>());
		pp.accept(visitor);
		PExpression ex = visitor.getExpression();
		ASTProlog pv = new ASTProlog(prologOutput, null);
		ex.apply(pv);
	}

	private static void printPredicate(IPrologTermOutput prologOutput,
			Predicate pp) {
		PredicateVisitor visitor = new PredicateVisitor(
				new LinkedList<String>());
		pp.accept(visitor);
		PPredicate predicate = visitor.getPredicate();
		ASTProlog pv = new ASTProlog(prologOutput, null);
		predicate.apply(pv);
	}

}
