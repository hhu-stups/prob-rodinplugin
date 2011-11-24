package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class DeserializeStateCommand implements IComposableCommand {

    private String id;
    private final String state;

    public DeserializeStateCommand(String state) {
        this.state = state;
    }

    public static String deserialize(Animator a, String state) throws ProBException {
        DeserializeStateCommand c = new DeserializeStateCommand(state);
        a.execute(c);
        return c.id;
    }

    @Override
    public void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
            throws CommandException {
        this.id = bindings.get("Id").toString();
    }

    @Override
    public void writeCommand(IPrologTermOutput pto) {
        pto.openTerm("deserialize").printVariable("Id").printAtom(state).closeTerm();
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

}
