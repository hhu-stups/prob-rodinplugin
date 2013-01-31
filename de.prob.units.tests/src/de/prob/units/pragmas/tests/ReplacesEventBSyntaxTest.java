package de.prob.units.pragmas.tests;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

import de.prob.core.translator.TranslationFailedException;
import de.prob.units.tests.AbstractEventBTests;

public class ReplacesEventBSyntaxTest extends AbstractEventBTests {
	final IAttributeType.String UNITATTRIBUTE = RodinCore
			.getStringAttrType("de.prob.units.unitPragmaAttribute");

	@Test
	public void testReplacesOnVariable() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IMachineRoot machine = createMachine(project, "TestMachine");

		IVariable v1 = createVariable(machine, "v1");
		createInvariant(machine, "inv1", "v1=5", false);

		// add unit pragma to variable
		v1.setAttributeValue(UNITATTRIBUTE, "m^2", new NullProgressMonitor());

		// save file and build workspace - this triggers static check, and
		// generates missing files
		machine.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		assertEquals("m^2",
				machine.getVariables()[0].getAttributeValue(UNITATTRIBUTE));
		assertEquals("m**2",
				machine.getSCMachineRoot().getSCVariables()[0]
						.getAttributeValue(UNITATTRIBUTE));
	}

	@Test
	public void testReplacesOnConstant() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		IConstant c1 = createConstant(context, "cst1");
		createAxiom(context, "axm1", "cst1=5", false);

		// add unit pragma to constant
		c1.setAttributeValue(UNITATTRIBUTE, "m^2", new NullProgressMonitor());

		// save file and build workspace - this triggers static check, and
		// generates missing files
		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		assertEquals("m^2",
				context.getConstants()[0].getAttributeValue(UNITATTRIBUTE));
		assertEquals("m**2",
				context.getSCContextRoot().getSCConstants()[0]
						.getAttributeValue(UNITATTRIBUTE));

	}
}
