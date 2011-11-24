/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.prob.core.ITrace;
import de.prob.core.internal.Message.Type;

/**
 * TODO: Maybe a part of the trace should be dumped to a file, to prevent a high
 * memory consumption.
 */
public class Trace implements ITrace {

	private final LinkedList<Message> list = new LinkedList<Message>();
	private Integer maximum;

	public Trace() {
		this(null);
	}

	public Trace(final int maximum) {
		this(Integer.valueOf(maximum));
	}

	private Trace(final Integer maximum) {
		this.maximum = maximum;
	}

	/**
	 * @return the List of logged messages as unmodifiable List of Strings
	 */
	public final List<Message> getTraceAsList() {
		return Collections.unmodifiableList(list);
	}

	/**
	 * @return the List of logged messages as a String
	 */
	public final String getTraceAsString() {
		StringBuffer buffer = new StringBuffer();

		for (Message message : list) {
			buffer.append(starterString(message.getType()));
			buffer.append(message.getMessage());
			buffer.append(stopperString(message.getType()));
		}
		return buffer.toString();
	}

	public final void addMessage(final Type type, final String message) {
		list.add(new Message(type, message));
		limit();
	}

	public final void addMessageOnTop(final Type type, final String message) {
		list.add(1, new Message(type, message));
		limit();
	}

	private final String starterString(final Type type) {
		return "@START " + type + "\n";
	}

	private final String stopperString(final Type type) {
		return "\n@END " + type + "\n";
	}

	public static final ITrace traceFromStream(final InputStream stream) {
		final Trace trace = new Trace();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			iterateOverLines(trace, reader);
		} catch (IOException e) {
			System.out.println(trace.getClass().getName()
					+ ": IOException at Filehandling");
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// IGNORE
				}
			}
		}
		return trace;
	}

	private static void iterateOverLines(final Trace trace,
			final BufferedReader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();

		String line = reader.readLine();
		while (null != line) {
			final boolean restartBuffer = collect(line, trace, buffer);
			if (restartBuffer) {
				buffer = new StringBuffer();
			}
			line = reader.readLine();
		}
	}

	private static final boolean collect(final String line, final Trace trace,
			final StringBuffer buffer) {
		final boolean restartBuffer;
		if (line.startsWith("@START")) {
			restartBuffer = true;
		} else {
			restartBuffer = false;
			Type type;
			if (line.startsWith("@END ")) {
				final String typeString = line.substring(5);
				try {
					type = Type.valueOf(typeString);
				} catch (IllegalArgumentException e) {
					// the string does not represent one of our types
					type = null;
				}
			} else {
				type = null;
			}
			if (type != null) {
				trace.addMessage(type, buffer.toString());
			} else {
				buffer.append(line);
			}
		}
		return restartBuffer;
	}

	public int size() {
		return list == null ? 0 : list.size();
	}

	@Override
	public void setMaximum(final Integer max) {
		if (max != null && max < 0)
			throw new IllegalArgumentException("Non-negative maximum expected");
		maximum = max;
		limit();
	}

	private void limit() {
		if (maximum != null && list != null) {
			final int toRemove = list.size() - maximum;
			for (int i = 0; i < toRemove; i++) {
				list.removeFirst();
			}
		}
	}

	@Override
	public Integer getMaximum() {
		return maximum;
	}

}
