/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.translator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCAxiomaticOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ITheoryPathRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import de.prob.core.internal.Activator;
import de.prob.tmparser.OperatorMapping;
import de.prob.tmparser.TheoryMappingException;
import de.prob.tmparser.TheoryMappingParser;

/**
 * The theory mapping files (.ptm) of Rodin theories: for each deployed
 * theory, ProB reads <code>T.ptm</code> in the theory's project (see
 * {@link Theories}), which maps the theory's axiomatic operators and types to
 * constructs of ProB. This class finds the theories with axiomatic operators
 * or types, for creating such files (see {@link TheoryMappingTemplate}).
 * <p>
 * Needs the Theory plug-in: check with {@link #isTheoryPluginInstalled()}.
 */
public final class TheoryMappingFiles {

	public static final String EXTENSION = "ptm";

	/** an axiomatic operator or type of a theory */
	public static final class AxiomaticOperator {
		public final String name;
		/** the arguments' names */
		public final List<String> arguments;
		/** the arguments' types, e.g. ℤ or ℙ(ℤ×ℤ) */
		public final List<String> argumentTypes;
		/** whether it is an axiomatic type (no arguments) */
		public final boolean type;

		public AxiomaticOperator(final String name,
				final List<String> arguments,
				final List<String> argumentTypes, final boolean type) {
			this.name = name;
			this.arguments = Collections.unmodifiableList(arguments);
			this.argumentTypes = Collections.unmodifiableList(argumentTypes);
			this.type = type;
		}

		public int arity() {
			return arguments.size();
		}
	}

	/** a deployed theory with axiomatic operators or types */
	public static final class Theory {
		/** the theory's project, where ProB looks for the .ptm file */
		public final IProject project;
		public final String name;
		public final List<AxiomaticOperator> operators;

		Theory(final IProject project, final String name,
				final List<AxiomaticOperator> operators) {
			this.project = project;
			this.name = name;
			this.operators = Collections.unmodifiableList(operators);
		}

		/** @return the theory's mapping file, which may not exist */
		public IFile mappingFile() {
			return project.getFile(name + "." + EXTENSION);
		}
	}

	private TheoryMappingFiles() {
	}

	/** @return whether the Theory plug-in is installed */
	public static boolean isTheoryPluginInstalled() {
		try {
			Theories.touch();
			return true;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}

	/**
	 * @return the deployed theories with axiomatic operators or types that the
	 *         project uses (via its theory path) or that are deployed in it
	 *         (for a theory project), each once
	 */
	public static List<Theory> theoriesOf(final IProject project)
			throws CoreException {
		final IRodinProject rodinProject = RodinCore.valueOf(project);
		final Map<String, IDeployedTheoryRoot> deployed = new LinkedHashMap<String, IDeployedTheoryRoot>();
		if (rodinProject == null || !rodinProject.exists())
			return Collections.emptyList();
		for (IDeployedTheoryRoot theory : rodinProject
				.getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE))
			add(deployed, theory);
		for (ITheoryPathRoot path : rodinProject
				.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE))
			for (IAvailableTheoryProject ap : path
					.getAvailableTheoryProjects())
				for (IAvailableTheory at : ap.getTheories())
					add(deployed, at.getDeployedTheory());
		final List<Theory> result = new ArrayList<Theory>();
		for (IDeployedTheoryRoot theory : deployed.values()) {
			final List<AxiomaticOperator> operators = axiomaticOperators(theory);
			if (!operators.isEmpty())
				result.add(new Theory(theory.getRodinProject().getProject(),
						theory.getComponentName(), operators));
		}
		return result;
	}

	private static void add(final Map<String, IDeployedTheoryRoot> deployed,
			final IDeployedTheoryRoot theory) {
		if (theory != null && theory.exists())
			deployed.put(theory.getRodinProject().getElementName() + "/"
					+ theory.getComponentName(), theory);
	}

	private static List<AxiomaticOperator> axiomaticOperators(
			final IDeployedTheoryRoot theory) throws CoreException {
		final FormulaFactory ff = theory.getFormulaFactory();
		final List<AxiomaticOperator> result = new ArrayList<AxiomaticOperator>();
		for (ISCAxiomaticDefinitionsBlock block : theory
				.getSCAxiomaticDefinitionsBlocks()) {
			for (ISCIdentifierElement type : block
					.getAxiomaticTypeDefinitions())
				result.add(new AxiomaticOperator(type.getIdentifierString(),
						Collections.<String> emptyList(), Collections
								.<String> emptyList(), true));
			for (ISCAxiomaticOperatorDefinition opdef : block
					.getAxiomaticOperatorDefinitions()) {
				final List<String> names = new ArrayList<String>();
				final List<String> types = new ArrayList<String>();
				for (ISCOperatorArgument argument : opdef
						.getOperatorArguments()) {
					names.add(argument.getIdentifierString());
					String type;
					try {
						type = String.valueOf(argument.getType(ff));
					} catch (CoreException e) {
						type = "?";
					}
					types.add(type);
				}
				// the label is the operator's name (see Theories)
				result.add(new AxiomaticOperator(opdef.getLabel(), names,
						types, false));
			}
		}
		return result;
	}

	/**
	 * @return the names of the operators the existing mapping file maps; empty
	 *         if it does not exist
	 * @throws CoreException
	 *             if it cannot be read or parsed (it must then not be changed)
	 */
	public static Set<String> mappedOperators(final Theory theory)
			throws CoreException {
		final IFile file = theory.mappingFile();
		final Set<String> result = new LinkedHashSet<String>();
		if (!file.exists())
			return result;
		try (InputStream input = file.getContents(true);
				Reader reader = new InputStreamReader(input, file.getCharset())) {
			for (OperatorMapping mapping : TheoryMappingParser
					.parseTheoryMapping(theory.name, reader))
				result.add(mapping.getOperatorName());
		} catch (IOException | TheoryMappingException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, "Could not read " + file.getFullPath()
							+ ": " + e.getMessage(), e));
		}
		return result;
	}
}
