package de.prob.eventb.disprover.core.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;

import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.eventb.translator.ExpressionVisitor;

/**
 * Helper class for the Disprover, containing an identifier and its type.
 * <p>
 * 
 * As Nodes in the Syntax Tree can only have one parent, the getter methods
 * always create new Objects, rather than handing out references.
 * <p>
 * 
 * Types (vs. variables and constants ) have a name, but the type is null.
 * 
 * @author jastram
 */
public class DisproverIdentifier {

	private String name;
	private Type type;
	private LinkedList<String> bounds;

	public DisproverIdentifier(FreeIdentifier id, Type type,
			LinkedList<String> bounds) {
		this.name = id.getName();
		this.bounds = bounds;
		if (type != null)
			this.type = type;
	}

	private PExpression typeToPExpression(Type type, LinkedList<String> bounds) {
		ExpressionVisitor visitor = new ExpressionVisitor(bounds);
		Expression expression = type.toExpression(FormulaFactory.getDefault());
		expression.accept(visitor);
		PExpression typeExpr = visitor.getExpression();
		return typeExpr;
	}

	private PExpression idToPExpression() {
		return stringToPExpression(name);
	}

	public String getName() {
		return name;
	}

	public PExpression getId() {
		return idToPExpression();
	}

	public PExpression getType() {
		if (type == null)
			return null;
		return typeToPExpression(type, bounds);
	}

	/**
	 * Helper method that converts a String to a {@link PExpression} by making
	 * it an {@link TIdentifierLiteral}.
	 */
	public static PExpression stringToPExpression(String name) {
		AIdentifierExpression expression = new AIdentifierExpression();
		List<TIdentifierLiteral> identifiers = new ArrayList<TIdentifierLiteral>();
		identifiers.add(new TIdentifierLiteral(name));
		expression.setIdentifier(identifiers);
		return expression;
	}

	@Override
	public String toString() {
		return name + "(" + type + ")";
	}

}
