/**
 * 
 */
package de.prob.ui.stateview.statetree;

/**
 * Describes a static property of states that can have sub-property, e.g. a
 * formula with it sub-formulas.
 * 
 * @author plagge
 */
public interface StateTreeElement extends StaticStateElement {
	public static final StateTreeElement[] EMPTY_ARRAY = new StateTreeElement[0];

	StaticStateElement getParent();

	boolean hasChildren();

	StaticStateElement[] getChildren();

}
