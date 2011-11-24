/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.eventb.translator.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ITraceableElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.PositionPrinter;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.core.translator.TranslationFailedException;
import de.prob.prolog.output.IPrologTermOutput;

/**
 * This PositionPrinter stores and prints the labels and internal node name
 * 
 * @author plagge
 * 
 */
public class LabelPositionPrinter implements PositionPrinter {
	private IPrologTermOutput pout;

	private final Map<Node, NodeInfo> nodeInfos = new ConcurrentHashMap<Node, NodeInfo>();

	public void addNode(final Node node, final IInternalElement element)
			throws TranslationFailedException {
		final String label, source;
		try {
			if (element instanceof ITraceableElement) {
				// get name of unchecked element
				IRodinElement traceableSource;
				traceableSource = ((ITraceableElement) element).getSource();

				source = traceableSource.getElementName();
			} else {
				source = null;
			}
			if (element instanceof ILabeledElement) {
				label = ((ILabeledElement) element).getLabel();
			} else {
				label = null;
			}
			addNode(node, label, source);
		} catch (RodinDBException e) {
			final String message = "A Rodin exception occured during translation process, you can try to fix that by cleaning the project. Original Exception: ";
			throw new TranslationFailedException(element.getElementName(),
					message + e.getLocalizedMessage());
		}
	}

	public void addNodes(final Map<Node, IInternalElement> nodeMapping)
			throws TranslationFailedException {
		for (Entry<Node, IInternalElement> item : nodeMapping.entrySet()) {
			final Node key = item.getKey();
			final IInternalElement value = item.getValue();
			addNode(key, value);
		}
	}

	private void addNode(final Node node, final String label,
			final String elementName) {
		if (label != null || elementName != null) {
			nodeInfos.put(node, new NodeInfo(label, elementName));
		}
	}

	public void printPosition(final Node node) {
		final NodeInfo info = nodeInfos.get(node);
		if (info == null) {
			pout.printAtom("none");
		} else {
			pout.openTerm("rodinpos");
			if (info.label == null) {
				pout.emptyList();
			} else {
				pout.printAtom(info.label);
			}
			if (info.elementName == null) {
				pout.emptyList();
			} else {
				pout.printAtom(info.elementName);
			}
			pout.closeTerm();
		}
	}

	public void setPrologTermOutput(final IPrologTermOutput pout) {
		this.pout = pout;
	}

	private static class NodeInfo {
		private final String label;
		private final String elementName;

		public NodeInfo(final String label, final String elementName) {
			super();
			this.label = label;
			this.elementName = elementName;
		}
	}

}
