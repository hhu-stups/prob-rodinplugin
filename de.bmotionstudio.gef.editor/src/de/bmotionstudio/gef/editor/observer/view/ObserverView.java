package de.bmotionstudio.gef.editor.observer.view;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import de.bmotionstudio.gef.editor.BMotionStudioEditor;

public class ObserverView extends PageBookView {

	public static String ID = "de.bmotionstudio.gef.editor.ObserverView";

	private String defaultText = "The observer view is not available.";

	private ObserverPage page;

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
			page = new ObserverPage();
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
		ObserverPage page = (ObserverPage) rec.page;
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

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return (part instanceof IEditorPart);
	}

}
