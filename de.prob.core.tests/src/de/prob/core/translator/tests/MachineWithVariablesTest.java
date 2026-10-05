package de.prob.core.translator.tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MachineWithVariablesTest extends AbstractEventBTests {
	private StringWriter stringWriter;
	private PrintWriter writer;

	@BeforeEach
	@Override
	public void setUp() throws Exception {
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

		TranslatorFactory.translate(machine, writer);

		assertEquals(
				"package(load_event_b_project([event_b_model(none,'TestMachine',[sees(none,[]),variables(none,[identifier(none,v1)]),invariant(none,[equal(rodinpos('TestMachine',inv1,'('),identifier(none,v1),integer(none,5))]),theorems(none,[]),events(none,[])])],[],[exporter_version(3)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}
}
