package de.prob.eventb.disprover.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;

import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
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
	private final Type type;
	private final FormulaFactory ff;
	private final boolean givenSet;

	public DisproverIdentifier(String name, Type type, boolean givenSet,
			FormulaFactory ff) {
		this.givenSet = givenSet;
		this.ff = ff;
		this.name = name;
		this.type = type;
	}

	public boolean isGivenSet() {
		return givenSet;
	}

	public boolean isPrimedVariable() {
		return name.endsWith("'");
	}

	private PExpression typeToPExpression(Type type) {
		TranslationVisitor visitor = new TranslationVisitor();
		Expression expression = type.toExpression();
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
