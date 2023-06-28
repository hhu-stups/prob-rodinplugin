package de.prob.core.translator.tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;

import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;

import static org.junit.Assert.assertEquals;

public class EmptyTranslationsTest extends AbstractEventBTests {
	private StringWriter stringWriter;
	private PrintWriter writer;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
	}

	@Test
	public void testEmptyMachine() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IMachineRoot machine = createMachine(project, "TestMachine");

		machine.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		TranslatorFactory.translate(machine, writer);

		assertEquals(
				"package(load_event_b_project([event_b_model(none,'TestMachine',[sees(none,[]),variables(none,[]),invariant(none,[]),theorems(none,[]),events(none,[])])],[],[exporter_version(3)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}

	@Test
	public void testEmptyContext() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		TranslatorFactory.translate(context, writer);

		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext',[extends(none,[]),constants(none,[]),abstract_constants(none,[]),axioms(none,[]),theorems(none,[]),sets(none,[])])],[exporter_version(3)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}
}
