/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IConfidence;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.pragma.internal.ClassifiedPragma;
import de.be4.classicalb.core.parser.node.AAxiomsContextClause;
import de.be4.classicalb.core.parser.node.AConstantsContextClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AExtendsContextClause;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.ASetsContextClause;
import de.be4.classicalb.core.parser.node.ATheoremsContextClause;
import de.be4.classicalb.core.parser.node.PContextClause;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSet;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.hhu.stups.sablecc.patch.SourcePosition;
import de.prob.core.translator.TranslationFailedException;
import de.prob.core.translator.pragmas.IPragma;
import de.prob.core.translator.pragmas.UnitPragma;
import de.prob.eventb.translator.internal.EProofStatus;
import de.prob.eventb.translator.internal.ProofObligation;
import de.prob.eventb.translator.internal.SequentSource;

public final class ContextTranslator extends AbstractComponentTranslator {

	private static final SourcePosition NO_POS = new SourcePosition(-1, -1);
	private final ISCContext context;
	private final AEventBContextParseUnit model = new AEventBContextParseUnit();
	private final Map<String, ISCContext> depContext = new HashMap<String, ISCContext>();
	private final List<ProofObligation> proofs = new ArrayList<ProofObligation>();
	private final List<ClassifiedPragma> proofspragmas = new ArrayList<ClassifiedPragma>();
	private final List<IPragma> pragmas = new ArrayList<IPragma>();

	private final IEventBRoot root;
	private final FormulaFactory ff;
	private final ITypeEnvironment te;

	public static ContextTranslator create(final ISCContextRoot context)
			throws TranslationFailedException {
		try {
			assertConsistentModel(context);
			final FormulaFactory ff = context.getFormulaFactory();
			final ITypeEnvironment te = context.getTypeEnvironment(ff);
			final ContextTranslator translator = new ContextTranslator(context,
					ff, te, context);
			translator.translate();
			return translator;
		} catch (RodinDBException e) {
			throw createTranslationFailedException(context, e);
		}
	}

	public static ContextTranslator create(final ISCInternalContext context,
			final FormulaFactory ff, final ITypeEnvironment te)
			throws TranslationFailedException {
		final IEventBRoot root = getRootContext(context);
		final ContextTranslator translator = new ContextTranslator(context, ff,
				te, root);
		try {
			assertConsistentModel(context.getRoot());
			translator.translate();
		} catch (RodinDBException e) {
			throw createTranslationFailedException(context, e);
		}
		return translator;
	}

	private static IEventBRoot getRootContext(ISCInternalContext context) {
		try {
			String elementName = context.getElementName();
			IRodinProject rodinProject = context.getRodinProject();
			IRodinFile rodinFile = rodinProject.getRodinFile(elementName
					+ ".bcc");
			if (rodinFile.exists()) {
				final IInternalElement element = rodinFile.getRoot();
				if (element instanceof IEventBRoot) {
					return (IEventBRoot) element;
				}
			}
		} catch (Exception e) {
			// We do not guarantee to include proof infos. If something goes
			// wrong, we ignore the Proof info.
		}
		return null;
	}

	private static TranslationFailedException createTranslationFailedException(
			final ISCContext context, RodinDBException e)
			throws TranslationFailedException {
		final String message = "A Rodin exception occured during translation process. Possible cause: building aborted or still in progress. Please wait until building has finished before starting ProB. If this does not help, perform a clean and start ProB after building has finished. Original Exception: ";
		return new TranslationFailedException(context.getComponentName(),
				message + e.getLocalizedMessage());
	}

	private static boolean assertConsistentModel(IInternalElement machine_root)
			throws RodinDBException {
		return Assert.isTrue(machine_root.getRodinFile().isConsistent());
	}

	/**
	 * Translates a Rodin EventB Context into the ProB AST. Note, that this
	 * constructor might throw an RodinDBException, if the file is corrupted
	 * (i.e. the model is inconsistent with its underlying resource or it
	 * contains errors)
	 * 
	 * @param context
	 *            The context to translate
	 * @param ff
	 *            The FormulaFactory needed to extract the predicates
	 * @param te
	 *            The TypeEnvironment needed to extract the predicates
	 * @param root
	 *            the root to access the proofs
	 * @throws TranslationFailedException
	 */
	private ContextTranslator(final ISCContext context,
			final FormulaFactory ff, final ITypeEnvironment te,
			final IEventBRoot root) throws TranslationFailedException {
		this.context = context;
		this.ff = ff;
		this.te = te;
		this.root = root;
	}

