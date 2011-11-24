/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.Assert;

import de.be4.classicalb.core.parser.node.PPredicate;

public final class LabelStore {

	private final Map<PPredicate, String> node2label = new ConcurrentHashMap<PPredicate, String>();
	private final Map<String, PPredicate> label2node = new ConcurrentHashMap<String, PPredicate>();

	/**
	 * Returns the AST Node for a given label.
	 * 
	 * @param label
	 *            (must not be null)
	 * @return AST root node of the corresponding subtree
	 */
	public PPredicate getNode(final String label) {
		Assert.isNotNull(label);
		return label2node.get(label);
	}

	/**
	 * Returns the label for a given AST Node.
	 * 
	 * @param node
	 *            (must not be null)
	 * @return Label for a give subtree (if there is one, otherwise it returns
	 *         null)
	 */
	public String getLabel(final PPredicate node) {
		Assert.isNotNull(node);
		return node2label.get(node);
	}

	public synchronized void put(final String label, final PPredicate node) {
			label2node.put(label, node);
			node2label.put(node, label);
	}

}
