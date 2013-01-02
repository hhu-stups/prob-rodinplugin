package de.prob.eventb.translator.internal;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

public class SequentSource {

	public final String type;
	public final String label;

	public SequentSource(IElementType<? extends IRodinElement> type,
			String label) {
		this.type = type.toString().replaceAll("org.eventb.core.", "");
		this.label = label;
	}

}
