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
	public void testTranslationOfConjuncts() throws CoreException, TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		// check conjunction list is translated left-associatively
		createAxiom(context, "conj1", "1=2 ∧ 2=3 ∧ 3=4", false);
		createAxiom(context, "conj2", "(1=2 ∧ 2=3) ∧ 3=4", false);
		createAxiom(context, "conj3", "1=2 ∧ (2=3 ∧ 3=4)", false);

		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		TranslatorFactory.translate(context, writer);
		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext',[extends(none,[]),constants(none,[]),abstract_constants(none,[]),axioms(none,[conjunct(rodinpos('TestContext',conj1,'\\''),[equal(none,integer(none,1),integer(none,2)),equal(none,integer(none,2),integer(none,3)),equal(none,integer(none,3),integer(none,4))]),conjunct(rodinpos('TestContext',conj2,'('),[equal(none,integer(none,1),integer(none,2)),equal(none,integer(none,2),integer(none,3)),equal(none,integer(none,3),integer(none,4))]),conjunct(rodinpos('TestContext',conj3,')'),[equal(none,integer(none,1),integer(none,2)),conjunct(none,[equal(none,integer(none,2),integer(none,3)),equal(none,integer(none,3),integer(none,4))])])]),theorems(none,[]),sets(none,[])])],[exporter_version(3)],_Error)).\n",
				stringWriter.getBuffer().toString());
	}

	@Test
	public void testTranslationOfDisjuncts() throws CoreException, TranslationFailedException {
		IEventBProject project = createEventBProject("TestProject");
		IContextRoot context = createContext(project, "TestContext");

		// check disjunction list is translated left-associatively
		createAxiom(context, "conj1", "1=2 ∨ 2=3 ∨ 3=4", false);
		createAxiom(context, "conj2", "(1=2 ∨ 2=3) ∨ 3=4", false);
		createAxiom(context, "conj3", "1=2 ∨ (2=3 ∨ 3=4)", false);

		context.getRodinFile().save(monitor, false);
		workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		TranslatorFactory.translate(context, writer);
		assertEquals(
				"package(load_event_b_project([],[event_b_context(none,'TestContext',[extends(none,[]),constants(none,[]),abstract_constants(none,[]),axioms(none,[disjunct(rodinpos('TestContext',conj1,'\\''),disjunct(none,equal(none,integer(none,1),integer(none,2)),equal(none,integer(none,2),integer(none,3))),equal(none,integer(none,3),integer(none,4))),disjunct(rodinpos('TestContext',conj2,'('),disjunct(none,equal(none,integer(none,1),integer(none,2)),equal(none,integer(none,2),integer(none,3))),equal(none,integer(none,3),integer(none,4))),disjunct(rodinpos('TestContext',conj3,')'),equal(none,integer(none,1),integer(none,2)),disjunct(none,equal(none,integer(none,2),integer(none,3)),equal(none,integer(none,3),integer(none,4))))]),theorems(none,[]),sets(none,[])])],[exporter_version(3)],_Error)).\n",
				stringWriter.getBuffer().toString());
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
