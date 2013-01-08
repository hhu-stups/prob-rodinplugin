package de.prob.core.translator.tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;

import de.prob.core.translator.TranslationFailedException;

public class MachineWithVariablesTest extends AbstractEventBTests {
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
	public void testMachineWithVariables() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IMachineRoot machine = createMachine(project, "TestMachine");

		createVariable(machine, "v1");
		createInvariant(machine, "inv1", "v1=5", false);

		// save file and build workspace - this triggers static check, and
		// generates missing files
		machine.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		// there should be one variable and one SC variable
		assertEquals(1, machine.getVariables().length);
		assertEquals(1, machine.getSCMachineRoot().getSCVariables().length);

		// TranslatorFactory.translate(machine, writer);

		// assertEquals(
		// "package(load_event_b_project([event_b_model(none,'TestMachine',[sees(none,[]),variables(none,[identifier(none,v1)]),invariant(none,[equal(rodinpos('TestMachine',inv1,'('),identifier(none,v1),integer(none,5))]),theorems(none,[]),events(none,[])])],[],[exporter_version(2)],_Error)).\n",
		// stringWriter.getBuffer().toString());
	}
}
