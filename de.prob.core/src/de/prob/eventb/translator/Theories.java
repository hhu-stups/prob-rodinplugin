package de.prob.eventb.translator;

import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.basis.PORoot;
import org.eventb.internal.core.ast.extension.datatype.Datatype;
import org.eventb.internal.core.pog.POGStateRepository;
import org.eventb.internal.core.tool.state.StateRepository;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.prolog.output.IPrologTermOutput;

public class Theories {

	private static Stack<TranslateTheory> theories = new Stack<TranslateTheory>();

	public static final ITypeEnvironment global_te = FormulaFactory
			.getDefault().makeTypeEnvironment();

	private static final class TranslateTheory {
		public final ITypeEnvironment te;
		public final FormulaFactory ff;
		public final TheoryElement theory;
		private final String name;

		public TranslateTheory(String name, TheoryElement theory,
				FormulaFactory ff, ITypeEnvironment te) {
			this.name = name;
			this.theory = theory;
			this.ff = ff;
			this.te = te;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TranslateTheory) {
				TranslateTheory that = (TranslateTheory) obj;
				return this.theory.equals(that.theory);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return theory.hashCode();
		}

		@Override
		public String toString() {
			return "OP: " + name;
		}
	}

	public static void add(String name, Object t, FormulaFactory ff,
			ITypeEnvironment te) {
		if (t instanceof TheoryElement) {
			TheoryElement tx = (TheoryElement) t;
			theories.push(new TranslateTheory(name, tx, ff, te));
		}
		if (t instanceof Datatype) {
			Datatype dt = (Datatype) t;


        
			
			


			Set<IExpressionExtension> constructors = dt.getConstructors();
			for (IExpressionExtension c : constructors) {
				String symbol = c.getSyntaxSymbol();
				Object origin = c.getOrigin();
				System.out.println(origin.getClass());

			}
		}
	}

	public static void translate(IPrologTermOutput pto) throws RodinDBException {
		while (!theories.isEmpty()) {
			TranslateTheory theory = theories.pop();
			printTranslation(theory, pto);
		}
	}

	private static void printTranslation(TranslateTheory t,
			IPrologTermOutput pto) throws RodinDBException {

		if (t.theory instanceof ISCNewOperatorDefinition) {
			printOperator(t.name, (ISCNewOperatorDefinition) t.theory, t.ff,
					t.te, pto);
			return;
		}

		throw new NotImplementedException(
				"Implementation missing for theory type "
						+ t.getClass().getSimpleName());

	}

	private static void printOperator(String name,
			ISCNewOperatorDefinition theory, FormulaFactory ff,
			ITypeEnvironment te, IPrologTermOutput prologOutput)
			throws RodinDBException {

		prologOutput.openTerm("operator");
		prologOutput.printAtom(name);

		// Arguments
		prologOutput.openList();
		ISCOperatorArgument[] operatorArguments = theory.getOperatorArguments();
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

       ITypeEnvironment environment = ff.makeTypeEnvironment();

		environment.addAll(global_te);
		environment.addAll(te);

		// WD Condition
		Predicate wdCondition = theory.getWDCondition(ff, environment);
		printPredicate(ff, environment, prologOutput, wdCondition);

		// Direct Definitions
		prologOutput.openList();
		processDefinitions(ff, environment, prologOutput,
				theory.getDirectOperatorDefinitions());
		prologOutput.closeList();

		// Recursive Definitions
		prologOutput.openList();
		ISCRecursiveOperatorDefinition[] definitions = theory
				.getRecursiveOperatorDefinitions();
		for (ISCRecursiveOperatorDefinition definition : definitions) {
			ISCRecursiveDefinitionCase[] recursiveDefinitionCases = definition
					.getRecursiveDefinitionCases();
			for (ISCRecursiveDefinitionCase c : recursiveDefinitionCases) {
				Expression ex = c.getExpression(ff, environment);
				printExpression(ff, environment, prologOutput, ex);
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
			Formula scFormula = def.getSCFormula(ff, te);

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
