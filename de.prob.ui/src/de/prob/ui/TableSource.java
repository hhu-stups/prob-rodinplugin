/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.exceptions.ProBException;

/**
 * A table for the ProB Table view that is not one of the animator's, e.g. the
 * disprover's statistics. Contributed with the extension point
 * <code>de.prob.ui.tables</code>: the view offers it in a toolbar menu named
 * after its group.
 */
public interface TableSource {

	/** the contents of a table */
	final class Table {
		private final List<List<String>> rows;
		private final String note;

		/**
		 * @param rows
		 *            the rows; the first one has the column headers
		 * @param note
		 *            shown above the table, e.g. what it is about; may be
		 *            <code>null</code>
		 */
		public Table(final List<List<String>> rows, final String note) {
			this.rows = Collections
					.unmodifiableList(new ArrayList<List<String>>(rows));
			this.note = note;
		}

		public List<List<String>> getRows() {
			return rows;
		}

		public String getNote() {
			return note;
		}
	}

	/** computes the table; called in a background job */
	Table compute() throws ProBException;

	/**
	 * Registers a listener to be called (in any thread) when the table has
	 * changed: the view then computes it again while it is shown.
	 */
	default void addChangeListener(final Runnable listener) {
	}

	default void removeChangeListener(final Runnable listener) {
	}

	/**
	 * @return the id of the contributed table the view shows from then on,
	 *         with the rows computed here; e.g. an entry that resets
	 *         statistics continues with the statistics, so that Refresh does
	 *         not reset them again. <code>null</code> to keep this one.
	 */
	default String getFollowUpTable() {
		return null;
	}
}
