package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;

/**
 * Creates a new list of Operations, merging the list of available operations
 * with the list of enabled operations. Before adding the enabled operations,
 * they are divided into groups by their operation name
 * 
 */
class OperationsContentProvider implements IStructuredContentProvider {

	private final Collection<String> allOperations;

	public OperationsContentProvider(final Collection<String> allOperations) {
		this.allOperations = allOperations;
	}

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	public Object[] getElements(final Object inputElement) {

		List<Object> mergedOperations = new ArrayList<Object>();

		// add all available operations

		mergedOperations.addAll(allOperations);
		if (inputElement != null && inputElement instanceof State) {
			State state = (State) inputElement;
			// group and add all enabled Operations
			Map<String, List<Operation>> enabledOps = new HashMap<String, List<Operation>>();
			for (Operation op : state.getEnabledOperations()) {
				List<Operation> list = enabledOps.get(op.getName());
				if (list == null) {
					list = new ArrayList<Operation>();
					enabledOps.put(op.getName(), list);
				}
				list.add(op);
			}

			// merge them
			for (List<Operation> al : enabledOps.values()) {
				Operation op = al.get(0);
				int index = mergedOperations.indexOf(op.getName());
				if (index >= 0) {
					mergedOperations.set(index, al);
				} else {
					mergedOperations.add(al);
				}
			}
		}

		return mergedOperations.toArray();
	}
}