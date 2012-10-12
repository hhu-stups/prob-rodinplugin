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
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
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
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IConfidence;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
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
import de.prob.eventb.translator.internal.DischargedProof;
import de.prob.logging.Logger;

public final class ContextTranslator extends AbstractComponentTranslator {

	private static final SourcePosition NO_POS = new SourcePosition(-1, -1);
	private final ISCContext context;
	private final AEventBContextParseUnit model = new AEventBContextParseUnit();
	private final Map<String, ISCContext> depContext = new HashMap<String, ISCContext>();
	private final List<DischargedProof> proofs = new ArrayList<DischargedProof>();
	private final List<ClassifiedPragma> proofspragmas = new ArrayList<ClassifiedPragma>();
	private final FormulaFactory ff;
	private ITypeEnvironment te;

	public static ContextTranslator create(final ISCContext context)
			throws TranslationFailedException {
		ContextTranslator contextTranslator = new ContextTranslator(context);
		try {
			(new TheoryTranslator()).translate();
			contextTranslator.translate();
		} catch (RodinDBException e) {
			final String message = "A Rodin exception occured during translation process. Possible cause: building aborted or still in progress. Please wait until building has finished before starting ProB. If this does not help, perform a clean and start ProB after building has finished. Original Exception: ";
			throw new TranslationFailedException(context.getComponentName(),
					message + e.getLocalizedMessage());
		}
		return contextTranslator;
	}

	/**
	 * Translates a Rodin EventB Context into the ProB AST. Note, that this
	 * constructor might throw an RodinDBException, if the file is corrupted
	 * (i.e. the model is inconsistent with its underlying resource or it
	 * contains errors)
	 * 
	 * @param context
	 * @throws TranslationFailedException
	 * @throws RodinDBException
	 */
	private ContextTranslator(final ISCContext context)
			throws TranslationFailedException {
		this.context = context;
		ff = ((ISCContextRoot) context).getFormulaFactory();
		try {
			te = ((ISCContextRoot) context).getTypeEnvironment(ff);
		} catch (RodinDBException e) {
			final String message = "A Rodin exception occured during translation process. Original Exception: ";
			throw new TranslationFailedException(context.getComponentName(),
					message + e.getLocalizedMessage());
		}
	}

	private void translate() throws RodinDBException {
		if (context instanceof ISCContextRoot) {
			ISCContextRoot context_root = (ISCContextRoot) context;
			Assert.isTrue(context_root.getRodinFile().isConsistent());
			te = context_root.getTypeEnvironment(ff);
		} else if (context instanceof ISCInternalContext) {
			ISCInternalContext context_internal = (ISCInternalContext) context;

			try {

				String elementName = context_internal.getElementName();
				IRodinProject rodinProject = context_internal.getRodinProject();
				IRodinFile rodinFile = rodinProject.getRodinFile(elementName
						+ ".bcc");
				if (rodinFile.exists()) {
					ISCContextRoot root = (ISCContextRoot) rodinFile.getRoot();
					collectProofInfo(root);
				}
			} catch (Exception e) {
				// We do not guarantee to include proof infos. If something goes
				// wrong, we ignore the Proof info.
			}

			ISCMachineRoot machine_root = (ISCMachineRoot) context_internal
					.getRoot();
			Assert.isTrue(machine_root.getRodinFile().isConsistent());
		}
		translateContext();

	}

	private void collectProofInfo(ISCContextRoot context_root)
			throws RodinDBException {

		IPSRoot proofStatus = context_root.getPSRoot();
		IPSStatus[] statuses = proofStatus.getStatuses();

		List<String> bugs = new LinkedList<String>();

		for (IPSStatus status : statuses) {
			final int confidence = status.getConfidence();
			boolean broken = status.isBroken();
			if (!broken && confidence == IConfidence.DISCHARGED_MAX) {
				IPOSequent sequent = status.getPOSequent();
				IPOSource[] sources = sequent.getSources();

				for (IPOSource source : sources) {

					IRodinElement srcElement = source.getSource();
					if (!srcElement.exists()) {
						bugs.add(status.getElementName());
						break;
					}

					if (srcElement instanceof IAxiom) {
						IAxiom tmp = (IAxiom) srcElement;
						if (((IContextRoot) tmp.getParent())
								.equals(context_root.getContextRoot())) {
							proofs.add(new DischargedProof(context_root, tmp,
									null));
						}
					}
				}
			}
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
			if (element.isTheorem() != theorems) {
				continue;
			}
			final PredicateVisitor visitor = new PredicateVisitor(
					new LinkedList<String>());
			element.getPredicate(ff, te).accept(visitor);
			final PPredicate predicate = visitor.getPredicate();
			list.add(predicate);
			labelMapping.put(predicate, element);
			proofspragmas.add(new ClassifiedPragma("discharged", predicate,
					Arrays.asList(new String[0]), Arrays.asList(new String[0]),
					NO_POS, NO_POS));
		}
		return list;
	}

	public List<DischargedProof> getProofs() {
		return proofs;
	}

	public List<ClassifiedPragma> getProofspragmas() {
		return proofspragmas;
	}

	@Override
	public String getResource() {
		return context.getComponentName();
	}

}
