package de.prob.eventb.translator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.core.IEventBProject;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ITheoryPathRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.core.translator.TranslationFailedException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.tmparser.OperatorMapping;
import de.prob.tmparser.TheoryMappingException;
import de.prob.tmparser.TheoryMappingParser;


public class Theories {
	private static final String PROB_THEORY_MAPPING_SUFFIX = "ptm";

	public static void translate(IEventBProject project, IPrologTermOutput pout)
			throws TranslationFailedException {
		try {
			final IRodinProject rProject = project.getRodinProject();
			final IDeployedTheoryRoot[] theories = rProject
					.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
			for (IDeployedTheoryRoot theory : theories) {
				savePrintTranslation(theory, pout);
			}
			final ITheoryPathRoot[] theoryPaths = rProject
					.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
			for (ITheoryPathRoot theoryPath : theoryPaths) {
				for (IAvailableTheoryProject ap : theoryPath
						.getAvailableTheoryProjects()) {
					for (IAvailableTheory at : ap.getTheories()) {
						savePrintTranslation(at.getDeployedTheory(), pout);
					}
				}
			}
		} catch (RodinDBException e) {
			throw new TranslationFailedException(e);
		}
	}

	/**
	 * We currently write the translated theory into a PrologTerm object because
	 * the translation is currently very unstable and erroneous. Writing in a
	 * Prolog object makes sure that the output stream to the Prolog process
	 * will not be corrupted.
	 * 
	 * @throws TranslationFailedException
	 */
	private static void savePrintTranslation(IDeployedTheoryRoot theory,
			IPrologTermOutput opto) throws RodinDBException,
			TranslationFailedException {

		final StructuredPrologOutput pto = new StructuredPrologOutput();
		printTranslation(theory, pto);
		pto.fullstop();
		final PrologTerm result = pto.getSentences().get(0);
		opto.printTerm(result);
	}

	private static void printTranslation(IDeployedTheoryRoot theory,
			StructuredPrologOutput pto) throws RodinDBException,
			TranslationFailedException {
		pto.openTerm("theory");
		printIdentifiers(theory.getSCTypeParameters(), pto);
		printDataTypes(theory, pto);
		printOperatorDefs(theory, pto);
		printAxiomaticDefs(theory, pto);
		findProBMappingFile(theory, pto);
		pto.closeTerm();
	}

	private static void findProBMappingFile(IDeployedTheoryRoot theory,
			IPrologTermOutput pto) throws TranslationFailedException {
		final String theoryName = theory.getComponentName();
		final IPath path = new Path(theoryName + "."
				+ PROB_THEORY_MAPPING_SUFFIX);
		final IProject project = theory.getRodinProject().getProject();
		final Collection<OperatorMapping> mappings;
		if (project.exists(path)) {
			final IFile file = project.getFile(path);
			mappings = readMappingFile(file, theory);
		} else {
			mappings = Collections.emptyList();
		}
		printMappings(mappings, pto);
	}

	private static Collection<OperatorMapping> readMappingFile(IFile file,
			IDeployedTheoryRoot theory) throws TranslationFailedException {
		try {
			final InputStream input = file.getContents();
			final String name = theory.getComponentName();
			final Reader reader = new InputStreamReader(input);
			return TheoryMappingParser.parseTheoryMapping(name, reader);
		} catch (CoreException e) {
			throw new TranslationFailedException(e);
		} catch (TheoryMappingException e) {
			throw new TranslationFailedException(e);
		} catch (IOException e) {
			throw new TranslationFailedException(e);
		}
	}

	private static void printMappings(Collection<OperatorMapping> mappings,
			IPrologTermOutput pto) {
		pto.openList();
		// Currently, we support only one kind of operator mapping, just tagging
		// an operator to indicate that an optimized ProB implementation should
		// be used. We do not invest any effort in preparing future kinds of
		// other operator mappings.
		for (OperatorMapping mapping : mappings) {
			pto.openTerm("tag");
			pto.printAtom(mapping.getOperatorName());
			pto.printAtom(mapping.getSpec());
			pto.closeTerm();
		}
		pto.closeList();
	}

	private static void printIdentifiers(ISCIdentifierElement[] identifiers,
			IPrologTermOutput pto) throws RodinDBException {
		pto.openList();
		for (ISCIdentifierElement identifier : identifiers) {
			pto.printAtom(identifier.getIdentifierString());
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
		printOperatorArguments(opDef.getOperatorArguments(), prologOutput, ff);

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

	private static void printOperatorArguments(ISCOperatorArgument[] arguments,
			IPrologTermOutput prologOutput, final FormulaFactory ff)
			throws RodinDBException {
		prologOutput.openList();
		for (ISCOperatorArgument argument : arguments) {
			printTypedIdentifier("argument", argument, ff, prologOutput);
		}
		prologOutput.closeList();
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
		Type type = id.getType(ff);
		printType(type, ff, pto);
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

	private static void printAxiomaticDefs(IDeployedTheoryRoot theory,
			IPrologTermOutput pto) throws RodinDBException {
		FormulaFactory ff = theory.getFormulaFactory();
		ITypeEnvironment te = theory.getTypeEnvironment(ff);
		pto.openList();
		for (final ISCAxiomaticDefinitionsBlock block : theory
				.getSCAxiomaticDefinitionsBlocks()) {
			printAxiomaticDefBlock(block, ff, te, pto);
		}
		pto.closeList();
	}

	private static void printAxiomaticDefBlock(
			ISCAxiomaticDefinitionsBlock block, FormulaFactory ff,
			ITypeEnvironment te, IPrologTermOutput pto) throws RodinDBException {
		pto.openTerm("axiomatic_def_block");
		pto.printAtom(block.getLabel());
		printIdentifiers(block.getAxiomaticTypeDefinitions(), pto);
		printAxiomaticOperators(block.getAxiomaticOperatorDefinitions(), ff,
				pto);
		printAxioms(block.getAxiomaticDefinitionAxioms(), ff, te, pto);
		pto.closeTerm();
	}

	private static void printAxiomaticOperators(
			ISCAxiomaticOperatorDefinition[] axdefs, FormulaFactory ff,
			IPrologTermOutput pto) throws RodinDBException {
		pto.openList();
		for (final ISCAxiomaticOperatorDefinition opdef : axdefs) {
			pto.openTerm("opdef");
			pto.printAtom(opdef.getLabel()); // The label seems to be the
												// operator name
			printOperatorArguments(opdef.getOperatorArguments(), pto, ff);
			pto.openList();
			// WD condition missing
			pto.closeList();
			pto.closeTerm();
		}
		pto.closeList();
	}

	private static void printAxioms(ISCAxiomaticDefinitionAxiom[] axioms,
			FormulaFactory ff, ITypeEnvironment te, IPrologTermOutput pto)
			throws RodinDBException {
		pto.openList();
		for (ISCAxiomaticDefinitionAxiom axiom : axioms) {
			printPredicate(pto, axiom.getPredicate(ff, te));
		}
		pto.closeList();
	}

	public static void touch() throws NoClassDefFoundError {
		// Just some dummy code to check if the theory plugin is installed
		@SuppressWarnings("unused")
		String extension = DatabaseUtilities.DEPLOYED_THEORY_FILE_EXTENSION;
	}
}
