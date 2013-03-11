package de.prob.eventb.disprover.core.internal;

import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AEventBModelParseUnit;
import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.logging.Logger;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class DisproverLoadCommand implements IComposableCommand {

	private final AEventBModelParseUnit machineAst;
	private final List<AEventBContextParseUnit> contextAsts;

	public DisproverLoadCommand(final AEventBModelParseUnit machineAst,
			final List<AEventBContextParseUnit> contextParseUnits) {
		this.machineAst = machineAst;
		this.contextAsts = contextParseUnits;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		ListPrologTerm errors = (ListPrologTerm) bindings.get("Error");
		if (!errors.isEmpty()) { // FIXME Removing Error Variable from Prolog
			// makes this obsolete
			StringBuffer sb = new StringBuffer();
			for (PrologTerm prologTerm : errors) {
				sb.append(prologTerm.toString());
				sb.append("\n");
			}
			Logger.notifyUserWithoutBugreport("Error loading model. \n"
					+ sb.toString());
		}

	}

	public void writeCommand(final IPrologTermOutput pout) {
		ASTProlog modelAst = new ASTProlog(pout, null);

		// from printProlog();
		pout.openTerm("load_event_b_project");

		// from printModels()
		pout.openList();
		machineAst.apply(modelAst);
		pout.closeList();

		// from printContexts()
		pout.openList();
		for (AEventBContextParseUnit contextAst : contextAsts) {
			contextAst.apply(modelAst);
		}
		pout.closeList();

		// from printProofInformation()
		pout.openList();
		pout.closeList();

		pout.printVariable("Error");
		pout.closeTerm();

	}

}
