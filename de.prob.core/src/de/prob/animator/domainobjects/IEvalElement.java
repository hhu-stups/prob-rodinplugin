package de.prob.animator.domainobjects;

import de.prob.prolog.output.IPrologTermOutput;

public interface IEvalElement {
	public abstract String getCode();

	public abstract void printProlog(IPrologTermOutput pout);

	public abstract String getKind();
}
