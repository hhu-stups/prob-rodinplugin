package de.bmotionstudio.gef.editor.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.bmotionstudio.gef.editor.BMotionStudioSWTConstants;

public class TextEditorWindow extends Window {

	private String value;
	private Point position;
	private Text text;
	private PopupResult result = new PopupResult();
	private List<IPopupListener> popupListener = new ArrayList<IPopupListener>();

	protected TextEditorWindow(Shell parentShell, Text text) {
		this(new SameShellProvider(parentShell), text);
	}

	protected TextEditorWindow(IShellProvider parentShell, Text text) {
		super(parentShell);
		this.value = text.getText();
		this.position = text.toDisplay(0, 0);
		this.result.setReturncode(getReturnCode());
		setShellStyle(SWT.ON_TOP);
		setBlockOnOpen(true);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, 0);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		text = new Text(composite, SWT.MULTI | SWT.WRAP);
		text.setText(value);
		text.selectAll();
		text.setFont(BMotionStudioSWTConstants.fontArial10);
		return composite;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 200);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return position;
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.addShellListener(new ShellAdapter() {
			@Override
			public void shellDeactivated(ShellEvent e) {
				setReturnCode(OK);
				close();
			}
		});
		super.configureShell(newShell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#setReturnCode(int)
	 */
	@Override
	protected void setReturnCode(int code) {
		result.setReturncode(code);
		super.setReturnCode(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#close()
	 */
	@Override
	public boolean close() {
		result.setValue(text.getText());
		notifyPopupClosed();
		boolean b = super.close();
		return b;
	}

	public PopupResult openPopup() {
		notifyPopupOpened();
		int i = super.open();
		result.setReturncode(i);
		return result;
	}

	public void notifyPopupOpened() {
		for (IPopupListener l : popupListener)
			l.popupOpened();
	}

	public void notifyPopupClosed() {
		for (IPopupListener l : popupListener)
			l.popupClosed();
	}

	public void addPopupListener(IPopupListener l) {
		this.popupListener.add(l);
	}

	public void removePopupListener(IPopupListener l) {
		this.popupListener.remove(l);
	}

}
