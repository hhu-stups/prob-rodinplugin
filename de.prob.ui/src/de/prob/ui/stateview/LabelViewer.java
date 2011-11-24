/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.ui.stateview;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * This LabelViewer controls the status of a simple SWT label.
 * 
 * You can use {@link ILabelProvider} to control text and image,
 * {@link IFontProvider} for the font, {@link IColorProvider} for back- and
 * foreground colors and {@link IVisibilityProvider} to conrol the Label's
 * visibility.
 * 
 * You must use a {@link ISimpleContentProvider} to provide a content.
 * 
 * @author plagge
 */
public class LabelViewer extends ContentViewer {

	private final Label label;
	private Object content;
	private ISelection selection;

	public LabelViewer(final Composite parent, final int style) {
		super();
		label = new Label(parent, style);
		label.setText("");
	}

	public LabelViewer(final Label label) {
		super();
		this.label = label;
	}

	public Label getLabel() {
		return label;
	}

	@Override
	public Control getControl() {
		return label;
	}

	@Override
	public ISelection getSelection() {
		return this.selection;
	}

	public void addMouseListener(final MouseListener listener) {
		label.addMouseListener(listener);
	}

	@Override
	public void refresh() {
		IBaseLabelProvider provider = getLabelProvider();
		if (provider instanceof ILabelProvider) {
			final ILabelProvider labelprovider = (ILabelProvider) provider;
			final String text = labelprovider.getText(content);
			label.setText(text == null ? "" : text);
		}
		if (provider instanceof IFontProvider) {
			final IFontProvider fontprovider = (IFontProvider) provider;
			label.setFont(fontprovider.getFont(content));
		}
		if (provider instanceof IColorProvider) {
			final IColorProvider colorProvider = (IColorProvider) provider;
			label.setBackground(colorProvider.getBackground(content));
			label.setForeground(colorProvider.getForeground(content));
		}
		if (provider instanceof IVisibilityProvider) {
			final IVisibilityProvider visibilityProvider = (IVisibilityProvider) provider;
			label.setVisible(visibilityProvider.isVisible(content));
		}
		label.update();
	}

	@Override
	public void setSelection(final ISelection selection, final boolean reveal) {
		this.selection = selection;
	}

	@Override
	protected void inputChanged(final Object input, final Object oldInput) {
		super.inputChanged(input, oldInput);
		content = ((ISimpleContentProvider) getContentProvider())
				.convert(input);
		refresh();
	}

	/**
	 * This interface defines a trivial ContentProvider which just maps an input
	 * to an output.
	 */
	public static interface ISimpleContentProvider extends IContentProvider {
		Object convert(Object element);
	}
}
