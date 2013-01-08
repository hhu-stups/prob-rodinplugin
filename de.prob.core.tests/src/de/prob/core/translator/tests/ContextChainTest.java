package de.prob.core.translator.tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.junit.Before;
import org.junit.Test;

import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;

public class ContextChainTest extends AbstractEventBTests {
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
	public void testEmptyContextChain() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context1 = createContext(project, "TestContext1");
		IContextRoot context2 = createContext(project, "TestContext2");

		createExtendsContextClause(context2, "TestContext1");

		context1.getRodinFile().save(monitor, false);
		context2.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		TranslatorFactory.translate(context2, writer);

		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext2',[extends(none,['TestContext1']),constants(none,[]),axioms(none,[]),theorems(none,[]),sets(none,[])]),event_b_context(none,'TestContext1',[extends(none,[]),constants(none,[]),axioms(none,[]),theorems(none,[]),sets(none,[])])],[exporter_version(2)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}
}
