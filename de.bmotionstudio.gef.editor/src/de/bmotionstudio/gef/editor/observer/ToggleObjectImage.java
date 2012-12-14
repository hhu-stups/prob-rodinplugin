/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;


public class ToggleObjectImage extends ObserverEvalObject implements Cloneable {

	private String image;

	public ToggleObjectImage() {
	}

	public ToggleObjectImage(String image, String eval) {
		super(eval);
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		Object oldValue = this.image;
		this.image = image;
		firePropertyChange("image", oldValue, this.image);
	}

	public ToggleObjectImage clone() throws CloneNotSupportedException {
		return (ToggleObjectImage) super.clone();
	}

}
