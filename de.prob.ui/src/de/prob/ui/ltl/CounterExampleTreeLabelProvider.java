package de.prob.ui.ltl;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/***
 * Provides a label provider for a counter-example tree view
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleTreeLabelProvider implements
		ITableLabelProvider {
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		if (columnIndex == 0) {
			String proposition = ((CounterExampleProposition) element)
					.toString();
			return proposition;
		} else {
			CounterExampleValueType value = ((CounterExampleProposition) element)
					.getValues().get(columnIndex - 1);
			return value.toString();
		}
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}
}
