package de.prob.core.translator.tests;

import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.TranslatorFactory;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class TranslationTest extends AbstractEventBTests {
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
	public void testTranslationOfNegativeIntegers() throws CoreException, TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		createConstant(context, "neg");
		createAxiom(context, "axm1", "neg=−5", false); // expect unary_minus expression

		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		TranslatorFactory.translate(context, writer);
		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext',[extends(none,[]),constants(none,[identifier(none,neg)]),abstract_constants(none,[]),axioms(none,[equal(rodinpos('TestContext',axm1,'('),identifier(none,neg),unary_minus(none,integer(none,5)))]),theorems(none,[]),sets(none,[])])],[exporter_version(3)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}
}
