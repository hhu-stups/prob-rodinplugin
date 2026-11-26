/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * The configuration of the {@link MappingFileEditor}: the syntax colouring,
 * and the hovers of the text editor for the markers; no spell checking (the
 * file has names of operators and tags rather than words).
 */
public class MappingFileConfiguration extends TextSourceViewerConfiguration {

	public MappingFileConfiguration(final IPreferenceStore store) {
		super(store);
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			final ISourceViewer sourceViewer) {
		final PresentationReconciler reconciler = new PresentationReconciler();
		final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				new MappingFileScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		return reconciler;
	}

	@Override
	public IReconciler getReconciler(final ISourceViewer sourceViewer) {
		return null; // no spell checking
	}
}
