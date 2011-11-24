/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.stateview;

import org.eclipse.jface.viewers.IBaseLabelProvider;

public interface IVisibilityProvider extends IBaseLabelProvider {
	boolean isVisible(Object element);
}
