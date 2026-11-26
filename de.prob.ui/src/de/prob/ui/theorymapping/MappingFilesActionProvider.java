/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import de.prob.logging.Logger;

/**
 * Makes the Event-B Explorer's "open" action, run by a double-click (or
 * Enter), open the selected theory mapping file (.ptm) in its editor
 */
public class MappingFilesActionProvider extends CommonActionProvider {

	private static final String TEXT_EDITOR = MappingFileEditor.ID;

	private ICommonActionExtensionSite site;
	private Action open;

	@Override
	public void init(final ICommonActionExtensionSite site) {
		super.init(site);
		this.site = site;
		open = new Action("Open") {
			@Override
			public void run() {
				final IFile file = selectedFile();
				if (file != null)
					open(file);
			}
		};
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		if (selectedFile() != null)
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
					open);
	}

	private void open(final IFile file) {
		if (!(site.getViewSite() instanceof ICommonViewerWorkbenchSite))
			return;
		final IWorkbenchPage page = ((ICommonViewerWorkbenchSite) site
				.getViewSite()).getPage();
		try {
			IDE.openEditor(page, file, TEXT_EDITOR);
		} catch (PartInitException e) {
			Logger.notifyUserNonModal("Could not open " + file.getFullPath()
					+ ".", e.getMessage());
		}
	}

	private IFile selectedFile() {
		final ISelection selection = site.getStructuredViewer().getSelection();
		if (!(selection instanceof IStructuredSelection)
				|| ((IStructuredSelection) selection).size() != 1)
			return null;
		final Object element = ((IStructuredSelection) selection)
				.getFirstElement();
		return element instanceof MappingFileNode ? ((MappingFileNode) element)
				.getFile() : null;
	}
}
