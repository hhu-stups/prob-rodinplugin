/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * The editor of theory mapping files (.ptm): a text editor with syntax
 * colouring (see {@link MappingFileScanner}). The file is checked when it is
 * opened and when it is saved (see {@link MappingFileValidator}); the
 * problems are shown as markers in the editor's ruler and the Problems view.
 */
public class MappingFileEditor extends TextEditor {

	/** see plugin.xml */
	public static final String ID = "de.prob.ui.theoryMappingEditor";

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setSourceViewerConfiguration(new MappingFileConfiguration(
				getPreferenceStore()));
	}

	@Override
	protected void doSetInput(final IEditorInput input) throws CoreException {
		super.doSetInput(input);
		if (input instanceof IFileEditorInput)
			MappingFileValidator.validate(((IFileEditorInput) input)
					.getFile());
	}
}
