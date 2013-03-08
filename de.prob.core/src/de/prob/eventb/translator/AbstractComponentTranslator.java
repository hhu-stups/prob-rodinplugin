/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import de.prob.core.translator.pragmas.IPragma;
import de.prob.core.translator.pragmas.UnitPragma;
import de.prob.eventb.translator.internal.ProofObligation;
import de.prob.eventb.translator.internal.TranslationVisitor;

public abstract class AbstractComponentTranslator {

	protected final Map<Node, IInternalElement> labelMapping = new ConcurrentHashMap<Node, IInternalElement>();

	public Map<Node, IInternalElement> getLabelMapping() {
		return Collections.unmodifiableMap(labelMapping);
	}

	private boolean theoryIsUsed = false;
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
			throws RodinDBException {
		final PredicateVisitor visitor = new PredicateVisitor(
				new LinkedList<String>());
		final Predicate pred = predicate.getPredicate(ff, env);
		pred.accept(visitor);
		final PPredicate result = visitor.getPredicate();
		theoryIsUsed |= TranslationVisitor.checkNewImplementation(pred, result);
		return result;
	}

	protected PExpression translateExpression(FormulaFactory ff,
			final ITypeEnvironment env, final ISCExpressionElement expression)
			throws RodinDBException {
		final ExpressionVisitor visitor = new ExpressionVisitor(
				new LinkedList<String>());
		final Expression expr = expression.getExpression(ff, env);
		expr.accept(visitor);
		final PExpression result = visitor.getExpression();
		theoryIsUsed |= TranslationVisitor.checkNewImplementation(expr, result);
		return result;
	}

	public boolean isTheoryUsed() {
		return theoryIsUsed;
	}

	abstract public Node getAST();

}