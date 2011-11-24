/**
 * 
 */
package de.prob.ui.errorview;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.prob.ui.errorview.ShownErrors.ErrorEvent;
import de.prob.ui.errorview.ShownErrors.ShownStateError;

/**
 * @author plagge
 * 
 */
public class ErrorLabelProvider extends BaseLabelProvider implements
		ITableLabelProvider {

	public Image getColumnImage(Object object, int column) {
		return null;
	}

	public String getColumnText(Object object, int column) {
		final String text;
		if (column == 0) {
			if (object instanceof ShownStateError) {
				ShownStateError error = (ShownStateError) object;
				text = error.getError().getShortDescription();
			} else if (object instanceof ErrorEvent) {
				final ErrorEvent event = (ErrorEvent) object;
				final int size = event.getErrors().size();
				StringBuilder sb = new StringBuilder();
				sb.append(event.getEvent());
				sb.append(" (").append(size);
				sb.append(size == 1 ? " error)" : " errors)");
				text = sb.toString();
			} else {
				text = null;
			}
		} else {
			text = null;
		}
		return text;
	}
}
