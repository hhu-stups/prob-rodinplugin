/**
 * 
 */
package de.prob.ui.errorview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.ui.errorview.ShownErrors.ErrorEvent;
import de.prob.ui.errorview.ShownErrors.ShownStateError;

/**
 * @author plagge
 * 
 */
public class ErrorContentProvider implements ITreeContentProvider {
	@Override
	public Object[] getChildren(Object parent) {
		ShownStateError[] errors;
		if (parent != null && parent instanceof ErrorEvent) {
			ErrorEvent errorEvent = (ErrorEvent) parent;
			errors = errorEvent.getErrors()
					.toArray(ShownStateError.EMPTY_ARRAY);
		} else {
			errors = null;
		}
		return errors;
	}

	@Override
	public Object getParent(Object arg) {
		if (arg != null) {
			if (arg instanceof ShownStateError) {
				return ((ShownStateError) arg).getEvent();
			}
			if (arg instanceof ErrorEvent) {
				return ((ErrorEvent) arg).getShownErrors();
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object parent) {
		// empty nodes are not even created
		return parent != null && parent instanceof ErrorEvent;
	}

	@Override
	public Object[] getElements(Object object) {
		ErrorEvent[] events;
		if (object != null && object instanceof ShownErrors) {
			events = ((ShownErrors) object).getErrorEvents().toArray(
					ErrorEvent.EMPTY_ARRAY);
		} else {
			events = null;
		}
		return events;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

}
