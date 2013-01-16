package de.prob.units.tests;

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

public class PragmaAttributesTest extends AbstractEventBTests {
	final IAttributeType.String UNITATTRIBUTE = RodinCore
			.getStringAttrType("de.prob.units.unitPragmaAttribute");

	@Test
	public void testMachineWithUnitPragmaOnVariable() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IMachineRoot machine = createMachine(project, "TestMachine");

		IVariable v1 = createVariable(machine, "v1");
		createInvariant(machine, "inv1", "v1=5", false);

		// add unit pragma to variable
		v1.setAttributeValue(UNITATTRIBUTE, "test", new NullProgressMonitor());

		// save file and build workspace - this triggers static check, and
		// generates missing files
		machine.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		// there should be one variable and one SC variable
		assertEquals(1, machine.getVariables().length);
		assertEquals(1, machine.getSCMachineRoot().getSCVariables().length);

		// and both should hold our attribute
		assertEquals("test",
				machine.getVariables()[0].getAttributeValue(UNITATTRIBUTE));
		assertEquals("test",
				machine.getSCMachineRoot().getSCVariables()[0]
						.getAttributeValue(UNITATTRIBUTE));
	}

	@Test
	public void testContextWithUnitPragmaOnConstant() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		IConstant c1 = createConstant(context, "cst1");
		createAxiom(context, "axm1", "cst1=5", false);

		// add unit pragma to constant
		c1.setAttributeValue(UNITATTRIBUTE, "test", new NullProgressMonitor());

		// save file and build workspace - this triggers static check, and
		// generates missing files
		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		// there should be one constant and one SC constant
		// both holding the attribute
		assertEquals(1, context.getConstants().length);
		assertEquals(1, context.getSCContextRoot().getSCConstants().length);

		// and both should hold our attribute
		assertEquals("test",
				context.getConstants()[0].getAttributeValue(UNITATTRIBUTE));
		assertEquals("test",
				context.getSCContextRoot().getSCConstants()[0]
						.getAttributeValue(UNITATTRIBUTE));

	}
}
