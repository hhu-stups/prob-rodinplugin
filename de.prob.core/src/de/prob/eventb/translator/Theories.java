package de.prob.eventb.translator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
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
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.TheoryElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.internal.TranslationVisitor;
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
			// Start with an empty set of visited theories. This set is used to
			// prevent printing of a theory twice.
			final Collection<String> visitedTheories = new HashSet<String>();

			final IRodinProject rProject = project.getRodinProject();
			// It seems that we need only the theories that are referenced by
			// the theory path objects. If this is not the case, we need to
			// add the deployed theories.
			final ITheoryPathRoot[] theoryPaths = rProject
					.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
			for (ITheoryPathRoot theoryPath : theoryPaths) {
				for (IAvailableTheoryProject ap : theoryPath
						.getAvailableTheoryProjects()) {
					for (IAvailableTheory at : ap.getTheories()) {
						final IDeployedTheoryRoot deployedTheory = at
								.getDeployedTheory();
						savePrintTranslation(deployedTheory, visitedTheories,
								pout);
					}
				}
			}
		} catch (CoreException e) {
			throw new TranslationFailedException(e);
		}
	}

	/**
	 * Returns the used theories of a deployed theory. These are (at least I
	 * think so) the (deployed versions of) theories imported by the original
	 * theory.
	 * 
	 * Please note that {@link IDeployedTheoryRoot#getSCImportTheoryProjects()}
	 * does not work as it always returns an empty list.
	 * 
	 * @param deployedTheory
	 * @return An {@link Iterable} with the deployed theories that are used by
	 *         the given theory.
	 * @throws RodinDBException
	 */
	private static Iterable<IDeployedTheoryRoot> getUsedTheories(
			final IDeployedTheoryRoot deployedTheory) throws RodinDBException {
		Collection<IDeployedTheoryRoot> theories = new ArrayList<IDeployedTheoryRoot>();
		for (IUseTheory use : deployedTheory.getUsedTheories()) {
			if (use.hasUseTheory()) {
				theories.add(use.getUsedTheory());
			}
		}
		return theories;
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
			Collection<String> visitedTheories, IPrologTermOutput opto)
			throws CoreException, TranslationFailedException {
		final StructuredPrologOutput pto = new StructuredPrologOutput();
		printTranslation(theory, visitedTheories, pto);
		for (PrologTerm result : pto.getSentences()) {
			opto.printTerm(result);
		}
	}

	private static void printTranslation(IDeployedTheoryRoot theory,
			Collection<String> visitedTheories, StructuredPrologOutput pto)
			throws CoreException, TranslationFailedException {
		final String name = theory.getElementName();
		// Check if the theory has already been printed, if yes, skip it
		if (!visitedTheories.contains(name)) {
			visitedTheories.add(name);
			// First print the imported theories, this guarantees that needed
			// dependencies are printed first. (I'm not sure that ProB needs
			// that, anyway)
			Iterable<IDeployedTheoryRoot> imported = getUsedTheories(theory);
			printImportedTheories(imported, visitedTheories, pto);
			printTheory(theory, imported, pto);
			// We add a full stop, because we want to build a list of terms that
			// will be printed after everything went well.
			pto.fullstop();
		}
	}

	private static void printImportedTheories(
			Iterable<IDeployedTheoryRoot> theories,
			Collection<String> visitedTheories, StructuredPrologOutput pto)
			throws CoreException, TranslationFailedException {
		for (IDeployedTheoryRoot theory : theories) {
			printTranslation(theory, visitedTheories, pto);
		}
	}

	private static void printTheory(IDeployedTheoryRoot theory,
			Iterable<IDeployedTheoryRoot> imported, StructuredPrologOutput pto)
			throws CoreException, TranslationFailedException {
		pto.openTerm("theory");
		printTheoryName(theory, pto);
		printListOfImportedTheories(imported, pto);
		printIdentifiers(theory.getSCTypeParameters(), pto);
		printDataTypes(theory, pto);
		printOperatorDefs(theory, pto);
		printAxiomaticDefs(theory, pto);
		findProBMappingFile(theory, pto);
		pto.closeTerm();
	}

	private static void printListOfImportedTheories(
			Iterable<IDeployedTheoryRoot> imported, StructuredPrologOutput pto)
			throws RodinDBException {
		pto.openList();
		for (IDeployedTheoryRoot theory : imported) {
			printTheoryName(theory, pto);
		}
		pto.closeList();
	}

	private static void printTheoryName(IDeployedTheoryRoot theory,
			IPrologTermOutput pto) {
		pto.openTerm("theory_name");
		pto.printAtom(theory.getRodinProject().getElementName());
		pto.printAtom(theory.getElementName());
		pto.closeTerm();
	}

	/**
	 * Each theory might have a ProB Mapping File which describes how ProB
	 * should handle operators of the theory. We look if there is a special
	 * named file in the same directory as the original theory file.
	 * 
	 * E.g. if the theory is "example" in the project "P" we look for a file
	 * named "example.ptm" in "P".
	 * 
	 * If a mapping file is found, it contents is processed and printed to the
	 * Prolog output.
	 * 
	 * @param theory
	 * @param pto
	 * @throws TranslationFailedException
	 */
	private static void findProBMappingFile(ISCTheoryRoot theory,
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
			ISCTheoryRoot theory) throws TranslationFailedException {
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

	private static void printDataTypes(ISCTheoryRoot theory,
			IPrologTermOutput pto) throws CoreException {
		final FormulaFactory ff = theory.getFormulaFactory();
		pto.openList();
		for (ISCDatatypeDefinition def : theory.getSCDatatypeDefinitions()) {
			printDataType(def, ff, pto);
		}
		pto.closeList();
	}

	private static void printDataType(ISCDatatypeDefinition def,
			FormulaFactory ff, IPrologTermOutput pto) throws CoreException {
		pto.openTerm("datatype");
		pto.printAtom(def.getIdentifierString());
		pto.openList();
		for (ISCTypeArgument arg : def.getTypeArguments()) {
			printType(arg.getSCGivenType(ff), pto);
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
			FormulaFactory ff, IPrologTermOutput pto) throws CoreException {
		pto.openTerm("constructor");
		pto.printAtom(cons.getIdentifierString());
		pto.openList();
		for (ISCConstructorArgument arg : cons.getConstructorArguments()) {
			printTypedIdentifier("destructor", arg, ff, pto);
		}
		pto.closeList();
		pto.closeTerm();
	}

	private static void printOperatorDefs(ISCTheoryRoot theory,
			IPrologTermOutput pto) throws RodinDBException {
		pto.openList();
		for (ISCNewOperatorDefinition opdef : theory
				.getSCNewOperatorDefinitions()) {
			printOperator(opdef, theory, pto);
		}
		pto.closeList();
	}

	private static void printOperator(ISCNewOperatorDefinition opDef,
			ISCTheoryRoot theory, IPrologTermOutput prologOutput)
			throws RodinDBException {

		prologOutput.openTerm("operator");
		prologOutput.printAtom(opDef.getLabel());

		final FormulaFactory ff = theory.getFormulaFactory();
		final ITypeEnvironment te = theory.getTypeEnvironment(ff);

		// Arguments
		printOperatorArguments(opDef.getOperatorArguments(), prologOutput, ff);
		for (ISCOperatorArgument arg : opDef.getOperatorArguments()) {
			te.add(arg.getIdentifier(ff));
		}

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
			final String indArg = definition.getInductiveArgument();
			ISCRecursiveDefinitionCase[] recursiveDefinitionCases = definition
					.getRecursiveDefinitionCases();
			for (ISCRecursiveDefinitionCase c : recursiveDefinitionCases) {
				printRecDefCase(indArg, prologOutput, ff, c);
			}
		}
		prologOutput.closeList();
		prologOutput.closeTerm();
	}

	private static void printRecDefCase(String indArg,
			IPrologTermOutput prologOutput, final FormulaFactory ff,
			ISCRecursiveDefinitionCase c) throws RodinDBException {
		final String es = c.getExpressionString();
		final IParseResult pr = ff.parseExpression(es, null);
		final Expression ex = pr.getParsedExpression();

		final String formulaAsString = c
				.getAttributeValue(TheoryAttributes.FORMULA_ATTRIBUTE);
		Formula<?> formula = TheoryElement.parseFormula(formulaAsString, ff,
				false);

		prologOutput.openTerm("case");
		prologOutput.printAtom(indArg);
		prologOutput.openList();
		for (FreeIdentifier fi : ex.getFreeIdentifiers()) {
			prologOutput.printAtom(fi.getName());
		}
		prologOutput.closeList();
		printExpression(prologOutput, ex);
		if (formula instanceof Predicate) {
			printPredicate(prologOutput, (Predicate) formula);
		} else if (formula instanceof Expression) {
			printExpression(prologOutput, (Expression) formula);
		} else {
			throw new IllegalStateException("unexpected formula of type "
					+ formula.getClass().getName()
					+ " for recursive definition case in theory");
		}
		prologOutput.closeTerm();
	}

	private static void printOperatorArguments(ISCOperatorArgument[] arguments,
			IPrologTermOutput prologOutput, final FormulaFactory ff)
			throws CoreException {
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
			final IPrologTermOutput pto) throws CoreException {
		pto.openTerm(functor);
		pto.printAtom(id.getIdentifierString());
		Type type = id.getType(ff);
		printType(type, pto);
		pto.closeTerm();
	}

	private static void printType(final Type type, final IPrologTermOutput pto) {
		printExpression(pto, type.toExpression());
	}

	private static void printExpression(IPrologTermOutput prologOutput,
			Expression ee) {
		final PExpression expr = TranslationVisitor.translateExpression(ee);
		final ASTProlog pv = new ASTProlog(prologOutput, null);
		expr.apply(pv);
	}

	private static void printPredicate(IPrologTermOutput prologOutput,
			Predicate pp) {
		final PPredicate predicate = TranslationVisitor.translatePredicate(pp);
		final ASTProlog pv = new ASTProlog(prologOutput, null);
		predicate.apply(pv);
	}

	private static void printAxiomaticDefs(ISCTheoryRoot theory,
			IPrologTermOutput pto) throws CoreException {
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
			ITypeEnvironment te, IPrologTermOutput pto) throws CoreException {
		pto.openTerm("axiomatic_def_block");
		pto.printAtom(block.getLabel());
		printIdentifiers(block.getAxiomaticTypeDefinitions(), pto);
		printAxiomaticOperators(block.getAxiomaticOperatorDefinitions(), ff,
				pto);
		printAxioms(block.getAxiomaticDefinitionAxioms(), te, pto);
		pto.closeTerm();
	}

	private static void printAxiomaticOperators(
			ISCAxiomaticOperatorDefinition[] axdefs, FormulaFactory ff,
			IPrologTermOutput pto) throws CoreException {
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
			ITypeEnvironment te, IPrologTermOutput pto) throws CoreException {
		pto.openList();
		for (ISCAxiomaticDefinitionAxiom axiom : axioms) {
			printPredicate(pto, axiom.getPredicate(te));
		}
		pto.closeList();
	}

	public static void touch() throws NoClassDefFoundError {
		// Just some dummy code to check if the theory plugin is installed
		@SuppressWarnings("unused")
		String extension = DatabaseUtilities.DEPLOYED_THEORY_FILE_EXTENSION;
	}
}
