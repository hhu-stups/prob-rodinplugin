/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

public class Filter {

	private final String pattern;
	private final String name;
	private Boolean enabled = true;

	public Filter(String pattern) {
		this.pattern = pattern;
		this.name = pattern;
	}

	public Filter(String pattern, String name, Boolean enabled) {
		this.pattern = pattern;
		this.name = name;
		this.enabled = enabled;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getPattern() {
		return pattern;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(name);
		stringBuilder.append(" pattern:");
		stringBuilder.append(pattern);
		stringBuilder.append(" (");
		stringBuilder.append(enabled ? "enabled" : "disabled");
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	public String getName() {
		return name;
	}

}
