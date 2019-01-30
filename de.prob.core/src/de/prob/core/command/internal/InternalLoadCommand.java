package de.prob.core.command.internal;

import org.eventb.core.IEventBRoot;

import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class InternalLoadCommand implements IComposableCommand {
	private final IEventBRoot model;

	public InternalLoadCommand(final IEventBRoot model) {
		this.model = model;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto)
			throws CommandException {
		try {
			TranslatorFactory.translate(model, pto);
		} catch (TranslationFailedException e) {
			throw new CommandException(
					"Translation from Event-B to ProB's internal representation failed: " + e.getMessage(),
					e);
		}
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// there are no results to process
	}

}