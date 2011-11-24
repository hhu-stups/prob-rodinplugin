/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.swt.graphics.Image;

public class LibraryObject {

	private String name;
	private String type;
	private Image typeImage;

	public LibraryObject(String name, String type, Image typeImage) {
		this.name = name;
		this.type = type;
		this.typeImage = typeImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setImage(Image image) {
		this.typeImage = image;
	}

	public Image getImage() {
		return typeImage;
	}

	public void delete(LibraryPage page) {
	}

	public Image getPreview(LibraryPage page) {
		return null;
	}

}
