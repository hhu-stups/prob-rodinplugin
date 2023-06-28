/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.edit;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Lukas Ladenberger
 * 
 */
public class TextCellEditorWithContentProposal extends TextCellEditor {

	private ContentProposalAdapter contentProposalAdapter;
	private boolean popupOpen = false; // true, iff popup is currently open

	public TextCellEditorWithContentProposal(Composite parent,
			IContentProposalProvider contentProposalProvider,
			KeyStroke keyStroke, char[] autoActivationCharacters) {
		super(parent);

		enableContentProposal(contentProposalProvider, keyStroke,
				autoActivationCharacters);
	}

	private void enableContentProposal(
			IContentProposalProvider contentProposalProvider,
			KeyStroke keyStroke, char[] autoActivationCharacters) {
		contentProposalAdapter = new ContentProposalAdapter(text,
				new TextContentAdapter(), contentProposalProvider, keyStroke,
				autoActivationCharacters);

		// Listen for popup open/close events to be able to handle focus events
		// correctly
		contentProposalAdapter
				.addContentProposalListener(new IContentProposalListener2() {

					public void proposalPopupClosed(
							ContentProposalAdapter adapter) {
						popupOpen = false;
					}

					public void proposalPopupOpened(
							ContentProposalAdapter adapter) {
						popupOpen = true;
					}
				});
	}

	/**
	 * Return the {@link ContentProposalAdapter} of this cell editor.
	 * 
	 * @return the {@link ContentProposalAdapter}
	 */
	public ContentProposalAdapter getContentProposalAdapter() {
		return contentProposalAdapter;
	}

	protected void focusLost() {
		if (!popupOpen) {
			// Focus lost deactivates the cell editor.
			// This must not happen if focus lost was caused by activating
			// the completion proposal popup.
			super.focusLost();
		}
	}

	protected boolean dependsOnExternalFocusListener() {
		// Always return false;
		// Otherwise, the ColumnViewerEditor will install an additional focus
		// listener
		// that cancels cell editing on focus lost, even if focus gets lost due
		// to
		// activation of the completion proposal popup. See also bug 58777.
		return false;
	}

}
