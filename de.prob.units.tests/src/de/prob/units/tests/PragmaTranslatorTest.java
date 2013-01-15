package de.prob.units.tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;

public class PragmaTranslatorTest extends AbstractEventBTests {
	final IAttributeType.String UNITATTRIBUTE = RodinCore
			.getStringAttrType("de.prob.units.unitPragmaAttribute");

	private StringWriter stringWriter;
	private PrintWriter writer;

	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
	}

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

		TranslatorFactory.translate(machine, writer);

		assertEquals(
				"package(load_event_b_project([event_b_model(none,'TestMachine',[sees(none,[]),variables(none,[identifier(none,v1)]),invariant(none,[equal(rodinpos('TestMachine',inv1,'('),identifier(none,v1),integer(none,5))]),theorems(none,[]),events(none,[])])],[],[exporter_version(2),pragma(unit,'TestMachine',v1,[test])],_Error)).\n",
				stringWriter.getBuffer().toString());
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
		project.getRodinProject().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		// there should be one constant and one SC constant
		// the constant holds the attribute
		assertEquals(1, context.getConstants().length);
		assertEquals("test",
				context.getConstants()[0].getAttributeValue(UNITATTRIBUTE));

		assertEquals(1, context.getSCContextRoot().getSCConstants().length);

		TranslatorFactory.translate(context, writer);

		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext',[extends(none,[]),constants(none,[identifier(none,cst1)]),axioms(none,[equal(rodinpos('TestContext',axm1,'('),identifier(none,cst1),integer(none,5))]),theorems(none,[]),sets(none,[])])],[exporter_version(2)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}
}
