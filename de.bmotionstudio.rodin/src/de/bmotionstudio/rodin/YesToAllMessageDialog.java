/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

public class YesToAllMessageDialog extends MessageDialog {

	public static int YES = 0;
	public static int NO = 1;
	public static int CANCEL = -1;
	public static int YES_TO_ALL = 2;
	public static int NO_TO_ALL = 3;

	public YesToAllMessageDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
	}

	public static int openYesNoToAllQuestion(Shell parent, String title,
			String message) {
		MessageDialog dialog = new MessageDialog(parent, title, null, // accept
				message, QUESTION, new String[] { IDialogConstants.YES_LABEL,
						IDialogConstants.NO_LABEL,
						IDialogConstants.YES_TO_ALL_LABEL,
						IDialogConstants.NO_TO_ALL_LABEL }, 0); // yes is the
		return dialog.open();
	}

}
