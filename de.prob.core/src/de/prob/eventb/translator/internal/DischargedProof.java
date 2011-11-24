/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.eventb.translator.internal;

import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;

public class DischargedProof {
	public final IInvariant invariant;
	public final IEvent event;
	public final IMachineRoot machine;

	public DischargedProof(final IMachineRoot root, final IInvariant inv,
			final IEvent evt) {
		machine = root;
		invariant = inv;
		event = evt;
	}
}