/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.prob.core.command.GetTheoryMappingTargetsCommand.Default;
import de.prob.core.command.GetTheoryMappingTargetsCommand.Target;
import de.prob.eventb.translator.TheoryMappingFiles.AxiomaticOperator;

/**
 * Writes the lines of a theory mapping file (.ptm) for the axiomatic
 * operators and types of a theory: for each, a line
 * <code>operator "Op" internal {Tag}</code> with the tag ProB would use by
 * default, or a commented-out line to complete if there is no default. At the
 * end, the tags probcli supports are listed by number of arguments.
 * <p>
 * The lines only make ProB's defaults explicit (and so remove ProB's
 * warnings about them); operators without a default are left commented out,
 * so the file does not change how ProB translates the theory until it is
 * edited.
 */
public final class TheoryMappingTemplate {

	private static final String URL = "https://prob.hhu.de/w/index.php?title=Event-B_Theories";

	/** probcli's tags and defaults; null if probcli cannot list them */
	private final List<Target> targets;
	private final List<Default> defaults;

	public TheoryMappingTemplate(final List<Target> targets,
			final List<Default> defaults) {
		this.targets = targets;
		this.defaults = defaults;
	}

	/**
	 * @return the contents of a new mapping file for the operators
	 * @param date
	 *            e.g. 2026-11-26, for the header
	 */
	public String newFile(final String theory, final String project,
			final List<AxiomaticOperator> operators, final String date) {
		final StringBuilder out = new StringBuilder();
		out.append("// ProB mapping file for the theory ").append(theory)
				.append(" of the project ").append(project)
				.append(", created by ProB for Rodin on ").append(date)
				.append(".\n");
		out.append("// Each line maps an axiomatic operator or type of the theory to a construct of ProB:\n");
		out.append("//   operator \"Name\" internal {Tag}\n");
		out.append("// The tag can also be a B formula between dollar signs; for an operator with arguments\n");
		out.append("// it must be a lambda expression or set comprehension, e.g.\n");
		out.append("//   operator \"mod2\" internal {$%(x,y).(x:INTEGER & y:INTEGER | x mod y)$}\n");
		out.append("// Lines starting with // are comments. The possible tags are listed at the end.\n");
		out.append("// More information: ").append(URL).append('\n');
		out.append('\n');
		appendOperators(out, operators);
		appendTags(out);
		return out.toString();
	}

	/**
	 * @return the lines to add to an existing mapping file for the operators
	 *         it does not map yet, or null if it maps all of them
	 */
	public String additions(final List<AxiomaticOperator> operators,
			final Set<String> mapped, final String date) {
		final List<AxiomaticOperator> missing = new ArrayList<AxiomaticOperator>();
		for (AxiomaticOperator op : operators)
			if (!mapped.contains(op.name))
				missing.add(op);
		if (missing.isEmpty())
			return null;
		final StringBuilder out = new StringBuilder();
		out.append("\n// Added by ProB for Rodin on ").append(date)
				.append(": the axiomatic operators and types without a line above.\n");
		appendOperators(out, missing);
		return out.toString();
	}

	private void appendOperators(final StringBuilder out,
			final List<AxiomaticOperator> operators) {
		for (AxiomaticOperator op : operators) {
			out.append("// ").append(signature(op)).append('\n');
			final List<Default> found = defaultsFor(op);
			final Default chosen = choose(op, found);
			if (chosen != null) {
				out.append(line(op.name, chosen.tag)).append(" // ProB's default");
				final Target target = target(chosen.tag);
				if (target != null)
					out.append(": ").append(target.description());
				if (!"any".equals(chosen.argType))
					out.append(", for ").append(chosen.argType).append("s");
				out.append('\n');
			} else {
				if (!found.isEmpty()) {
					// typed defaults whose type does not fit the arguments
					out.append("// ProB's defaults for other argument types:");
					for (Default d : found)
						out.append(' ').append(d.tag).append(" (")
								.append(d.argType).append("s)");
					out.append('\n');
				} else if (targets != null) {
					out.append("// no default: choose a tag for ")
							.append(arguments(op.arity()))
							.append(" (see the end of the file) or write a B formula\n");
				}
				out.append("// ").append(line(op.name, "TODO")).append('\n');
			}
			out.append('\n');
		}
	}

