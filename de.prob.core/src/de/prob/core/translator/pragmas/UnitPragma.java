/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.translator.pragmas;

import de.prob.prolog.output.IPrologTermOutput;

public class UnitPragma implements IPragma {
	private String definedIn;
	private String attachedTo;
	private String content;

	public UnitPragma(String definedIn, String attachedTo, String content) {
		this.definedIn = definedIn;
		this.attachedTo = attachedTo;
		this.content = content;
	}

	@Override
	public void output(IPrologTermOutput pout) {
		pout.openTerm("pragma");
		pout.printAtom("unit");
		pout.printAtom(definedIn);
		pout.printAtom(attachedTo);
		pout.openList();
		pout.printAtom(content);
		pout.closeList();
		pout.closeTerm();
	}

}
