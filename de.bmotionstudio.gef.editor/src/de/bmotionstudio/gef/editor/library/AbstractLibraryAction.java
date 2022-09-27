/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.jface.action.Action;


public class AbstractLibraryAction extends Action {

	private LibraryPage page;

	public AbstractLibraryAction(LibraryPage page) {
		this.page = page;
	}

	public void setPage(LibraryPage page) {
		this.page = page;
	}

	public LibraryPage getPage() {
		return page;
	}

}
