/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;


public class AttributeTransferObject {

	private LibraryObject libraryObject;

	public AttributeTransferObject(LibraryObject attributeValue) {
		this.libraryObject = attributeValue;
	}

	public void setLibraryObject(LibraryObject libraryObject) {
		this.libraryObject = libraryObject;
	}

	public LibraryObject getLibraryObject() {
		return libraryObject;
	}

}
