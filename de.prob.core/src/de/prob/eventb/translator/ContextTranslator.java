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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
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
import de.be4.classicalb.core.parser.node.AAbstractConstantsContextClause;
import de.be4.classicalb.core.parser.node.AAxiomsContextClause;
import de.be4.classicalb.core.parser.node.AConstantsContextClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AExtendsContextClause;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.ASetsContextClause;
import de.be4.classicalb.core.parser.node.ATheoremsContextClause;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PContextClause;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSet;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.hhu.stups.sablecc.patch.SourcePosition;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.internal.EProofStatus;
import de.prob.eventb.translator.internal.ProofObligation;
import de.prob.eventb.translator.internal.SequentSource;
import de.prob.logging.Logger;

public final class ContextTranslator extends AbstractComponentTranslator {

	private static final SourcePosition NO_POS = new SourcePosition(-1, -1);
	private final ISCContext context;
	private final AEventBContextParseUnit model = new AEventBContextParseUnit();
	private final Map<String, ISCContext> depContext = new HashMap<String, ISCContext>();
	private final List<ClassifiedPragma> proofspragmas = new ArrayList<ClassifiedPragma>();

	private final IEventBRoot root;
	private final FormulaFactory ff;
	private final ITypeEnvironment te;

	public static ContextTranslator create(final ISCContextRoot context)
			throws TranslationFailedException {
		try {
			assertConsistentModel(context);
			final FormulaFactory ff = context.getFormulaFactory();
			final ITypeEnvironment te = context.getTypeEnvironment();
			final ContextTranslator translator = new ContextTranslator(context,
					ff, te, context);
			translator.translate();
			return translator;
		} catch (CoreException e) {
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
		} catch (CoreException e) {
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
			final ISCContext context, CoreException e)
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
		super(context.getComponentName());
		this.context = context;
		this.ff = ff;
		this.te = te;
		this.root = root;
	}

	private void translate() throws CoreException {
		translateContext();
		collectProofInfo();
		collectPragmas();
	}

	private void collectPragmas() throws RodinDBException {
		// unit pragma, attached to constants
		addUnitPragmas(context.getSCConstants());
	}

	private void collectProofInfo() throws RodinDBException {
		if (root != null) {
			collectProofInfo(root);
		}
	}

	private void collectProofInfo(IEventBRoot origin) throws RodinDBException {
		List<String> bugs = new LinkedList<String>();

		try {
			IPSRoot proofStatus = origin.getPSRoot();
			IPSStatus[] statuses = proofStatus.getStatuses();

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
				addProof(new ProofObligation(origin, s, name, pstatus));
			}
		} catch (RodinDBException e) {
			bugs.add(e.getLocalizedMessage());
		}
		
		if (!bugs.isEmpty()) {
			String message = "Translation incomplete due to a Bug in Rodin. This does not affect correctness of the Animation/Model Checking but can decrease its performance. Skipped discharged information about: "
					+ StringUtils.join(bugs, ",");
			Logger.notifyUser(message);
		}


	}

	public AEventBContextParseUnit getContextAST() {
		return model;
	}

	public Map<String, ISCContext> getContextDependencies() {
		return depContext;
	}

	private void translateContext() throws CoreException {
		model.setName(new TIdentifierLiteral(context.getComponentName()));

		final List<PContextClause> clauses = new ArrayList<PContextClause>();
		clauses.add(processExtends());
		clauses.addAll(processConstants());
		clauses.add(processAxioms());
		clauses.add(processTheorems());
		clauses.add(processSets());
		model.setContextClauses(clauses);
	}

	private AExtendsContextClause processExtends() throws CoreException {

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
			throws CoreException {
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

	private List<PContextClause> processConstants() throws RodinDBException {
		final ISCConstant[] constants = context.getSCConstants();

		final List<PExpression> concreteConstants = new ArrayList<PExpression>(
				constants.length);
		final List<PExpression> abstractConstants = new ArrayList<PExpression>(
				constants.length);

		for (final ISCConstant constant : constants) {
			boolean isAbstractConstant = false;

			// try if the attribute exists and is set
			// if the symbolic evaluation plugin is not installed
			// Rodin throws an IllegalArgumentException
			try {
				final IAttributeType.Boolean ATTRIBUTE = RodinCore
						.getBooleanAttrType("de.prob.symbolic.symbolicAttribute");
				if (constant.hasAttribute(ATTRIBUTE)) {
					isAbstractConstant = constant.getAttributeValue(ATTRIBUTE);
				}
			} catch (IllegalArgumentException E) {
				// the attribute did not exist
			}

			if (isAbstractConstant) {
				abstractConstants
						.add(new AIdentifierExpression(
								Arrays.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
										constant.getIdentifierString()) })));
			} else {
				concreteConstants
						.add(new AIdentifierExpression(
								Arrays.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
										constant.getIdentifierString()) })));
			}
		}

		final AConstantsContextClause constantsContextClause = new AConstantsContextClause();
		constantsContextClause.setIdentifiers(concreteConstants);

		final AAbstractConstantsContextClause abstractConstantsClause = new AAbstractConstantsContextClause();
		abstractConstantsClause.setIdentifiers(abstractConstants);

		return Arrays.asList(constantsContextClause, abstractConstantsClause);
	}

	private ATheoremsContextClause processTheorems() throws CoreException {
		final ISCAxiom[] axioms = context.getSCAxioms();
		final ATheoremsContextClause theoremsContextClause = new ATheoremsContextClause();
		theoremsContextClause.setPredicates(extractPredicates(axioms, true));
		return theoremsContextClause;
	}

	private AAxiomsContextClause processAxioms() throws CoreException {
		final ISCAxiom[] axioms = context.getSCAxioms();
		final AAxiomsContextClause axiomsContextClause = new AAxiomsContextClause();
		axiomsContextClause.setPredicates(extractPredicates(axioms, false));
		return axiomsContextClause;
	}

	private List<PPredicate> extractPredicates(final ISCAxiom[] predicates,
			final boolean theorems) throws CoreException {
		final List<PPredicate> list = new ArrayList<PPredicate>(
				predicates.length);
		for (final ISCAxiom element : predicates) {
			if (element.isTheorem() == theorems) {
				final PPredicate predicate = translatePredicate(ff, te, element);
				list.add(predicate);
				labelMapping.put(predicate, element);
				proofspragmas.add(new ClassifiedPragma("discharged", predicate,
						Arrays.asList(new String[0]), Arrays
								.asList(new String[0]), NO_POS, NO_POS));
			}
		}
		return list;
	}

	public List<ClassifiedPragma> getProofspragmas() {
		return proofspragmas;
	}

	@Override
	public String getResource() {
		return context.getComponentName();
	}

	@Override
	public Node getAST() {
		return getContextAST();
	}

}
