/**
 * 
 */
package de.prob.core.command;

import org.eclipse.core.commands.Command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * A ComposableCommand defines a ProB command that can be used together with
 * other commands in a batch-mode style.
 * 
 * That means that the communication to the ProB core is as simple as a message
 * to the core (containing one ore more Prolog terms) and one answer.
 * 
 * @author plagge
 * 
 * @see Command
 */
public interface IComposableCommand {
	static IComposableCommand[] EMPTY_ARRAY = new IComposableCommand[0];

	void writeCommand(IPrologTermOutput pto) throws CommandException;

	void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException;
}
