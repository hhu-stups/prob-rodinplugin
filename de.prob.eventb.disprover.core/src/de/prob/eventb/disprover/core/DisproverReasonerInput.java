package de.prob.eventb.disprover.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.IEventBProject;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.rodinp.core.RodinDBException;

/**
 * Input for the Disprover. The Input keeps track of the {@link DisproverMode}
 * that the Disprover runs in. To prevent conditional statements in the
 * Disprover code, this method provides some helper mthod that return the
 * appropriate values according to the mode.
 * 
 * @author jastram
 */
public class DisproverReasonerInput implements IReasonerInput {

	private final IProofTreeNode node;
	private final boolean useDisproverPrefs;
	private final boolean useContexts;

	private List<ISCContextRoot> contexts;
	private Set<String> constants;
	private int maxInt;
	private int minInt;
	private int setSize;
	private int timeout;

	public DisproverReasonerInput(IProofTreeNode node, boolean useContext) {
		this.node = node;
		this.useContexts = useContext;
		this.useDisproverPrefs = false;
	}

	public DisproverReasonerInput(IProofTreeNode node, boolean useContext,
			int maxInt, int minInt, int setSize, int timeout) {
		this.node = node;
		this.useContexts = useContext;
		this.useDisproverPrefs = true;
		this.maxInt = maxInt;
		this.minInt = minInt;
		this.setSize = setSize;
		this.timeout = timeout;
	}

	@Override
	public void applyHints(ReplayHints renaming) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	public IProofTreeNode getProofTreeNode() {
		return node;
	}

	public boolean usesContexts() {
		return useContexts;
	}

	/**
	 * Absolutely fucked-up code for retrieving the contexts. The contexts are
	 * lazily retrieved.
	 * 
	 * @return the related contexts, according to the mode. Only in the mode
	 *         {@link DisproverMode#RELEVANT_HYPOTHESES_WITH_CONTEXTS} will the
	 *         referred Contexts be returned. Otherwise, an empty Set is
	 *         returned.
	 */
	public List<ISCContextRoot> getContexts() throws RodinDBException,
			DisproverException {
		if (contexts != null)
			return contexts;

		contexts = new ArrayList<ISCContextRoot>();

		if (!useContexts)
			return contexts;

		// Get the IEventBProject and the name of the Rodin Component
		IProofAttempt origin = (IProofAttempt) node.getProofTree().getOrigin();
		IPORoot poRoot = origin.getComponent().getPORoot();
		String name = poRoot.getElementName();
		IEventBProject eventBProject = (IEventBProject) poRoot
				.getRodinProject().getAdapter(IEventBProject.class);

		// We don't know whether we have a machine or a context.
		ISCMachineRoot machineRoot = eventBProject.getSCMachineRoot(name);
		ISCContextRoot contextRoot = eventBProject.getSCContextRoot(name);

		if (machineRoot.exists()) {
			ISCInternalContext[] ctxs = machineRoot
					.getChildrenOfType(ISCInternalContext.ELEMENT_TYPE);
			for (ISCInternalContext ctx : ctxs) {
				ISCContextRoot context = eventBProject.getSCContextRoot(ctx
						.getElementName());
				contexts.add(context);
			}

		} else if (contextRoot.exists()) {
			contexts.add(contextRoot);
		} else {
			// Neither Machine nor Context
			throw new DisproverException("Has neither Maschine nor Context: "
					+ eventBProject);
		}
		return contexts;
	}

	/**
	 * @return a set with the names of the constants in the relevant contexts.
	 */
	public Set<String> getConstantNames() throws RodinDBException,
			DisproverException {
		if (constants != null)
			return constants;
		constants = new HashSet<String>();
		for (ISCContextRoot context : getContexts()) {
			for (ISCConstant constant : context.getSCConstants()) {
				constants.add(constant.getElementName());
			}
		}
		return constants;
	}

	/**
	 * @param sequent
	 *            , the sequent to prove
	 * @return the required Hypothesis, depending on {@link #mode}.
	 * @throws DisproverException
	 */
	public Iterable<Predicate> getHypotheses(IProverSequent sequent)
			throws DisproverException {
		return sequent.visibleHypIterable();
	}

	public int getMaxInt() {
		return maxInt;
	}

	public int getMinInt() {
		return minInt;
	}

	public int getSetSize() {
		return setSize;
	}

	public int getTimeout() {
		return timeout;
	}

	public boolean useDisproverPrefs() {
		return useDisproverPrefs;
	}

}
