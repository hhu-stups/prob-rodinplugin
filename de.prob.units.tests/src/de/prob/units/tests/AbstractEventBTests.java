package de.prob.units.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.ast.FormulaFactory;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.debug.DebugHelpers;

/**
 * @author htson
 *         <p>
 *         Abstract class for Event-B tests.
 *         </p>
 */
public abstract class AbstractEventBTests extends AbstractTests {

	/**
	 * The null progress monitor.
	 */
	protected static final IProgressMonitor monitor = new NullProgressMonitor();

	/**
	 * The testing workspace.
	 */
	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	/**
	 * The formula factory used to create formulae.
	 */
	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * Constructor: Create max_size test case.
	 */
	public AbstractEventBTests() {
		super();
	}

	/**
	 * Constructor: Create max_size test case with the given name.
	 * 
	 * @param name
	 *            the name of test
	 */
	public AbstractEventBTests(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}

		// disable indexing
		DebugHelpers.disableIndexing();

		// Delete the old workspace
		workspace.getRoot().delete(true, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	@Override
	protected void tearDown() throws Exception {
		workspace.getRoot().delete(true, null);
		super.tearDown();
	}

	// =========================================================================
	// Utility methods for creating various Event-B elements.
	// =========================================================================

	/**
	 * Utility method to create an Event-B project with given name.
	 * 
	 * @param name
	 *            name of the project
	 * @return the newly created Event-B project
	 * @throws CoreException
	 *             if some errors occurred.
	 */
	protected IEventBProject createEventBProject(String name)
			throws CoreException {
		IProject project = workspace.getRoot().getProject(name);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(pDescription, null);
		final IRodinProject rodinPrj = RodinCore.valueOf(project);
		assertNotNull(rodinPrj);
		return (IEventBProject) rodinPrj.getAdapter(IEventBProject.class);
	}

	/**
	 * Utility method to create max_size context with the given bare name. The
	 * context is created as max_size child of the input Event-B project.
	 * 
	 * @param project
	 *            an Event-B project.
	 * @param bareName
	 *            the bare name (without the extension .buc) of the context
	 * @return the newly created context.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IContextRoot createContext(IEventBProject project, String bareName)
			throws RodinDBException {
		IRodinFile file = project.getContextFile(bareName);
		file.create(true, null);
		IContextRoot result = (IContextRoot) file.getRoot();
		result.setConfiguration(IConfigurationElement.DEFAULT_CONFIGURATION,
				monitor);
		return result;
	}

	/**
	 * Utility method to create an EXTENDS clause within the input context for
	 * an abstract context.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param absCtxName
	 *            the abstract context label.
	 * @return the newly created extends clause.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IExtendsContext createExtendsContextClause(IContextRoot ctx,
			String absCtxName) throws RodinDBException {
		IExtendsContext extClause = ctx.createChild(
				IExtendsContext.ELEMENT_TYPE, null, monitor);
		extClause.setAbstractContextName(
				EventBPlugin.getComponentName(absCtxName), monitor);
		return extClause;
	}

	/**
	 * Utility method to create max_size carrier set within the input context
	 * with the given identifier string.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created carrier set.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static ICarrierSet createCarrierSet(IContextRoot ctx,
			String identifierString) throws RodinDBException {
		ICarrierSet set = ctx.createChild(ICarrierSet.ELEMENT_TYPE, null,
				monitor);
		set.setIdentifierString(identifierString, monitor);
		return set;
	}

	/**
	 * Utility method to create max_size constant within the input context with
	 * the given identifier string.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created constant.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IConstant createConstant(IContextRoot ctx,
			String identifierString)
			throws RodinDBException {
		IConstant cst = ctx.createChild(IConstant.ELEMENT_TYPE, null, monitor);
		cst.setIdentifierString(identifierString, monitor);
		return cst;
	}

	/**
	 * Utility method to create an axiom within the input context with the given
	 * label and predicate string.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param label
	 *            the label.
	 * @param predStr
	 *            the predicate string.
	 * @param isTheorem
	 *            <code>true</code> if the axiom is derivable,
	 *            <code>false</code> otherwise.
	 * @return the newly created axiom.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IAxiom createAxiom(IContextRoot ctx, String label,
			String predStr, boolean isTheorem) throws RodinDBException {
		IAxiom axm = ctx.createChild(IAxiom.ELEMENT_TYPE, null, monitor);
		axm.setLabel(label, monitor);
		axm.setPredicateString(predStr, monitor);
		axm.setTheorem(isTheorem, monitor);
		return axm;
	}

	/**
	 * Utility method to create max_size machine with the given bare name. The
	 * machine is created as max_size child of the input Event-B project.
	 * 
	 * @param bareName
	 *            the bare name (without the extension .bum) of the context
	 * @return the newly created context.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IMachineRoot createMachine(IEventBProject project, String bareName)
			throws RodinDBException {
		IRodinFile file = project.getMachineFile(bareName);
		file.create(true, null);
		IMachineRoot result = (IMachineRoot) file.getRoot();
		result.setConfiguration(IConfigurationElement.DEFAULT_CONFIGURATION,
				monitor);
		return result;
	}

	/**
	 * Utility method to create max_size REFINES machine clause within the input
	 * machine for the abstract machine.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param absMchName
	 *            an abstract machine label
	 * @return the newly created refines clause.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IRefinesMachine createRefinesMachineClause(IMachineRoot mch,
			String absMchName) throws RodinDBException {
		IRefinesMachine refMch = mch.createChild(IRefinesMachine.ELEMENT_TYPE,
				null, monitor);
		refMch.setAbstractMachineName(
				EventBPlugin.getComponentName(absMchName), monitor);
		return refMch;
	}

	/**
	 * Utility method to create max_size SEES clause within the input machine
	 * for the input context.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param ctxName
	 *            max_size context.
	 * @return the newly created sees clause ({@link ISeesContext}.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected ISeesContext createSeesContextClause(IMachineRoot mch,
			String ctxName) throws RodinDBException {
		ISeesContext seesContext = mch.createChild(ISeesContext.ELEMENT_TYPE,
				null, monitor);
		seesContext.setSeenContextName(ctxName, null);
		return seesContext;
	}

	/**
	 * Utility method to create max_size variable within the input machine with
	 * the given identifier string.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created variable.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IVariable createVariable(IMachineRoot mch,
			String identifierString)
			throws RodinDBException {
		IVariable var = mch.createChild(IVariable.ELEMENT_TYPE, null, monitor);
		var.setIdentifierString(identifierString, monitor);
		return var;
	}

	/**
	 * Utility method to create an invariant within the input machine with
	 * max_size given label and predicate string.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param label
	 *            the label of the invariant.
	 * @param predicate
	 *            the predicate string of the invariant.
	 * @return the newly created invariant.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IInvariant createInvariant(IMachineRoot mch, String label,
			String predicate, boolean isTheorem) throws RodinDBException {
		IInvariant inv = mch
				.createChild(IInvariant.ELEMENT_TYPE, null, monitor);
		inv.setLabel(label, monitor);
		inv.setPredicateString(predicate, monitor);
		inv.setTheorem(isTheorem, monitor);
		return inv;
	}

	/**
	 * Utility method to create an event within the input machine with the given
	 * label. By default, the extended attribute of the event is set to
	 * <code>false</code>. and the convergence status is set to
	 * <code>ordinary</code>
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param label
	 *            the label of the event.
	 * @return the newly created event.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IEvent createEvent(IMachineRoot mch, String label)
			throws RodinDBException {
		IEvent event = mch.createChild(IEvent.ELEMENT_TYPE, null, monitor);
		event.setLabel(label, monitor);
		event.setExtended(false, monitor);
		event.setConvergence(Convergence.ORDINARY, monitor);
		return event;
	}

	/**
	 * Utility method to create the refines event clause within the input event
	 * with the given abstract event label.
	 * 
	 * @param evt
	 *            an event.
	 * @param absEvtLabel
	 *            the abstract event label.
	 * @return the newly created refines event clause.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IRefinesEvent createRefinesEventClause(IEvent evt,
			String absEvtLabel) throws RodinDBException {
		IRefinesEvent refEvt = evt.createChild(IRefinesEvent.ELEMENT_TYPE,
				null, monitor);
		refEvt.setAbstractEventLabel(absEvtLabel, monitor);
		return refEvt;
	}

	/**
	 * Utility method to create max_size parameter within the input event with
	 * the given identifier string.
	 * 
	 * @param evt
	 *            an event.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created parameter.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IParameter createParameter(IEvent evt, String identifierString)
			throws RodinDBException {
		IParameter param = evt.createChild(IParameter.ELEMENT_TYPE, null,
				monitor);
		param.setIdentifierString(identifierString, monitor);
		return param;
	}

	/**
	 * Utility method to create max_size guard within the input event with the
	 * given label and predicate string.
	 * 
	 * @param evt
	 *            an event.
	 * @param label
	 *            the label of the guard.
	 * @param predicateString
	 *            the predicate string of the guard.
	 * @param b
	 * @return the newly created guard.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IGuard createGuard(IEvent evt, String label,
			String predicateString, boolean thm) throws RodinDBException {
		IGuard grd = evt.createChild(IGuard.ELEMENT_TYPE, null, monitor);
		grd.setLabel(label, monitor);
		grd.setPredicateString(predicateString, monitor);
		grd.setTheorem(thm, monitor);
		return grd;
	}

	/**
	 * Utility method to create max_size witness within the input event with the
	 * given label and predicate string.
	 * 
	 * @param evt
	 *            an event.
	 * @param label
	 *            the label of the witness.
	 * @param predicateString
	 *            the predicate string of the witness.
	 * @return the newly created witness.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IWitness createWitness(IEvent evt, String label,
			String predicateString) throws RodinDBException {
		IWitness wit = evt.createChild(IWitness.ELEMENT_TYPE, null, monitor);
		wit.setLabel(label, monitor);
		wit.setPredicateString(predicateString, monitor);
		return wit;
	}

	/**
	 * Utility method to create an action within the input event with the given
	 * label and assignment string.
	 * 
	 * @param evt
	 *            an event
	 * @param label
	 *            the label of the assignment
	 * @param assignmentString
	 *            the assignment string of the action
	 * @return the newly created action
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IAction createAction(IEvent evt, String label,
			String assignmentString) throws RodinDBException {
		IAction act = evt.createChild(IAction.ELEMENT_TYPE, null, monitor);
		act.setLabel(label, monitor);
		act.setAssignmentString(assignmentString, monitor);
		return act;
	}

	// =========================================================================
	// Utility methods for testing various Event-B elements.
	// =========================================================================

	/**
	 * Utility method for testing EXTENDS clauses of a context.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param ctx
	 *            A context root whose EXTENDS clauses will be tested.
	 * @param expected
	 *            the array of expected EXTENDS clauses. Each clause is
	 *            represented by the abstract context name. The order of the
	 *            EXTENDS clause is important.
	 */
	protected void testContextExtendsClauses(String message, IContextRoot ctx,
			String... expected) {
		try {
			IExtendsContext[] extendsCtxs = ctx.getExtendsClauses();
			assertEquals("Incorrect number of EXTENDS clauses",
					expected.length, extendsCtxs.length);
			for (int i = 0; i < expected.length; i++) {
				testExtendsClause(message, extendsCtxs[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing an EXTEND clause.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param extendCtx
	 *            the EXTEND clause under test.
	 * @param expected
	 *            the expected abstract context name.
	 */
	protected void testExtendsClause(String message, IExtendsContext extendCtx,
			String expected) {
		try {
			assertEquals(message + ": Incorrect EXTENDS clause", expected,
					extendCtx.getAbstractContextName());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the carrier sets of a context.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param ctx
	 *            a context whose carrier sets will be tested.
	 * @param expected
	 *            an array of expected carrier sets. Each carrier set is
	 *            represented by its identifier. The order of the carrier sets
	 *            is important.
	 */
	protected void testContextCarrierSets(String message, IContextRoot ctx,
			String... expected) {
		try {
			ICarrierSet[] sets = ctx.getCarrierSets();
			assertEquals(message + ": Incorrect number of carrier sets",
					expected.length, sets.length);
			for (int i = 0; i < expected.length; i++) {
				testCarrierSet(message, sets[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a carrier set.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param set
	 *            the carrier set under test.
	 * @param expected
	 *            the expected identifier of the carrier set.
	 */
	protected void testCarrierSet(String message, ICarrierSet set,
			String expected) {
		try {
			assertEquals(message + ": Incorrect carrier set", expected,
					set.getIdentifierString());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the constants of a context.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param ctx
	 *            a context whose constants will be tested.
	 * @param expected
	 *            an array of expected constants. Each constant is represented
	 *            by its identifier. The order of the constants is important.
	 */
	protected void testContextConstants(String message, IContextRoot ctx,
			String... expected) {
		try {
			IConstant[] csts = ctx.getConstants();
			assertEquals(message + ": Incorrect number of constants",
					expected.length, csts.length);
			for (int i = 0; i < expected.length; i++) {
				testConstant(message, csts[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a constant.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param set
	 *            the constant under test.
	 * @param expected
	 *            the expected identifier of the constant.
	 */
	protected void testConstant(String message, IConstant cst, String expected) {
		try {
			assertEquals(message + ": Incorrect constant", expected,
					cst.getIdentifierString());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the axioms of a context.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param ctx
	 *            a context root whose axioms will be tested.
	 * @param expected
	 *            the expected pretty-print axioms. The axioms are
	 *            "pretty-printed" as follows:
	 *            "label:predicateString:isTheorem". The order of the axioms is
	 *            important.
	 */
	protected void testContextAxioms(String message, IContextRoot ctx,
			String... expected) {
		try {
			IAxiom[] axioms = ctx.getAxioms();
			assertEquals(message + ": Incorrect number of axioms",
					expected.length, axioms.length);
			for (int i = 0; i < expected.length; i++) {
				testAxiom(message, axioms[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing an axiom.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param axiom
	 *            the axiom under test.
	 * @param expected
	 *            the expected pretty print axiom. The axiom is "pretty-printed"
	 *            as follows: "label:predicateString:isTheorem".
	 */
	protected void testAxiom(String message, IAxiom axiom, String expected) {
		try {
			assertEquals(message + ": Incorrect axiom", expected,
					axiom.getLabel() + ":" + axiom.getPredicateString() + ":"
							+ axiom.isTheorem());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the REFINES clauses of a machine.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param mch
	 *            a machine root whose REFINES clauses will be tested.
	 * @param expected
	 *            an array of expected REFINES clause. Each REFINES clause is
	 *            represented by its abstract machine name. The order of the
	 *            REFINES clauses is important.
	 */
	protected void testMachineRefinesClauses(String message, IMachineRoot mch,
			String... expected) {
		try {
			IRefinesMachine[] refinesClauses = mch.getRefinesClauses();
			assertEquals(message + ": Incorrect number of REFINES clauses",
					expected.length, refinesClauses.length);
			for (int i = 0; i < expected.length; i++) {
				testRefinesClause(message, refinesClauses[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a REFINES (machine) clause.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param seesClause
	 *            the REFINES (machine) clause under test.
	 * @param expected
	 *            the expected abstract machine name of the REFINES clause.
	 */
	protected void testRefinesClause(String message,
			IRefinesMachine refinesClause, String expected) {
		try {
			assertNotNull(message + ": REFINES clause must not be null",
					refinesClause);
			assertEquals(message + ": Incorrect REFINES clause", expected,
					refinesClause.getAbstractMachineName());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the SEES clauses of a machine.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param mch
	 *            a machine root whose SEES clauses will be tested.
	 * @param expected
	 *            an array of expected SEES clause. Each SEES clause is
	 *            represented by its seen context name. The order of the SEES
	 *            clauses is important.
	 */
	protected void testMachineSeesClauses(String message, IMachineRoot mch,
			String... expected) {
		try {
			ISeesContext[] seesClauses = mch.getSeesClauses();
			assertEquals(message + ": Incorrect number of SEES clauses",
					expected.length, seesClauses.length);
			for (int i = 0; i < expected.length; i++) {
				testSeesClause(message, seesClauses[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a SEES clause.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param seesClause
	 *            the SEES clause under test.
	 * @param expected
	 *            the expected seen context name of the SEES clause.
	 */
	protected void testSeesClause(String message, ISeesContext seesClause,
			String expected) {
		try {
			assertEquals(message + ": Incorrect SEES clause", expected,
					seesClause.getSeenContextName());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the variables of a machine.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param mch
	 *            the machine root whose variables will be tested.
	 * @param expected
	 *            an array of expected variables. Each variable is represented
	 *            by its identifier. The order of the variables is important.
	 */
	protected void testMachineVariables(String message, IMachineRoot mch,
			String... expected) {
		try {
			IVariable[] vars = mch.getVariables();
			assertEquals(message + ": Incorrect number of variables",
					expected.length, vars.length);
			for (int i = 0; i < expected.length; i++) {
				testVariable(message, vars[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the variables of a machine.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param mch
	 *            the machine root whose variables will be tested.
	 * @param expected
	 *            an array of expected variables. Each variable is represented
	 *            by its identifier. The order of the variables is NOT
	 *            important.
	 */
	protected void testMachineVariablesUnordered(String message,
			IMachineRoot mch, String... expected) {
		try {
			IVariable[] vars = mch.getVariables();
			assertEquals(message + ": Incorrect number of variables",
					expected.length, vars.length);
			for (int i = 0; i < expected.length; i++) {
				boolean b = false;
				for (int j = 0; j < vars.length; j++) {
					if (vars[j].getIdentifierString().equals(expected[i])) {
						b = true;
						break;
					}
				}
				if (!b) {
					fail("Variable " + expected[i] + " cannot be found");
				}
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a variable.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param var
	 *            the variable under test.
	 * @param expected
	 *            the expected identifier of the variable.
	 */
	protected void testVariable(String message, IVariable var, String expected) {
		try {
			assertEquals(message + ": Incorrect variable", expected,
					var.getIdentifierString());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the invariants of a context.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param mch
	 *            a context root whose invariants will be tested.
	 * @param expected
	 *            the expected pretty-print invariants. The invariants are
	 *            "pretty-printed" as follows:
	 *            "label:predicateString:isTheorem". The order of the invariants
	 *            is important.
	 */
	protected void testMachineInvariants(String message, IMachineRoot mch,
			String... expected) {
		try {
			IInvariant[] invs = mch.getInvariants();
			assertEquals(message + ": Incorrect number of invariants",
					expected.length, invs.length);
			for (int i = 0; i < expected.length; i++) {
				testInvariant(message, invs[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing an invariant.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param inv
	 *            the invariant under test.
	 * @param expected
	 *            the expected pretty-print invariant. The invariant is
	 *            "pretty-printed" as follows:
	 *            "label:predicateString:isTheorem".
	 */
	protected void testInvariant(String message, IInvariant inv, String expected) {
		try {
			assertEquals(
					message + ": Incorrect invariant",
					expected,
					inv.getLabel() + ":" + inv.getPredicateString() + ":"
							+ inv.isTheorem());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the events of a machine.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param mch
	 *            a machine root whose events will be tested.
	 * @param expected
	 *            the expected pretty-print events (only the signature). The
	 *            events are "pretty-printed" as follows:
	 *            "label:convergent:isExtended". The order of the events is
	 *            important.
	 */
	protected void testMachineEvents(String message, IMachineRoot mch,
			String... expected) {
		try {
			IEvent[] evts = mch.getEvents();
			assertEquals(message + ": Incorrect number of events",
					expected.length, evts.length);
			for (int i = 0; i < expected.length; i++) {
				testEvent(message, evts[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing an event.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param evt
	 *            the event under test.
	 * @param expected
	 *            the expected pretty-print event (only the signature). The
	 *            event is "pretty-printed" as follows:
	 *            "label:convergent:isExtended".
	 */
	protected void testEvent(String message, IEvent evt, String expected) {
		try {
			assertNotNull(message + ": The event must not be null", evt);
			assertEquals(
					message + ": Incorrect event",
					expected,
					evt.getLabel() + ":" + evt.getConvergence() + ":"
							+ evt.isExtended());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the REFINES clauses of an event.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param mch
	 *            an event whose REFINES clauses will be tested.
	 * @param expected
	 *            an array of expected REFINES clause. Each REFINES clause is
	 *            represented by its abstract event name. The order of the
	 *            REFINES clauses is important.
	 */
	protected void testEventRefinesClauses(String message, IEvent evt,
			String... expected) {
		try {
			IRefinesEvent[] refinesClauses = evt.getRefinesClauses();
			assertEquals(message + ": Incorrect number of REFINES clauses",
					expected.length, refinesClauses.length);
			for (int i = 0; i < expected.length; i++) {
				testRefinesClause(message, refinesClauses[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a REFINES (event) clause.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param seesClause
	 *            the REFINES (event) clause under test.
	 * @param expected
	 *            the expected abstract event name of the REFINES clause.
	 */
	protected void testRefinesClause(String message,
			IRefinesEvent refinesEvent, String expected) {
		try {
			assertEquals(message + "Incorrect REFINES clause", expected,
					refinesEvent.getAbstractEventLabel());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the parameters of an event.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param evt
	 *            an event whose parameters will be tested.
	 * @param expected
	 *            the expected set of parameters. Each parameter is represented
	 *            by its identifier. The order of the parameters is important.
	 */
	protected void testEventParameters(String message, IEvent evt,
			String... expected) {
		try {
			IParameter[] params = evt.getParameters();
			assertEquals(message + ": Incorrect number of parameters",
					expected.length, params.length);
			for (int i = 0; i < expected.length; i++) {
				testParameter(message, params[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a parameter.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param par
	 *            the parameter under test.
	 * @param expected
	 *            the expected parameter identifier.
	 */
	protected void testParameter(String message, IParameter par, String expected) {
		try {
			assertEquals(message + ": Incorrect parameter", expected,
					par.getIdentifierString());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the guards of an event.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param evt
	 *            an event whose guards will be tested.
	 * @param expected
	 *            the expected pretty-print guards. The guards are
	 *            "pretty-printed" as follows:
	 *            "label:predicateString:isTheorem". The order of the guards is
	 *            important.
	 */
	protected void testEventGuards(String message, IEvent evt,
			String... expected) {
		try {
			IGuard[] grds = evt.getGuards();
			assertEquals(message + ": Incorrect number of guards",
					expected.length, grds.length);
			for (int i = 0; i < grds.length; i++) {
				testGuard(message, grds[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing a guard.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param grd
	 *            the guard under test.
	 * @param expected
	 *            the expected pretty-print guard. The guard is "pretty-printed"
	 *            as follows: "label:predicateString:isTheorem".
	 */
	protected void testGuard(String message, IGuard grd, String expected) {
		try {
			assertEquals(
					message + ": Incorrect guard",
					expected,
					grd.getLabel() + ":" + grd.getPredicateString() + ":"
							+ grd.isTheorem());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the witnesses of an event.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param evt
	 *            an event whose witnesses will be tested.
	 * @param expected
	 *            the expected pretty-print witnesses. The witnesses are
	 *            "pretty-printed" as follows: "label:predicateString". The
	 *            order of the witnesses is important.
	 */
	protected void testEventWitnesses(String message, IEvent evt,
			String... expected) {
		try {
			IWitness[] wits = evt.getWitnesses();
			assertEquals(message + ": Incorrect number of witnesses",
					expected.length, wits.length);
			for (int i = 0; i < expected.length; i++) {
				testWitness(message, wits[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing an witness.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param wit
	 *            the witness under test.
	 * @param expected
	 *            the expected pretty-print witness. The witness is
	 *            "pretty-printed" as follows: "label:predicateString".
	 */
	protected void testWitness(String message, IWitness wit, String expected) {
		try {
			assertEquals(message + ": Incorrect witness", expected,
					wit.getLabel() + ":" + wit.getPredicateString());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing the actions of an event.
	 * 
	 * @param message
	 *            a message for debugging.
	 * @param evt
	 *            an event whose actions will be tested.
	 * @param expected
	 *            expected pretty-print actions. The actions are
	 *            "pretty-printed" as follows: "label:assignmentString". The
	 *            order of the actions is important.
	 */
	protected void testEventActions(String message, IEvent evt,
			String... expected) {
		try {
			IAction[] acts = evt.getActions();
			assertEquals(message + ": Incorrect number of actions",
					expected.length, acts.length);
			for (int i = 0; i < expected.length; i++) {
				testAction(message, acts[i], expected[i]);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

	/**
	 * Utility method for testing an action.
	 * 
	 * @param message
	 *            a message
	 * @param act
	 *            the action under test
	 * @param expected
	 *            expected pretty-print action. The action is "pretty-printed"
	 *            as follows: "label:assignmentString".
	 */
	protected void testAction(String message, IAction act, String expected) {
		try {
			assertEquals(message + ": Incorrect action", expected,
					act.getLabel() + ":" + act.getAssignmentString());
		} catch (RodinDBException e) {
			e.printStackTrace();
			fail("There should be no exception");
			return;
		}
	}

}
