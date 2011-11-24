/**
 * 
 */
package de.prob.ui.stateview.statetree;


/**
 * @author plagge
 * 
 */
public abstract class AbstractStateTreeElement implements StateTreeElement {
	private final StaticStateElement parent;

	public AbstractStateTreeElement(final StaticStateElement parent) {
		this.parent = parent;
	}

	public StaticStateElement getParent() {
		return parent;
	}

}
