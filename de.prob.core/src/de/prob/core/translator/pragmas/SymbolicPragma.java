/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.translator.pragmas;

import de.prob.prolog.output.IPrologTermOutput;

public class SymbolicPragma implements IPragma {
	private final String definedIn;
	private final String attachedTo;
	private final String content;

	public SymbolicPragma(String definedIn, String attachedTo, String content) {
		this.definedIn = definedIn;
		this.attachedTo = attachedTo;
		this.content = content;
	}

	@Override
	public void output(IPrologTermOutput pout) {
		pout.openTerm("pragma");
		pout.printAtom("symbolic");
		pout.printAtom(definedIn);
		pout.printAtom(attachedTo);
		pout.openList();
		pout.printAtom(content);
		pout.closeList();
		pout.closeTerm();
	}

}
