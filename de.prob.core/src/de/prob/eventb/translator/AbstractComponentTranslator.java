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
import org.eventb.core.IPSRoot;
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
	 * the problems with proof information found during the current
	 * translation (see {@link TranslatorFactory}), reported once at its end
	 */
	private static final class ProofInfoProblems {
		/** skipped proof obligations, by component */
		final Map<String, List<String>> skipped = new LinkedHashMap<String, List<String>>();
		/** the proof status files that do not exist, by component */
		final Map<String, String> missing = new LinkedHashMap<String, String>();

		boolean isEmpty() {
			return skipped.isEmpty() && missing.isEmpty();
		}
	}

	private static final ThreadLocal<ProofInfoProblems> PROBLEMS = new ThreadLocal<ProofInfoProblems>();

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
		final ProofInfoProblems problems = problems();
		List<String> list = problems.skipped.get(component);
		if (list == null) {
			list = new ArrayList<String>();
			problems.skipped.put(component, list);
		}
		list.addAll(proofObligations);
		reportUnlessCollecting(component, problems);
	}

	/**
	 * @return whether the component has a proof status file. It has none
	 *         e.g. while Rodin has not built the project, or with automatic
	 *         building switched off; then there is no proof information to
	 *         use. This is not an inconsistency, but the user is told, as
	 *         ProB may be slower.
	 */
	protected static boolean hasProofStatus(final IPSRoot proofStatus,
			final String component) {
		if (proofStatus.getRodinFile().exists())
			return true;
		final ProofInfoProblems problems = problems();
		problems.missing.put(component, proofStatus.getRodinFile()
				.getElementName());
		reportUnlessCollecting(component, problems);
		return false;
	}

	/** @return the current translation's problems, or new ones */
	private static ProofInfoProblems problems() {
		final ProofInfoProblems problems = PROBLEMS.get();
		return problems != null ? problems : new ProofInfoProblems();
	}

	private static void reportUnlessCollecting(final String component,
			final ProofInfoProblems problems) {
		if (PROBLEMS.get() != problems)
			report(component, problems);
	}

	/** starts collecting problems with proof information, see {@link #endTranslation} */
	static void beginTranslation() {
		PROBLEMS.set(new ProofInfoProblems());
	}

	/** tells the user once, without a modal dialog, about the problems */
	static void endTranslation(final String model) {
		final ProofInfoProblems problems = PROBLEMS.get();
		PROBLEMS.remove();
		if (problems != null && !problems.isEmpty())
			report(model, problems);
	}

	/**
	 * one notice for both kinds of problems, as a new notice replaces the
	 * shown one
	 */
	private static void report(final String model,
			final ProofInfoProblems problems) {
		final StringBuilder message = new StringBuilder();
		final StringBuilder details = new StringBuilder();
		if (!problems.missing.isEmpty()) {
			final List<String> names = new ArrayList<String>(
					problems.missing.keySet());
			final boolean one = names.size() == 1;
			message.append("No proof information is used for ")
					.append(String.join(", ", names))
					.append(one ? ": its proof status file (.bps) does not exist."
							: ": their proof status files (.bps) do not exist.")
					.append(" Rodin creates ").append(one ? "it" : "them")
					.append(" when building the project (is Project -> Build Automatically on?).");
			details.append("Missing proof status files:");
			for (Map.Entry<String, String> entry : problems.missing
					.entrySet())
				details.append("\n").append(entry.getKey()).append(": ")
						.append(entry.getValue());
		}
		if (!problems.skipped.isEmpty()) {
			int count = 0;
			if (details.length() > 0)
				details.append("\n\n");
			details.append("Skipped discharged information about:");
			for (Map.Entry<String, List<String>> entry : problems.skipped
					.entrySet()) {
				count += entry.getValue().size();
				details.append("\n").append(entry.getKey()).append(": ")
						.append(String.join(", ", entry.getValue()));
			}
			if (message.length() > 0)
				message.append("\n\n");
			message.append("Translation of ").append(model)
					.append(" is incomplete due to an issue with the Rodin database")
					.append(" (try Project -> Clean...): the proof information of ")
					.append(count)
					.append(count == 1 ? " proof obligation" : " proof obligations")
					.append(" was skipped.");
		}
		message.append("\n\nThis does not affect the correctness of animation")
				.append(" and model checking, but can decrease their performance.");
		Logger.notifyUserNonModal(message.toString(), details.toString());
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