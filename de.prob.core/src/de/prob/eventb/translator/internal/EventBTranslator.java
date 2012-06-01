/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AEventBModelParseUnit;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.core.translator.ITranslator;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.AbstractComponentTranslator;
import de.prob.eventb.translator.ContextTranslator;
import de.prob.prolog.output.IPrologTermOutput;

public abstract class EventBTranslator implements ITranslator {
	protected final IEventBProject project;
	private final String name;

	protected EventBTranslator(final IEventBRoot root) {
		this.project = root.getEventBProject();
		this.name = root.getComponentName();
	}

	// another constructor to cater for ISCInternalContext (which is not a root
	// element)
	protected EventBTranslator(final ISCInternalContext ctx) {
		Assert.isTrue(ctx.getRoot() instanceof ISCMachineRoot);
		this.project = ((ISCMachineRoot) ctx.getRoot()).getEventBProject();
		this.name = ctx.getComponentName();
	}

	private LabelPositionPrinter createPrinter(
			final Collection<AbstractComponentTranslator> translators)
			throws TranslationFailedException {
		LabelPositionPrinter printer = new LabelPositionPrinter();
		for (AbstractComponentTranslator t : translators) {
			final Map<Node, IInternalElement> labelMapping = t
					.getLabelMapping();
			printer.addNodes(labelMapping);
		}
		return printer;
	}

	private void printContexts(
			final Collection<ContextTranslator> contextTranslators,
			final IPrologTermOutput pout, final ASTProlog prolog) {
		pout.openList();
		for (final ContextTranslator contextTranslator : contextTranslators) {
			final AEventBContextParseUnit contextAST = contextTranslator
					.getContextAST();
			contextAST.apply(prolog);
		}
		pout.closeList();
	}

	private void printModels(
			final Collection<ModelTranslator> refinementChainTranslators,
			final IPrologTermOutput pout, final ASTProlog prolog) {
		pout.openList();

		for (final ModelTranslator modelTranslator : refinementChainTranslators) {
			final AEventBModelParseUnit modelAST = modelTranslator
					.getModelAST();
			modelAST.apply(prolog);
		}
		pout.closeList();
	}

	private void printProofInformation(
			final Collection<ModelTranslator> refinementChainTranslators,
			Collection<ContextTranslator> contextTranslators,
			final IPrologTermOutput pout) throws TranslationFailedException {

		ArrayList<DischargedProof> list = new ArrayList<DischargedProof>();

		for (ContextTranslator contextTranslator : contextTranslators) {
			list.addAll(contextTranslator.getProofs());
		}
		for (ModelTranslator modelTranslator : refinementChainTranslators) {
			list.addAll(modelTranslator.getProofs());
		}

		for (DischargedProof proof : list) {
			pout.openTerm("discharged");
			pout.printAtom(proof.machine.getRodinFile().getBareName());
			try {
				final String label = proof.event != null ? proof.event
						.getLabel() : "$ANY";
				final String elementName = proof.predicate;
				pout.printAtom(label);
				pout.printAtom(elementName);
			} catch (RodinDBException e) {
				final String details = "Translation error while getting information about discharged proof obligations";
				throw new TranslationFailedException(name, details);
			}

			pout.closeTerm();
		}

		if (System.getProperty("flow") != null)
			printFlowInformation(pout);
	}

	protected abstract void printFlowInformation(final IPrologTermOutput pout);

	private ASTProlog createAstVisitor(
			final Collection<ModelTranslator> refinementChainTranslators,
			final Collection<ContextTranslator> contextTranslators,
			final IPrologTermOutput pout) throws TranslationFailedException {
		Collection<AbstractComponentTranslator> translators = new ArrayList<AbstractComponentTranslator>();
		translators.addAll(refinementChainTranslators);
		translators.addAll(contextTranslators);
		return new ASTProlog(pout, createPrinter(translators));
	}

	protected void printProlog(
			final Collection<ModelTranslator> refinementChainTranslators,
			final Collection<ContextTranslator> contextTranslators,
			final IPrologTermOutput pout) throws TranslationFailedException {
		final ASTProlog prolog = createAstVisitor(refinementChainTranslators,
				contextTranslators, pout);

		pout.openTerm("load_event_b_project");
		printModels(refinementChainTranslators, pout, prolog);
		printContexts(contextTranslators, pout, prolog);
		pout.openList();
		printProofInformation(refinementChainTranslators, contextTranslators,
				pout);
		pout.closeList();
		pout.printVariable("_Error");
		pout.closeTerm();
	}
}
