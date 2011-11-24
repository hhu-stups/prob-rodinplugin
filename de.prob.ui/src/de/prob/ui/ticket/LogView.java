/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.ticket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class LogView extends ViewPart {

	private static LogView instance = new LogView();
	private Text text;

	@Override
	public void createPartControl(final Composite parent) {
		text = new Text(parent, SWT.WRAP | SWT.MULTI);
	}

	public static void writeToLog(final String s) {
		if (instance != null) {
			instance.write(s);
		}
	}

	private synchronized void write(final String s) {
		if (text != null) {
			text.append(s);
			text.append("\n");
		}
	}

	@Override
	public void setFocus() {

	}

}
