package de.prob.ui.ltl;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

/***
 * Provides a content provider for a counter-example tree view
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleContentProvider implements
		ITreeContentProvider {

	/**
	 * Returns the children of an element
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List) {
			return ((List<?>) parentElement).toArray();
		}

		if (parentElement instanceof CounterExampleUnaryOperator) {
			Object[] children = new Object[1];
			children[0] = ((CounterExampleUnaryOperator) parentElement)
					.getArgument();
			return children;
		}

		if (parentElement instanceof CounterExampleBinaryOperator) {
			Object[] children = new Object[2];
			children[0] = ((CounterExampleBinaryOperator) parentElement)
					.getFirstArgument();
			children[1] = ((CounterExampleBinaryOperator) parentElement)
					.getSecondArgument();
			return children;
		}

		return new Object[0];
	}

	/**
	 * Returns the parent of an element
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof CounterExampleProposition) {
			return ((CounterExampleProposition) element).getParent();
		}

		return null;
	}

	/**
	 * Returns whether an element has children
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof List) {
			return ((List<?>) element).size() > 0;
		} else if (element instanceof CounterExampleUnaryOperator) {
			return true;
		} else if (element instanceof CounterExampleBinaryOperator) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the root element
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
