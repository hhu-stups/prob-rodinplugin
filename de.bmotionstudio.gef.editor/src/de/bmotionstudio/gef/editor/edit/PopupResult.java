/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.edit;

/**
 * @author Lukas Ladenberger
 * 
 */
public class PopupResult {

	private String value;
	private int returncode;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setReturncode(int returncode) {
		this.returncode = returncode;
	}

	public int getReturncode() {
		return returncode;
	}

}