	private void translate() throws RodinDBException {
		translateContext();
		collectProofInfo();
		collectPragmas();
	}

	private void collectPragmas() throws RodinDBException {
		// unit pragma, attached to constants
		final IAttributeType.String UNITATTRIBUTE = RodinCore
				.getStringAttrType("de.prob.units.unitPragmaAttribute");

		final ISCConstant[] constants = context.getSCConstants();

		for (final ISCConstant constant : constants) {
			if (constant.hasAttribute(UNITATTRIBUTE)) {
				String content = constant.getAttributeValue(UNITATTRIBUTE);

				if (!content.isEmpty()) {
					pragmas.add(new UnitPragma(getResource(), constant
							.getIdentifierString(), content));
				}
			}
		}
	}

	private void collectProofInfo() throws RodinDBException {
		if (root != null) {
			collectProofInfo(root);
		}
	}

	private void collectProofInfo(IEventBRoot origin) throws RodinDBException {

		IPSRoot proofStatus = origin.getPSRoot();
		IPSStatus[] statuses = proofStatus.getStatuses();

		List<String> bugs = new LinkedList<String>();

		for (IPSStatus status : statuses) {
			final int confidence = status.getConfidence();
			boolean broken = status.isBroken();

			EProofStatus pstatus = EProofStatus.UNPROVEN;

			if (!broken && confidence == IConfidence.REVIEWED_MAX)
				pstatus = EProofStatus.REVIEWED;
			if (!broken && confidence == IConfidence.DISCHARGED_MAX)
				pstatus = EProofStatus.PROVEN;

			IPOSequent sequent = status.getPOSequent();
			IPOSource[] sources = sequent.getSources();

			String name = sequent.getDescription();

			ArrayList<SequentSource> s = new ArrayList<SequentSource>(
					sources.length);
			for (IPOSource source : sources) {

				IRodinElement srcElement = source.getSource();
				if (!srcElement.exists()
						|| !(srcElement instanceof ILabeledElement)) {
					bugs.add(status.getElementName());
					break;
				}

				ILabeledElement le = (ILabeledElement) srcElement;

				s.add(new SequentSource(srcElement.getElementType(), le
						.getLabel()));

			}
			proofs.add(new ProofObligation(origin, s, name, pstatus));
		}

	}

	public AEventBContextParseUnit getContextAST() {
		return model;
	}

	public Map<String, ISCContext> getContextDependencies() {
		return depContext;
	}

	private void translateContext() throws RodinDBException {
		model.setName(new TIdentifierLiteral(context.getComponentName()));

		final List<PContextClause> clauses = new ArrayList<PContextClause>();
		clauses.add(processExtends());
		clauses.add(processConstants());
		clauses.add(processAxioms());
		clauses.add(processTheorems());
		clauses.add(processSets());
		model.setContextClauses(clauses);
	}

	private AExtendsContextClause processExtends() throws RodinDBException {

		if (context instanceof ISCContextRoot)
			return processExtendsForContextRoot();
		else if (context instanceof ISCInternalContext)
			return processExtendsForInternalContext();
		else {
			// should not be reachable
			Assert.isTrue(false);
			return null;
		}
	}

	private AExtendsContextClause processExtendsForInternalContext()
			throws RodinDBException {
		ISCInternalContext icontext = (ISCInternalContext) context;
		IExtendsContext[] extendsClauses = icontext
				.getChildrenOfType(IExtendsContext.ELEMENT_TYPE);

		try {
			extendsClauses = getSeenContexts(icontext);
		} catch (RodinDBException e) {
			// Use the default value
		}

		final List<TIdentifierLiteral> extendsList = new ArrayList<TIdentifierLiteral>(
				extendsClauses.length);

		for (final IExtendsContext extendsContext : extendsClauses) {
			final String name = extendsContext.getAbstractSCContext()
					.getComponentName();
			extendsList.add(new TIdentifierLiteral(name));
		}

		return new AExtendsContextClause(extendsList);
	}

