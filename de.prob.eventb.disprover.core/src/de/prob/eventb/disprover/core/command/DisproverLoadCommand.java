package de.prob.eventb.disprover.core.command;

import org.eventb.core.IEventBProject;
import org.rodinp.core.*;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.core.command.*;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.Theories;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class DisproverLoadCommand implements IComposableCommand {
	private final AEventBContextParseUnit context;
	private final IEventBProject project;

	public DisproverLoadCommand(IEventBProject project,
			AEventBContextParseUnit context) {
		this.context = context;
		this.project = project;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		ASTProlog modelAst = new ASTProlog(pto, null);
		boolean theoryIsUsed;

		// no proof information but the theories if used
		try {
			pto.openTerm("load_event_b_project");
			pto.openList();
			pto.closeList();

			// load context
			pto.openList();
			context.apply(modelAst);
			pto.closeList();

			theoryIsUsed = theoriesUsed();
			if (theoryIsUsed) {
				theoriesAvailable();
			}

			pto.openList();

			pto.openTerm("exporter_version");
			pto.printNumber(3);
			pto.closeTerm();

			if (theoryIsUsed) {
				Theories.translate(project, pto);
			}
			pto.closeList();

			pto.printVariable("E");
			pto.closeTerm();
		} catch (TranslationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// there are no results to process
	}

	private boolean theoriesUsed() throws TranslationFailedException {
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

	private void theoriesAvailable() throws TranslationFailedException {
		try {
			Theories.touch();
		} catch (NoClassDefFoundError e) {
			throw new TranslationFailedException(
					"Theory",
					"The model to animate makes use of a theory but the theory plug-in is not installed");
		}
	}
}