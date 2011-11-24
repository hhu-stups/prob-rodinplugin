package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class SerializeStateCommand implements IComposableCommand {

    private final String id;
    private String state;

    public SerializeStateCommand(String id) {
        this.id = id;
    }

    public static String serialize(Animator a, String stateID) throws ProBException {
        SerializeStateCommand c = new SerializeStateCommand(stateID);
        a.execute(c);
        return c.state;
    }

    @Override
    public void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
            throws CommandException {
         state = PrologTerm.atomicString(bindings.get("State"));
    }

    @Override
    public void writeCommand(IPrologTermOutput pto) {
        pto.openTerm("serialize").printAtomOrNumber(id).printVariable("State").closeTerm();
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

}