	private IExtendsContext[] getSeenContexts(ISCInternalContext icontext)
			throws RodinDBException {
		IExtendsContext[] extendsClauses;
		String fname = icontext.getComponentName();
		IRodinFile file = icontext.getRodinProject().getRodinFile(
				fname + ".buc");
		IContextRoot root = (IContextRoot) file.getRoot();
		extendsClauses = root.getExtendsClauses();
		return extendsClauses;
	}

	private AExtendsContextClause processExtendsForContextRoot()
			throws RodinDBException {
		ISCExtendsContext[] extendsClauses = null;
		ISCContextRoot rcontext = (ISCContextRoot) context;

		extendsClauses = rcontext.getSCExtendsClauses();

		final List<TIdentifierLiteral> extendsList = new ArrayList<TIdentifierLiteral>(
				extendsClauses.length);

		for (final ISCExtendsContext extendsContext : extendsClauses) {
			ISCContextRoot root = (ISCContextRoot) extendsContext
					.getAbstractSCContext().getRoot();
			final String name = extendsContext.getAbstractSCContext()
					.getComponentName();
			extendsList.add(new TIdentifierLiteral(name));
			depContext.put(name, root);
		}
		return new AExtendsContextClause(extendsList);
	}

	private ASetsContextClause processSets() throws RodinDBException {
		final ISCCarrierSet[] carrierSets = context.getSCCarrierSets();
		final List<PSet> setList = new ArrayList<PSet>(carrierSets.length);
		for (final ISCCarrierSet carrierSet : carrierSets) {
			final ADeferredSetSet deferredSet = new ADeferredSetSet(
					Arrays.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
							carrierSet.getIdentifierString()) }));
			setList.add(deferredSet);
		}
		return new ASetsContextClause(setList);
	}

	private AConstantsContextClause processConstants() throws RodinDBException {
		final ISCConstant[] constants = context.getSCConstants();
		final List<PExpression> list = new ArrayList<PExpression>(
				constants.length);
		for (final ISCConstant constant : constants) {
			list.add(new AIdentifierExpression(Arrays
					.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
							constant.getIdentifierString()) })));
		}

		final AConstantsContextClause constantsContextClause = new AConstantsContextClause();
		constantsContextClause.setIdentifiers(list);
		return constantsContextClause;
	}

	private ATheoremsContextClause processTheorems() throws RodinDBException {
		final ISCAxiom[] axioms = context.getSCAxioms();
		final ATheoremsContextClause theoremsContextClause = new ATheoremsContextClause();
		theoremsContextClause.setPredicates(extractPredicates(axioms, true));
		return theoremsContextClause;
	}

	private AAxiomsContextClause processAxioms() throws RodinDBException {
		final ISCAxiom[] axioms = context.getSCAxioms();
		final AAxiomsContextClause axiomsContextClause = new AAxiomsContextClause();
		axiomsContextClause.setPredicates(extractPredicates(axioms, false));
		return axiomsContextClause;
	}

	private List<PPredicate> extractPredicates(final ISCAxiom[] predicates,
			final boolean theorems) throws RodinDBException {
		final List<PPredicate> list = new ArrayList<PPredicate>(
				predicates.length);
		for (final ISCAxiom element : predicates) {
			if (element.isTheorem() == theorems) {
				final PredicateVisitor visitor = new PredicateVisitor(
						new LinkedList<String>());
				element.getPredicate(ff, te).accept(visitor);
				final PPredicate predicate = visitor.getPredicate();
				list.add(predicate);
				labelMapping.put(predicate, element);
				proofspragmas.add(new ClassifiedPragma("discharged", predicate,
						Arrays.asList(new String[0]), Arrays
								.asList(new String[0]), NO_POS, NO_POS));
			}
		}
		return list;
	}

	public List<ProofObligation> getProofs() {
		return proofs;
	}

	public List<IPragma> getPragmas() {
		return pragmas;
	}

	public List<ClassifiedPragma> getProofspragmas() {
		return proofspragmas;
	}

	@Override
	public String getResource() {
		return context.getComponentName();
	}

}
