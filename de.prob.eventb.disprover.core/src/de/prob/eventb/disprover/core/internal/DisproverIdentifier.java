package de.prob.eventb.disprover.core.internal;

import java.util.*;

import org.eventb.core.ast.*;

import de.be4.classicalb.core.parser.node.*;
import de.prob.eventb.translator.internal.TranslationVisitor;

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

	private final String name;
	private Type type;
	private final FormulaFactory ff;

	public DisproverIdentifier(String name, Type type, FormulaFactory ff) {
		this.ff = ff;
		this.name = name;
		if (type != null)
			this.type = type;
	}

	public boolean isSet() {
		Type baseType = type.getBaseType();
		if (baseType == null) {
			return false;
		}
		return baseType.toString().equals(name);
	}

	private PExpression typeToPExpression(Type type) {
		TranslationVisitor visitor = new TranslationVisitor();
		Expression expression = type.toExpression(ff);
		expression.accept(visitor);
		PExpression typeExpr = visitor.getExpression();
		return typeExpr;
	}

	public String getName() {
		return name;
	}

	public List<TIdentifierLiteral> getId() {
		return stringToIdentifierLiteralList(name);
	}

	public AIdentifierExpression getIdExpression() {
		return new AIdentifierExpression(getId());
	}

	public PExpression getType() {
		if (type == null)
			return null;
		return typeToPExpression(type);
	}

	private List<TIdentifierLiteral> stringToIdentifierLiteralList(String name) {
		List<TIdentifierLiteral> identifiers = new ArrayList<TIdentifierLiteral>();
		identifiers.add(new TIdentifierLiteral(name));
		return identifiers;
	}

	@Override
	public String toString() {
		return name + "(" + type + ")";
	}

}
