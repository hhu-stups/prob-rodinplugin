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

public class ContextWithConstants extends AbstractEventBTests {
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
	public void testContextWithConstants() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		createConstant(context, "cst1");
		createAxiom(context, "axm1", "cst1=5", false);

		// save file and build workspace - this triggiers static check, and
		// generates missing files
		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		// there should be one constant and one SC constant
		assertEquals(1, context.getConstants().length);
		assertEquals(1, context.getSCContextRoot().getSCConstants().length);

		TranslatorFactory.translate(context, writer);

		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext',[extends(none,[]),constants(none,[identifier(none,cst1)]),axioms(none,[equal(rodinpos('TestContext',axm1,'('),identifier(none,cst1),integer(none,5))]),theorems(none,[]),sets(none,[])])],[exporter_version(2)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}
}
