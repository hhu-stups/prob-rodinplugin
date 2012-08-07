/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.eventb.translator.internal;

import org.eventb.core.IAxiom;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IInvariant;
import org.rodinp.core.RodinDBException;

public class DischargedProof {
	public final String predicate;
	public final IEvent event;
	public final IEventBRoot machine;

	public DischargedProof(final IEventBRoot root, final IInvariant inv,
			final IEvent evt) {
		machine = root;
		String p;
		try {
			p = inv.getLabel();
		} catch (RodinDBException e) {
			p = "";
			e.printStackTrace();
		}
		predicate = p;
		event = evt;
	}

	public DischargedProof(final IEventBRoot root, final IAxiom inv,
			final IEvent evt) {
		machine = root;
		String p;
		try {
			p = inv.getLabel();
		} catch (RodinDBException e) {
			p = "";
			e.printStackTrace();
		}
		predicate = p;
		event = evt;
	}
}