/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler;

import java.util.ArrayList;

import de.bmotionstudio.gef.editor.BindingObject;

public class AnimationScriptStep extends BindingObject implements Cloneable {

	private String command;
	private String parameter;
	private int maxrandom = 1;
	private transient ArrayList<ObserverCallBackObject> callBackList;

	public AnimationScriptStep() {
		this("", "");
	}

	public AnimationScriptStep(String command, String parameter,
			ArrayList<ObserverCallBackObject> callBackList) {
		this.command = command;
		this.parameter = parameter;
		this.callBackList = callBackList;
	}

	public AnimationScriptStep(String command, String parameter) {
		this(command, parameter, new ArrayList<ObserverCallBackObject>());
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		Object oldValue = this.command;
		this.command = command;
		firePropertyChange("command", oldValue, this.command);
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		Object oldValue = this.parameter;
		this.parameter = parameter;
		firePropertyChange("parameter", oldValue, this.parameter);
	}

	public void setCallBackList(ArrayList<ObserverCallBackObject> callBackList) {
		Object oldValue = this.callBackList;
		this.callBackList = callBackList;
		firePropertyChange("callBackList", oldValue, this.callBackList);
	}

	public ArrayList<ObserverCallBackObject> getCallBackList() {
		if (callBackList == null)
			this.callBackList = new ArrayList<ObserverCallBackObject>();
		return this.callBackList;
	}

	public void addCallBackObject(ObserverCallBackObject callBackObj) {
		callBackList.add(callBackObj);
	}

	public void removeCallBackObject(ObserverCallBackObject callBackObj) {
		callBackList.remove(callBackObj);
	}

	public AnimationScriptStep clone() throws CloneNotSupportedException {
		ArrayList<ObserverCallBackObject> tmpList = new ArrayList<ObserverCallBackObject>();
		for (ObserverCallBackObject p : getCallBackList()) {
			tmpList.add(p.clone());
		}
		AnimationScriptStep tmpObj = (AnimationScriptStep) super.clone();
		tmpObj.setCallBackList(tmpList);
		return tmpObj;
	}

	public int getMaxrandom() {
		return maxrandom;
	}

	public void setMaxrandom(int maxrandom) {
		Object oldValue = this.maxrandom;
		this.maxrandom = maxrandom;
		firePropertyChange("maxrandom", oldValue, this.maxrandom);
	}

	public boolean isRandom() {
		if (maxrandom > 1)
			return true;
		else
			return false;
	}

}
