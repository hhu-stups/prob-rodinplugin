package de.prob.eventb.translator2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eventb.core.IEventBRoot;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.Project;
import org.eventb.emf.core.context.Axiom;
import org.eventb.emf.core.context.CarrierSet;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.persistence.ProjectResource;
import org.rodinp.core.IRodinProject;

import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.PSet;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.prolog.output.IPrologTermOutput;

public class EMFEventBTranslator {
	private final Project project;

	public EMFEventBTranslator(IEventBRoot main) {
		IRodinProject rodinProject = main.getRodinProject();
		ProjectResource resource = new ProjectResource(rodinProject);
		try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		project = (Project) resource.getContents().get(0);
	}

	public void print(IPrologTermOutput pto) {
		for (EventBNamedCommentedComponentElement e : project.getComponents()) {
			if (e instanceof Context) {
				printContext(pto, (Context) e);
			} else {
				printMachine(pto, (Machine) e);
			}
		}
	}

	public void printContext(IPrologTermOutput pto, Context c) {
		EList<CarrierSet> sets = c.getSets();
		final List<PSet> setList = new ArrayList<PSet>(sets.size());
		for (CarrierSet s : sets) {
			final ADeferredSetSet deferredSet = new ADeferredSetSet(
					Arrays.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
							s.getName()) }));
			setList.add(deferredSet);
		}
		
		EList<Axiom> axioms = c.getAxioms();
		
		

	}

	public void printMachine(IPrologTermOutput pto, Machine m) {

	}

}
