package de.prob.eventb.translator.flow;

public class EventTuple implements Comparable<EventTuple> {

	private final Event first;
	private final Event second;

	public EventTuple(final Event first, final Event second) {
		this.first = first;
		this.second = second;
	}

	public Event getFirst() {
		return first;
	}

	public Event getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "(" + first.toString() + "," + second.toString() + ")";
	}

	public int compareTo(final EventTuple o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof EventTuple) {
			EventTuple other = (EventTuple) obj;
			return getFirst().equals(other.getFirst())
					&& getSecond().equals(other.getSecond());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return first.hashCode() + 71 * second.hashCode();
	}

}
