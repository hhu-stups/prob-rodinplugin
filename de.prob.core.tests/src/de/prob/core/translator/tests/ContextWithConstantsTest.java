package de.prob.core.translator.tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;

import static org.junit.Assert.assertEquals;

public class ContextWithConstantsTest extends AbstractEventBTests {
	private StringWriter stringWriter;
	private PrintWriter writer;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
	}

	@Ignore
	@Test
	public void testContextWithConstants() throws CoreException,
			TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		createConstant(context, "cst1");
		createAxiom(context, "axm1", "cst1=5", false);

		// save file and build workspace - this triggers static check, and
		// generates missing files
		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		// there should be one constant and one SC constant
		assertEquals(1, context.getConstants().length);
		assertEquals(1, context.getSCContextRoot().getSCConstants().length);

		TranslatorFactory.translate(context, writer);

		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext',[extends(none,[]),constants(none,[identifier(none,cst1)]),abstract_constants(none,[]),axioms(none,[equal(rodinpos('TestContext',axm1,'('),identifier(none,cst1),integer(none,5))]),theorems(none,[]),sets(none,[])])],[exporter_version(3)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}
}
