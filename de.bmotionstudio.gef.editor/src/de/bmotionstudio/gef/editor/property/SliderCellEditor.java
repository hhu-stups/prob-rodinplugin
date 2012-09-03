/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.property;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

public class SliderCellEditor extends CellEditor {

	/**
	 * The editor control.
	 */
	private Composite editor;

	/**
	 * The slider.
	 */
	private Slider slider;

	private Label countLabel;

	/**
	 * Listens for 'focusLost' events and fires the 'apply' event as long as the
	 * focus wasn't lost because the dialog was opened.
	 */
	private FocusListener buttonFocusListener;

	/**
	 * The value of this cell editor; initially null.
	 */
	private Object value = null;

	/**
	 * Internal class for laying out the dialog.
	 */
	private class SliderSampleCellLayout extends Layout {
		public void layout(Composite editor, boolean force) {
			Rectangle bounds = editor.getClientArea();
			slider.setBounds(30, 0, bounds.width - 30, bounds.height);
			countLabel.setBounds(5, 1, 25, bounds.height);
		}

		public Point computeSize(Composite editor, int wHint, int hHint,
				boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
			Point buttonSize = slider.computeSize(SWT.DEFAULT, SWT.DEFAULT,
					force);
			// Just return the button width to ensure the button is not clipped
			// if the label is long.
			// The label will just use whatever extra width there is
			Point result = new Point(buttonSize.x, buttonSize.y);
			return result;
		}
	}

	/**
	 * Default DialogCellEditor style
	 */
	private static final int defaultStyle = SWT.NONE;

	/**
	 * Creates a new dialog cell editor with no control
	 * 
	 * @since 2.1
	 */
	public SliderCellEditor() {
		setStyle(defaultStyle);
	}

	/**
	 * Creates a new dialog cell editor parented under the given control. The
	 * cell editor value is null initially, and has no * validator.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public SliderCellEditor(Composite parent) {
		this(parent, defaultStyle);
	}

	/**
	 * Creates a new dialog cell editor parented under the given control. The
	 * cell editor value is null initially, and has no * validator.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the style bits
	 * @since 2.1
	 */
	public SliderCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Creates the button for this cell editor under the given parent control. *
	 * The default implementation of this framework method creates the button *
	 * display on the right hand side of the dialog cell editor. Subclasses may
	 * extend or reimplement. *
	 * 
	 * @param parent
	 *            the parent control
	 * @return the new button control
	 */
	protected Slider createSpinner(Composite parent) {
		Slider result = new Slider(parent, SWT.HORIZONTAL);
		result.setMaximum(265);
		result.setIncrement(1);
		//$NON-NLS-1$
		return result;
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected Control createControl(Composite parent) {
		Font font = parent.getFont();
		Color bg = parent.getBackground();

		editor = new Composite(parent, getStyle());
		editor.setFont(font);
		editor.setBackground(bg);
		editor.setLayout(new SliderSampleCellLayout());

		countLabel = new Label(editor, SWT.NONE);

		slider = createSpinner(editor);
		slider.setFont(font);
		slider.setBackground(editor.getBackground());
		updateContents(value);

		slider.addFocusListener(getButtonFocusListener());
		slider.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				slider.removeFocusListener(getButtonFocusListener());

				Object newValue = slider.getSelection();
				if (newValue != null) {
					boolean newValidState = isCorrect(newValue);
					if (newValidState) {
						markDirty();
						doSetValue(newValue);
					} else {
						// try to insert the current value into the error
						// message.
						setErrorMessage(MessageFormat.format(getErrorMessage(),
								new Object[] { newValue.toString() }));
					}
				}
			}
		});

		slider.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event event) {
				fireApplyEditorValue();
			}
		});

		setValueValid(true);

		return editor;
	}

	/*
	 * (non-Javadoc) Override in order to remove the button's focus listener if
	 * the celleditor is deactivating.
	 */
	public void deactivate() {
		if (slider != null && !slider.isDisposed()) {
			slider.removeFocusListener(getButtonFocusListener());
		}

		super.deactivate();
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected Object doGetValue() {
		return value;
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor. The focus is set to the cell
	 * editor's button.
	 */
	protected void doSetFocus() {
		slider.setFocus(); // add a FocusListener to the button
		slider.addFocusListener(getButtonFocusListener());
	}

	/**
	 * Return a listener for button focus.
	 * 
	 * @return FocusListener
	 */
	private FocusListener getButtonFocusListener() {
		if (buttonFocusListener == null) {
			buttonFocusListener = new FocusListener() {
				public void focusGained(FocusEvent e) {
					// Do nothing
				}

				public void focusLost(FocusEvent e) {
					SliderCellEditor.this.focusLost();
				}
			};
		}

		return buttonFocusListener;
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected void doSetValue(Object value) {
		this.value = value;
		updateContents(value);
	}

	/**
	 * Updates the controls showing the value of this cell editor. * The default
	 * implementation of this framework method just converts the passed object
	 * to a string using toString and sets this as the text of the label widget.
	 */
	protected void updateContents(Object value) {
		String text = "";//$NON-NLS-1$
		if (value != null) {
			text = value.toString();
			if (slider != null) {
				slider.setSelection(Integer.parseInt(text));
				countLabel.setText(value.toString());
			}
		}
	}

}
