package de.prob.model.representation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is the subclass of all model elements (Models, Machines, Contexts,
 * Variables, etc.)
 * 
 * @author joy
 * 
 */
public abstract class AbstractElement {
	protected Map<Class<? extends AbstractElement>, java.util.Set<? extends AbstractElement>> children = new HashMap<Class<? extends AbstractElement>, Set<? extends AbstractElement>>();

	/**
	 * Each {@link AbstractElement} can have children of a subclass that extends
	 * {@link AbstractElement}. These are specified by the class of the child.
	 * To get a Set of all of the children of a particular class, use this
	 * method.
	 * 
	 * @param c
	 *            {@link Class} T of the desired type of children
	 * @return {@link Set} containing all the children of type T
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractElement> Set<T> getChildrenOfType(final Class<T> c) {
		Set<? extends AbstractElement> set = children.get(c);
		if (set == null) {
			return Collections.emptySet();
		}
		return (Set<T>) set;
	}

	/**
	 * Maps a {@link Collection} of elements to the specified {@link Class}
	 * 
	 * @param c
	 *            {@link Class} to specify children elements
	 * @param elements
	 *            {@link Collection} of elements with which c will be mapped
	 */
	public <T extends AbstractElement> void put(final Class<T> c,
			final Collection<? extends T> elements) {
		children.put(c, new LinkedHashSet<T>(elements));
	}

	/**
	 * @return the {@link Map} of {@link Class} to {@link Set} of children
	 */
	public Map<Class<? extends AbstractElement>, java.util.Set<? extends AbstractElement>> getChildren() {
		return children;
	}
}
