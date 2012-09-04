/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.rodinp.core.IInternalElement;

import de.be4.classicalb.core.parser.node.Node;

public abstract class AbstractComponentTranslator {

	protected final Map<Node, IInternalElement> labelMapping = new ConcurrentHashMap<Node, IInternalElement>();

	public Map<Node, IInternalElement> getLabelMapping() {
		return Collections.unmodifiableMap(labelMapping);
	}
	
	abstract public String getResource();

}