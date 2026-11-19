/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.translator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.eventb.core.EventBAttributes;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * Where the Event-B translator is, e.g. "machine M1 › event inc › guard grd1:
 * x > 0", to tell the user where a translation failed (see
 * {@link TranslationFailedException}). Each thread has its own location.
 * <p>
 * A part is entered before it is translated and left only when its
 * translation succeeded, so that after a failure the location still names
 * the part that failed.
 */
public final class TranslationLocation {

	private static final ThreadLocal<Deque<String>> PARTS = ThreadLocal
			.withInitial(ArrayDeque::new);

	/** longer formulas are cut off in the description */
	private static final int MAX_FORMULA = 300;

	private TranslationLocation() {
	}

	/** at the start of a translation */
	public static void reset() {
		PARTS.get().clear();
	}

	/** @param part e.g. "event inc" */
	public static void enter(final String part) {
		PARTS.get().push(part);
	}

	/** enters an element, e.g. "guard grd1: x > 0" */
	public static void enter(final IInternalElement element) {
		enter(describe(element));
	}

	/** after the part entered last has been translated */
	public static void leave() {
		final Deque<String> parts = PARTS.get();
		if (!parts.isEmpty())
			parts.pop();
	}

	/**
	 * @return e.g. "machine M1 › event inc › guard grd1: x > 0", or null if
	 *         no translation is in progress
	 */
	public static String describe() {
		final List<String> parts = new ArrayList<String>(PARTS.get());
		if (parts.isEmpty())
			return null;
		Collections.reverse(parts);
		return String.join(" › ", parts);
	}

	/** e.g. "guard grd1: x > 0" */
	public static String describe(final IInternalElement element) {
		final StringBuilder sb = new StringBuilder(kind(element));
		if (element instanceof ILabeledElement) {
			try {
				sb.append(' ').append(((ILabeledElement) element).getLabel());
			} catch (Exception e) {
				sb.append(' ').append(element.getElementName());
			}
		}
		final String formula = formula(element);
		if (formula != null)
			sb.append(": ").append(
					formula.length() > MAX_FORMULA ? formula.substring(0,
							MAX_FORMULA) + "..." : formula);
		return sb.toString();
	}

	private static String kind(final IInternalElement element) {
		if (element instanceof ISCGuard)
			return isTheorem(element) ? "guard theorem" : "guard";
		if (element instanceof ISCAction)
			return "action";
		if (element instanceof ISCWitness)
			return "witness";
		if (element instanceof ISCInvariant)
			return isTheorem(element) ? "theorem" : "invariant";
		if (element instanceof ISCAxiom)
			return isTheorem(element) ? "theorem" : "axiom";
		if (element instanceof ISCVariant)
			return "variant";
		if (element instanceof ISCEvent)
			return "event";
		return element.getElementType().getName();
	}

	private static boolean isTheorem(final IInternalElement element) {
		try {
			return element.hasAttribute(EventBAttributes.THEOREM_ATTRIBUTE)
					&& element
							.getAttributeValue(EventBAttributes.THEOREM_ATTRIBUTE);
		} catch (Exception e) {
			return false;
		}
	}

	/** the formula as text, or null */
	private static String formula(final IInternalElement element) {
		for (IAttributeType.String type : new IAttributeType.String[] {
				EventBAttributes.PREDICATE_ATTRIBUTE,
				EventBAttributes.EXPRESSION_ATTRIBUTE,
				EventBAttributes.ASSIGNMENT_ATTRIBUTE }) {
			try {
				if (element.hasAttribute(type))
					return element.getAttributeValue(type);
			} catch (Exception e) {
				// try the next one
			}
		}
		return null;
	}

	/**
	 * @return a description of an unexpected exception for the user: its
	 *         type and message, those of its causes, and where in the
	 *         translator it occurred (e.g. for a bug report); for exceptions
	 *         without a message, such as a NullPointerException
	 */
	public static String describe(final Throwable exception) {
		final StringBuilder sb = new StringBuilder();
		Throwable t = exception;
		for (int depth = 0; t != null && depth < 5; depth++) {
			if (depth > 0)
				sb.append("\nCaused by: ");
			sb.append(t.getClass().getSimpleName());
			if (t.getLocalizedMessage() != null)
				sb.append(": ").append(t.getLocalizedMessage());
			if (t.getCause() == t)
				break;
			t = t.getCause();
		}
		final StackTraceElement frame = codeLocation(exception);
		if (frame != null)
			sb.append("\nIn the code: ").append(frame);
		return sb.toString();
	}

	/** the first frame in ProB's code, else the first one */
	private static StackTraceElement codeLocation(final Throwable exception) {
		final StackTraceElement[] trace = exception.getStackTrace();
		for (StackTraceElement frame : trace)
			if (frame.getClassName().startsWith("de.prob."))
				return frame;
		return trace.length > 0 ? trace[0] : null;
	}
}
