/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.domainobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

public final class Operation {
	private static final String INTERNAL_NAME_INITIALISE_MACHINE = "$initialise_machine";
	private static final String INTERNAL_NAME_SETUP_CONSTANTS = "$setup_constants";

	public static enum EventType {
		SETUP_CONTEXT("SETUP_CONTEXT"), INITIALISATION("INITIALISATION"), NORMAL_EVENT(
				null);
		private final String displayName;

		private EventType(final String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	public static String getInternalName(final String displayname) {
		if (displayname.equals("SETUP_CONTEXT")) {
			return INTERNAL_NAME_SETUP_CONSTANTS;
		}
		if (displayname.equals("INITIALISATION")) {
			return INTERNAL_NAME_INITIALISE_MACHINE;
		}
		return displayname;
	}

	public static class EventStackElement {
		private final String eventName;
		private final List<String> parameters;

		public EventStackElement(final String eventName,
				final List<String> parameters) {
			this.eventName = eventName;
			this.parameters = parameters;
		}

		public String getEventName() {
			return eventName;
		}

		public List<String> getParameters() {
			return parameters;
		}
	}

	private static final Map<String, EventType> SPECIAL_EVENTS = createSpecialEvents();

	public static final Operation NULL_OPERATION = new Operation("no operation");

	private final EventType eventType;
	private final long id;
	private final String name;
	private final String dest;
	private final String src;
	private final List<PrologTerm> args;
	private final List<String> argsPretty;
	private final List<EventStackElement> eventStack;

	/*
	 * Positions inside the Result Term
	 */
	private final static int ID = 1;
	private final static int NAME = 2;
	private final static int SRC = 3;
	private final static int DST = 4;
	private final static int ARGS = 5;
	private final static int ARGS_PRETTY = 6;
	private final static int INFOS = 7;

	private final int hashCode;

	/**
	 * @param name
	 * @deprecated This Method is for testing only !
	 */
	@Deprecated
	Operation(final String name) {
		EventType type = SPECIAL_EVENTS.get(name);
		this.id = -1;
		this.eventType = type == null ? EventType.NORMAL_EVENT : type;
		this.name = type == null ? name : type.getDisplayName();
		this.dest = null;
		this.src = null;
		this.eventStack = null;
		args = new ArrayList<PrologTerm>(0);
		argsPretty = new ArrayList<String>(0);
		hashCode = initHashCode();
	}

	private static Map<String, EventType> createSpecialEvents() {
		Map<String, EventType> specialEvents = new HashMap<String, EventType>();
		specialEvents.put(INTERNAL_NAME_SETUP_CONSTANTS,
				EventType.SETUP_CONTEXT);
		specialEvents.put(INTERNAL_NAME_INITIALISE_MACHINE,
				EventType.INITIALISATION);
		return Collections.unmodifiableMap(specialEvents);
	}

	public static Operation fromPrologTerm(final PrologTerm rawOpTerm) {
		final CompoundPrologTerm opTerm = (CompoundPrologTerm) rawOpTerm;

		final IntegerPrologTerm pInt = (IntegerPrologTerm) opTerm
				.getArgument(ID);
		final long id = pInt.getValue().longValue();
		final String name = PrologTerm.atomicString(opTerm.getArgument(NAME));
		final EventType type = SPECIAL_EVENTS.get(name);
		final String destId = getIdFromPrologTerm(opTerm.getArgument(DST));
		final String srcId = getIdFromPrologTerm(opTerm.getArgument(SRC));
		final List<PrologTerm> args = (ListPrologTerm) opTerm.getArgument(ARGS);
		final List<String> pargs = create_pretty_arguments((ListPrologTerm) opTerm
				.getArgument(ARGS_PRETTY));
		final List<EventStackElement> eventStack = createEventStack((ListPrologTerm) opTerm
				.getArgument(INFOS));

		return new Operation(id, type, name, destId, srcId, args, pargs,
				eventStack);
	}

	/**
	 * Creates a new Operation from a ProB answer and the Set Elements Mapping.
	 * 
	 * @param term
	 *            The term looks like:
	 *            op(move,2,[fd(3,'Floors')],[],0,[],[],'move(Second)')<br>
	 *            op/8 has the following arguments:<br>
	 *            <ol>
	 *            <li>Is of the transition</li>
	 *            <li>Name</li>
	 *            <li>ID of the source (internal ProB node id)</li>
	 *            <li>ID of the destination (internal ProB node id)</li>
	 *            <li>operation's arguments (as list)</li>
	 *            <li>operation's arguments prettyprinted (as list)</li>
	 *            <li>additional informations</li>
	 *            </ol>
	 * 
	 * @deprecated Use {@link #fromPrologTerm(CompoundPrologTerm)} instead
	 */
	@Deprecated
	public Operation(final CompoundPrologTerm term) {
		final String preName = PrologTerm.atomicString(term.getArgument(NAME));
		final EventType type = SPECIAL_EVENTS.get(preName);
		this.eventType = type == null ? EventType.NORMAL_EVENT : type;
		this.name = type == null ? preName : type.getDisplayName();
		this.dest = getIdFromPrologTerm(term.getArgument(DST));
		this.src = getIdFromPrologTerm(term.getArgument(SRC));
		this.args = (ListPrologTerm) term.getArgument(ARGS);

		this.argsPretty = create_pretty_arguments((ListPrologTerm) term
				.getArgument(ARGS_PRETTY));
		this.hashCode = initHashCode();

		final IntegerPrologTerm pInt = (IntegerPrologTerm) term.getArgument(ID);
		this.id = pInt.getValue().longValue();
		this.eventStack = createEventStack((ListPrologTerm) term
				.getArgument(INFOS));
	}

	private Operation(final long id, final EventType type, final String name,
			final String destId, final String srcId,
			final List<PrologTerm> prologArgs, final List<String> stringArgs,
			final List<EventStackElement> eventStack) {
		this.id = id;
		this.eventType = type;
		this.name = type == null ? name : type.getDisplayName();
		this.dest = destId;
		this.src = srcId;
		this.args = prologArgs;
		this.argsPretty = stringArgs;
		this.eventStack = eventStack;

		this.hashCode = initHashCode();
	}

	private static List<String> create_pretty_arguments(
			final List<PrologTerm> argsPrettyTerm) {
		List<String> argsPretty = new ArrayList<String>(argsPrettyTerm.size());
		for (final PrologTerm ppTerm : argsPrettyTerm) {
			final String ppArg = PrologTerm.atomicString(ppTerm);
			final String niceArg = UnicodeTranslator.toUnicode(ppArg);
			argsPretty.add(niceArg);
		}
		return Collections.unmodifiableList(argsPretty);
	}

	private static List<EventStackElement> createEventStack(
			final ListPrologTerm list) {
		for (PrologTerm term : list) {
			if (term.hasFunctor("event", 1)) {
				ListPrologTerm pstack = (ListPrologTerm) ((CompoundPrologTerm) term)
						.getArgument(1);
				final List<EventStackElement> stack = new ArrayList<EventStackElement>(
						pstack.size());
				for (final PrologTerm elem : pstack) {
					stack.add(extractStackElement(elem));
				}
				return Collections.unmodifiableList(stack);
			}
		}
		return null;
	}

	private static EventStackElement extractStackElement(final PrologTerm elem) {
		if (elem.hasFunctor("event", 2)) {
			CompoundPrologTerm term = (CompoundPrologTerm) elem;
			final String name = PrologTerm.atomicString(term.getArgument(1));
			List<PrologTerm> pparams = (ListPrologTerm) term.getArgument(2);
			List<String> sparams;
			if (pparams.isEmpty()) {
				sparams = Collections.emptyList();
			} else {
				sparams = new ArrayList<String>(pparams.size());
				for (PrologTerm p : pparams) {
					sparams.add(PrologTerm.atomicString(p));
				}
				sparams = Collections.unmodifiableList(sparams);
			}
			return new EventStackElement(name, sparams);
		} else {
			throw new IllegalArgumentException("expected event/2 but was: "
					+ elem);
		}
	}

	private static String getIdFromPrologTerm(final PrologTerm destTerm) {
		if (destTerm instanceof IntegerPrologTerm) {
			return ((IntegerPrologTerm) destTerm).getValue().toString();
		}
		return ((CompoundPrologTerm) destTerm).getFunctor();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDestination() {
		return dest;
	}

	public String getSource() {
		return src;
	}

	public List<String> getArguments() {
		return argsPretty;
	}

	public List<PrologTerm> getRawArguments() {
		return Collections.unmodifiableList(args);
	}

	public EventType getEventType() {
		return eventType;
	}

	public List<EventStackElement> getEventStack() {
		return eventStack;
	}

	@Override
	public String toString() {
		if (argsPretty.isEmpty()) {
			return name;
		} else {
			return name + "(" + String.join(",", argsPretty) + ")";
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (o != NULL_OPERATION && o != null && o instanceof Operation) {
			final Operation op = (Operation) o;
			return id == op.id && op.src.equals(src) && op.dest.equals(dest)
					&& op.name.equals(name) && op.argsPretty.equals(argsPretty);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	private int initHashCode() {
		int result = 527 + (src == null ? 0 : src.hashCode());
		result = 31 * result + (dest == null ? 0 : dest.hashCode());
		result = 31 * result + (name == null ? 0 : name.hashCode());
		for (String arg : argsPretty) {
			result = 31 * result + (arg == null ? 0 : arg.hashCode());
		}
		return result * 31 + ((int) id);
	}

}