	private static String line(final String name, final String tag) {
		return "operator \"" + name + "\" internal {" + tag + "}";
	}

	/** @return e.g. "div(a: ℤ, b: ℤ)", "REAL (axiomatic type)" */
	static String signature(final AxiomaticOperator op) {
		if (op.type)
			return op.name + " (axiomatic type)";
		if (op.arity() == 0)
			return op.name + " (axiomatic operator without arguments)";
		final StringBuilder s = new StringBuilder(op.name).append('(');
		for (int i = 0; i < op.arity(); i++) {
			if (i > 0)
				s.append(", ");
			s.append(op.arguments.get(i)).append(": ")
					.append(op.argumentTypes.get(i));
		}
		return s.append(')').toString();
	}

	/**
	 * @return the tag ProB uses for the operator without a .ptm line, if its
	 *         arguments fit; null if there is none (or probcli cannot tell)
	 */
	public Default defaultFor(final AxiomaticOperator op) {
		return choose(op, defaultsFor(op));
	}

	/** @return the tag, or null if probcli does not know it (or cannot tell) */
	public Target target(final String tag) {
		if (targets != null)
			for (Target t : targets)
				if (t.tag.equals(tag))
					return t;
		return null;
	}

	/** @return whether probcli told the tags */
	public boolean knowsTargets() {
		return targets != null;
	}

	private List<Default> defaultsFor(final AxiomaticOperator op) {
		final List<Default> result = new ArrayList<Default>();
		if (defaults != null)
			for (Default d : defaults)
				if (d.operator.equals(op.name) && d.arity == op.arity())
					result.add(d);
		return result;
	}

	/** @return the default for the arguments' types, or null */
	private static Default choose(final AxiomaticOperator op,
			final List<Default> found) {
		for (Default d : found)
			if ("any".equals(d.argType) || allOfType(op, d.argType))
				return d;
		return null;
	}

	private static boolean allOfType(final AxiomaticOperator op,
			final String argType) {
		for (String type : op.argumentTypes) {
			final boolean fits = "integer".equals(argType) ? "ℤ"
					.equals(type) : "real".equals(argType)
					&& ("ℝ".equals(type) || "REAL".equals(type));
			if (!fits)
				return false;
		}
		return true;
	}

	private static String arguments(final int arity) {
		return arity == 0 ? "operators without arguments"
				: arity == 1 ? "1 argument" : arity + " arguments";
	}

	/** lists the tags by number of arguments */
	private void appendTags(final StringBuilder out) {
		if (targets == null) {
			out.append("// This probcli cannot list the possible tags (it has no get_theory_mapping_targets/2);\n");
			out.append("// see ").append(URL).append('\n');
			return;
		}
		final Map<Integer, List<Target>> byArity = new TreeMap<Integer, List<Target>>();
		for (Target t : targets) {
			List<Target> list = byArity.get(t.arity);
			if (list == null) {
				list = new ArrayList<Target>();
				byArity.put(t.arity, list);
			}
			list.add(t);
		}
		out.append("// ---------------------------------------------------------------------------\n");
		out.append("// The tags of this ProB version, by the number of the operator's arguments.\n");
		out.append("// An operator without arguments can also be mapped to a tag for 1 or 2 arguments:\n");
		out.append("// it then stands for a lambda expression (or a set for a predicate).\n");
		for (Map.Entry<Integer, List<Target>> e : byArity.entrySet()) {
			out.append("//\n// Tags for ")
					.append(e.getKey() == 0 ? "operators and types without arguments"
							: "operators with " + arguments(e.getKey()))
					.append(":\n");
			for (Target t : e.getValue())
				out.append("//   ").append(pad(t.tag, 24)).append(' ')
						.append(t.description()).append('\n');
		}
	}

	private static String pad(final String s, final int width) {
		final StringBuilder b = new StringBuilder(s);
		while (b.length() < width)
			b.append(' ');
		return b.toString();
	}
}
