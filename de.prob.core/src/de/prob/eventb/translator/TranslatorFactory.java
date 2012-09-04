/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator;

import java.io.PrintWriter;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;

import de.prob.core.translator.ITranslator;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.internal.EventBContextTranslator;
import de.prob.eventb.translator.internal.EventBMachineTranslator;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.PrologTermOutput;

public class TranslatorFactory {

	/**
	 * Use this Factory to obtain an instance of {@link ITranslator}.
	 * 
	 * @param root
	 *            IEventBRoot (either machine or context)
	 * @return {@link ITranslator} instance
	 * @throws TranslationFailedException
	 */
	public static void translate(final IEventBRoot root,
			final IPrologTermOutput pto) throws TranslationFailedException {
		
		
		
		
		
		if (root instanceof IMachineRoot) {
			final ISCMachineRoot scRoot = ((IMachineRoot) root)
					.getSCMachineRoot();
			EventBMachineTranslator.create(scRoot, pto);

		} else if (root instanceof IContextRoot) {
			final ISCContextRoot scRoot = ((IContextRoot) root)
					.getSCContextRoot();
			EventBContextTranslator.create(scRoot, pto);
		} else {
			throw new TranslationFailedException(root.getComponentName(),
					"Cannot translate anything else than IMachineRoot or IContextRoot. Type was: "
							+ root.getClass());
		}
	}

	public static void translate(final IEventBRoot root,
			final PrintWriter writer) throws TranslationFailedException {
		IPrologTermOutput pto = new PrologTermOutput(writer, false);
		pto.openTerm("package");
		TranslatorFactory.translate(root, pto);
		pto.closeTerm();
		pto.fullstop().flush();
	}

}
