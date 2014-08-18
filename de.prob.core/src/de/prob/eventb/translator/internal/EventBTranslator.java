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
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.core.translator.ITranslator;
import de.prob.core.translator.TranslationFailedException;
import de.prob.core.translator.pragmas.IPragma;
import de.prob.eventb.translator.AbstractComponentTranslator;
import de.prob.eventb.translator.Theories;
import de.prob.prolog.output.IPrologTermOutput;

public abstract class EventBTranslator implements ITranslator {
	protected final IEventBProject project;

	protected EventBTranslator(final IEventBRoot root) {
		this.project = root.getEventBProject();
	}

	// another constructor to cater for ISCInternalContext (which is not a root
	// element)
	protected EventBTranslator(final ISCInternalContext ctx) {
		Assert.isTrue(ctx.getRoot() instanceof ISCMachineRoot);
		this.project = ((ISCMachineRoot) ctx.getRoot()).getEventBProject();
	}

	private LabelPositionPrinter createLabelPrositionPrinter(
			final Collection<AbstractComponentTranslator> translators)
			throws TranslationFailedException {
		LabelPositionPrinter printer = new LabelPositionPrinter();
		for (AbstractComponentTranslator t : translators) {
			final Map<Node, IInternalElement> labelMapping = t
					.getLabelMapping();
			printer.addNodes(labelMapping, t.getResource());
		}
		return printer;
	}

	private Collection<Node> translateModels(
			final Collection<? extends AbstractComponentTranslator> refinementChainTranslators) {
		Collection<Node> nodes = new ArrayList<Node>();
		for (final AbstractComponentTranslator translator : refinementChainTranslators) {
			nodes.add(translator.getAST());
		}
		return nodes;
	}

	private void printModels(final Collection<Node> nodes,
			final IPrologTermOutput pout, final ASTProlog prolog) {
		pout.openList();
		for (final Node node : nodes) {
			node.apply(prolog);
		}
		pout.closeList();
	}

	private void printProofInformation(
			final Collection<? extends AbstractComponentTranslator> refinementChainTranslators,
			Collection<? extends AbstractComponentTranslator> contextTranslators,
			final IPrologTermOutput pout) throws TranslationFailedException {

		ArrayList<ProofObligation> list = new ArrayList<ProofObligation>();

		for (AbstractComponentTranslator contextTranslator : contextTranslators) {
			list.addAll(contextTranslator.getProofs());
		}
		for (AbstractComponentTranslator modelTranslator : refinementChainTranslators) {
			list.addAll(modelTranslator.getProofs());
		}

		for (ProofObligation proof : list) {
			pout.openTerm("po");
			pout.printAtom(proof.origin.getRodinFile().getBareName());
			pout.printAtom(proof.kind);
			pout.openList();
			for (SequentSource source : proof.sources) {
				pout.openTerm(source.type);
				pout.printAtom(source.label);
				pout.closeTerm();
			}
			pout.closeList();

			pout.printAtom(proof.discharged.toString());
			pout.closeTerm();
		}

		if (System.getProperty("flow") != null)
			printFlowInformation(pout);
	}

	private void printPragmaContents(
			Collection<? extends AbstractComponentTranslator> refinementChainTranslators,
			Collection<? extends AbstractComponentTranslator> contextTranslators,
			IPrologTermOutput pout) {
		ArrayList<IPragma> pragmas = new ArrayList<IPragma>();

		for (AbstractComponentTranslator contextTranslator : contextTranslators) {
			pragmas.addAll(contextTranslator.getPragmas());
		}
		for (AbstractComponentTranslator modelTranslator : refinementChainTranslators) {
			pragmas.addAll(modelTranslator.getPragmas());
		}

		for (IPragma pragma : pragmas) {
			pragma.output(pout);
		}
	}

	protected abstract void printFlowInformation(final IPrologTermOutput pout);

	private ASTProlog createAstVisitor(
			final Collection<? extends AbstractComponentTranslator> refinementChainTranslators,
			final Collection<? extends AbstractComponentTranslator> contextTranslators,
			final IPrologTermOutput pout) throws TranslationFailedException {
		Collection<AbstractComponentTranslator> translators = new ArrayList<AbstractComponentTranslator>();
		translators.addAll(refinementChainTranslators);
		translators.addAll(contextTranslators);
		return new ASTProlog(pout, createLabelPrositionPrinter(translators));
	}

	protected void printProlog(
			final Collection<? extends AbstractComponentTranslator> refinementChainTranslators,
			final Collection<? extends AbstractComponentTranslator> contextTranslators,
			final IPrologTermOutput pout) throws TranslationFailedException {
		Collection<Node> machineNodes = translateModels(refinementChainTranslators);
		Collection<Node> contextNodes = translateModels(contextTranslators);

		final boolean theoryIsUsed = areTheoriesAreUsed();
		if (theoryIsUsed) {
			checkIfTheoriesAvailable();
		}

		final ASTProlog prolog = createAstVisitor(refinementChainTranslators,
				contextTranslators, pout);

		pout.openTerm("load_event_b_project");
		printModels(machineNodes, pout, prolog);
		printModels(contextNodes, pout, prolog);
		pout.openList();
		pout.openTerm("exporter_version");
		pout.printNumber(3);
		pout.closeTerm();

		printProofInformation(refinementChainTranslators, contextTranslators,
				pout);

		if (theoryIsUsed) {
			translateTheories(project, pout);
		}

		printPragmaContents(refinementChainTranslators, contextTranslators,
				pout);

		pout.closeList();
		pout.printVariable("_Error");
		pout.closeTerm();
	}

	private boolean areTheoriesAreUsed() throws TranslationFailedException {
		try {
			final IRodinElement[] elements;
			elements = project.getRodinProject().getChildren();
			for (IRodinElement element : elements) {
				if (element instanceof IRodinFile) {
					IRodinFile file = (IRodinFile) element;
					final String id = file.getRootElementType().getId();
					if (id.startsWith("org.eventb.theory.core")) {
						return true;
					}
				}
			}
			return false;
		} catch (RodinDBException e) {
			throw new TranslationFailedException(e);
		}
	}

	private void checkIfTheoriesAvailable() throws TranslationFailedException {
		try {
			Theories.touch();
		} catch (NoClassDefFoundError e) {
			throw new TranslationFailedException(
					"Theory",
					"The model to animate makes use of a theory but the theory plug-in is not installed");
		}
	}

	private void translateTheories(IEventBProject project2,
			IPrologTermOutput pout) throws TranslationFailedException {
		Theories.translate(project, pout);
	}

}
