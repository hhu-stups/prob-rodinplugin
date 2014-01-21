/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.ui.stateview;

import static de.prob.ui.ProbUiPlugin.CHANGE_STAR;

import org.eclipse.jface.resource.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import de.prob.core.domainobjects.State;
import de.prob.ui.ProbUiPlugin;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * This LabelProvider maps a {@link StaticStateElement} to its label, current
 * value or previous value. Depending on its properties color and font is also
 * chosen.
 * 
 * An instance of {@link VarLabelProvider} has a state of its own, a current
 * state and a last state. This is needed to map {@link StaticStateElement}s to
 * their respective value without altering the tree structure.
 * 
 * @author plagge
 */
public class VarLabelProvider extends BaseLabelProvider implements
		ITableLabelProvider, ITableColorProvider, ITableFontProvider {

	private static final String RODIN_FONT_KEY = "org.rodinp.keyboard.textFont";

	private final StateLabelProvider slProvider = new StateLabelProvider();

	private final Color gray = Display.getCurrent().getSystemColor(
			SWT.COLOR_GRAY);

	private State currentState, lastState;

	public void setStates(final State currentState, final State lastState) {
		this.currentState = currentState;
		this.lastState = lastState;
	}

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		final Image image;
		if (isApplicable(element)) {
			if (columnIndex == 0) {
				image = hasChanged(element) ? ProbUiPlugin.getDefault()
						.getImageRegistry().get(CHANGE_STAR) : null;
			} else {
				State state = getState(columnIndex);
				image = state == null ? null : slProvider.getImage(state,
						(StaticStateElement) element);
			}
		} else {
			image = null;
		}
		return image;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final String result;
		if (isApplicable(element)) {
			StaticStateElement sse = (StaticStateElement) element;
			if (columnIndex == 0) {
				result = sse.getLabel();
			} else {
				State state = getState(columnIndex);
				result = state == null ? null : slProvider.getText(state, sse);
			}
		} else {
			result = null;
		}
		return result;
	}

	@Override
	public Color getBackground(final Object element, final int column) {
		final Color color;
		if (isApplicable(element)) {
			final State state = getState(column);
			color = state == null ? null : slProvider.getBackground(state,
					(StaticStateElement) element);
		} else {
			color = null;
		}
		return color;
	}

	@Override
	public Color getForeground(final Object element, final int column) {
		final Color color;
		if (isApplicable(element)) {
			final State curState = getState(column == 0 ? 1 : column);
			final boolean changed = hasChanged(element);

			color = curState == null ? gray : slProvider.getForeground(
					curState, changed, (StaticStateElement) element);
		} else {
			color = null;
		}
		return color;
	}

	@Override
	public Font getFont(final Object element, final int column) {
		final Font font;
		final FontRegistry fontRegistry = JFaceResources.getFontRegistry();
		if (isApplicable(element) && hasChanged(element)) {
			font = fontRegistry.getBold(RODIN_FONT_KEY);
		} else {
			font = fontRegistry.get(RODIN_FONT_KEY);
		}
		return font;
	}

	private boolean hasChanged(final Object element) {
		return ((StaticStateElement) element).hasChanged(currentState,
				lastState);
	}

	private boolean isApplicable(final Object element) {
		return element != null && element instanceof StaticStateElement;
	}

	private State getState(final int column) {
		final State state;
		switch (column) {
		case 1:
			state = currentState;
			break;
		case 2:
			state = lastState;
			break;
		default:
			state = null;
			break;
		}
		return state;
	}
}
