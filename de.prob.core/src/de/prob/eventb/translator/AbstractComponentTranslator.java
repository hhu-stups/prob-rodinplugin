/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCExpressionElement;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.logging.Logger;
import de.prob.core.translator.pragmas.IPragma;
import de.prob.core.translator.pragmas.UnitPragma;
import de.prob.eventb.translator.internal.ProofObligation;
import de.prob.eventb.translator.internal.TranslationVisitor;

public abstract class AbstractComponentTranslator {

	/**
	 * the proof obligations skipped during the current translation (see
	 * {@link TranslatorFactory}), by component, reported once at its end
	 */
	private static final ThreadLocal<Map<String, List<String>>> SKIPPED_PROOF_INFO = new ThreadLocal<Map<String, List<String>>>();

	/**
	 * Records that the discharged information of proof obligations of a
	 * component was skipped, because of an issue with the Rodin database.
	 * Outside a translation by {@link TranslatorFactory}, the user is told
	 * at once.
	 */
	protected static void skippedProofInfo(final String component,
			final List<String> proofObligations) {
		if (proofObligations.isEmpty())
			return;
		final Map<String, List<String>> skipped = SKIPPED_PROOF_INFO.get();
		if (skipped == null) {
			final Map<String, List<String>> single = new LinkedHashMap<String, List<String>>();
			single.put(component, proofObligations);
			reportSkippedProofInfo(component, single);
			return;
		}
		List<String> list = skipped.get(component);
		if (list == null) {
			list = new ArrayList<String>();
			skipped.put(component, list);
		}
		list.addAll(proofObligations);
	}

	/** starts collecting skipped proof information, see {@link #endTranslation} */
	static void beginTranslation() {
		SKIPPED_PROOF_INFO.set(new LinkedHashMap<String, List<String>>());
	}

	/** tells the user once, without a modal dialog, what was skipped */
	static void endTranslation(final String model) {
		final Map<String, List<String>> skipped = SKIPPED_PROOF_INFO.get();
		SKIPPED_PROOF_INFO.remove();
		if (skipped != null && !skipped.isEmpty())
			reportSkippedProofInfo(model, skipped);
	}

	private static void reportSkippedProofInfo(final String model,
			final Map<String, List<String>> skipped) {
		int count = 0;
		final StringBuilder details = new StringBuilder(
				"Skipped discharged information about:");
		for (Map.Entry<String, List<String>> entry : skipped.entrySet()) {
			count += entry.getValue().size();
			details.append("\n").append(entry.getKey()).append(": ")
					.append(String.join(", ", entry.getValue()));
		}
		Logger.notifyUserNonModal("Translation of " + model
				+ " is incomplete due to an issue with the Rodin database"
				+ " (try Project -> Clean...): the proof information of "
				+ count + (count == 1 ? " proof obligation" : " proof obligations")
				+ " was skipped. This does not affect the correctness of"
				+ " animation and model checking, but can decrease their"
				+ " performance.", details.toString());
	}

	protected final Map<Node, IInternalElement> labelMapping = new ConcurrentHashMap<Node, IInternalElement>();

	public Map<Node, IInternalElement> getLabelMapping() {
		return Collections.unmodifiableMap(labelMapping);
	}

	private final List<IPragma> pragmas = new ArrayList<IPragma>();
	private final List<ProofObligation> proofs = new ArrayList<ProofObligation>();
	private final String resourceName;

	protected AbstractComponentTranslator(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResource() {
		return resourceName;
	}

	public List<IPragma> getPragmas() {
		return Collections.unmodifiableList(pragmas);
	}

	public List<ProofObligation> getProofs() {
		return Collections.unmodifiableList(proofs);
	}

	protected void addProof(ProofObligation po) {
		proofs.add(po);
	}

	protected void addUnitPragmas(ISCIdentifierElement[] elements)
			throws RodinDBException {
		try {
			final IAttributeType.String UNITATTRIBUTE = RodinCore
					.getStringAttrType("de.prob.units.unitPragmaAttribute");

			for (final ISCIdentifierElement variable : elements) {
				if (variable.hasAttribute(UNITATTRIBUTE)) {
					String content = variable.getAttributeValue(UNITATTRIBUTE);

					if (!content.isEmpty()) {
						pragmas.add(new UnitPragma(getResource(), variable
								.getIdentifierString(), content));
					}
				}
			}
		} catch (IllegalArgumentException ex) {
			// Happens if the attribute does not exist, i.e. the unit plugin is
			// not installed
		}
	}

	protected PPredicate translatePredicate(FormulaFactory ff,
			final ITypeEnvironment env, final ISCPredicateElement predicate)
			throws CoreException {
		final Predicate pred = predicate.getPredicate(env);
		return TranslationVisitor.translatePredicate(pred);
	}

	protected PExpression translateExpression(FormulaFactory ff,
			final ITypeEnvironment env, final ISCExpressionElement expression)
			throws CoreException {
		final Expression expr = expression.getExpression(env);
		return TranslationVisitor.translateExpression(expr);
	}

	abstract public Node getAST();

}