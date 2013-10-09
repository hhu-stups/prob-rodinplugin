package de.prob.eventb.disprover.core.internal;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.core.command.*;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class DisproverLoadCommand implements IComposableCommand {
	private final AEventBContextParseUnit context;

	public DisproverLoadCommand(AEventBContextParseUnit context) {
		this.context = context;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		ASTProlog modelAst = new ASTProlog(pto, null);

		pto.openTerm("load_event_b_project");
		pto.openList();
		pto.closeList();

		// load context
		pto.openList();
		context.apply(modelAst);
		pto.closeList();

		// no proof information
		pto.openList();
		pto.closeList();

		pto.printVariable("E");
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// there are no results to process
	}

}