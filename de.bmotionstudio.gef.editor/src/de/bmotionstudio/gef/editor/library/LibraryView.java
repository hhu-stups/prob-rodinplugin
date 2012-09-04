/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import de.bmotionstudio.gef.editor.BMotionStudioEditor;

public class LibraryView extends PageBookView {

	public static String ID = "de.bmotionstudio.gef.editor.LibraryView";

	private String defaultText = "A library is not available.";

	private LibraryPage page;

	public LibraryView() {
		super();
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage(defaultText);
		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if (part instanceof BMotionStudioEditor) {
			page = new LibraryPage((BMotionStudioEditor) part);
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
		LibraryPage page = (LibraryPage) rec.page;
		page.dispose();
		rec.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		partActivated(part);
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return (part instanceof IEditorPart);
	}

}