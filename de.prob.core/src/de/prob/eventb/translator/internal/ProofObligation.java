package de.prob.eventb.translator.internal;

import java.util.ArrayList;

import org.eventb.core.IEventBRoot;

public class ProofObligation {

	public final IEventBRoot origin;
	public final ArrayList<SequentSource> sources;
	public final String kind;
	public final boolean discharged;

	public ProofObligation(IEventBRoot origin, ArrayList<SequentSource> s,
			String name, boolean discharged) {
		this.origin = origin;
		this.sources = s;
		this.kind = name;
		this.discharged = discharged;
	}

}
