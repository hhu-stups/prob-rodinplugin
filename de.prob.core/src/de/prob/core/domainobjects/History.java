/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.domainobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import de.prob.core.Animator;
import de.prob.core.IComputationListener;
import de.prob.core.command.SetStateCommand;
import de.prob.exceptions.ProBException;

public class History implements Iterable<HistoryItem>, IComputationListener {

	private static final HistoryItem[] EMPTY_ITEM_ARRAY = new HistoryItem[0];

	private final List<HistoryItem> items = new LinkedList<HistoryItem>();
	private int currentPosition = 0;
	private final Collection<HistoryListener> listeners = new ArrayList<HistoryListener>();

	/*
	 * @ public invariant
	 * 
	 * @ 0 <= currentPosition &&
	 * 
	 * @ currentPosition < items.size();
	 * 
	 * @
	 */

	@Override
	public synchronized/* @ pure @ */Iterator<HistoryItem> iterator() {
		// a copy: the caller iterates without holding the lock, while e.g. a
		// background job may add items
		return Collections.unmodifiableList(
				new ArrayList<HistoryItem>(items)).iterator();
	}

	public synchronized void add(final State s, final Operation o) {
		if (items.isEmpty()) {
			items.add(new HistoryItem(s, null));
			currentPosition = 0;
			notifyAboutNewState(s, 0);
		} else {
			HistoryItem oldItem = items.get(currentPosition);
			HistoryItem newItem = new HistoryItem(oldItem.getState(), o);

			for (int i = currentPosition + 1; i < items.size(); i++) {
				notifyAboutRemoval(items.get(i).getState(), i);
			}

			// Delete all following item (including the current one)
			while (items.size() > currentPosition) {
				items.remove(currentPosition);
			}
			items.add(newItem);
			// Create a new last item
			HistoryItem historyItem = new HistoryItem(s, null);
			items.add(historyItem);
			currentPosition++;
			notifyAboutNewState(s, currentPosition);
		}
	}

	private void notifyAboutNewState(final State state, final int position) {
		for (HistoryListener l : listeners) {
			l.stateEntersHistory(state, position);
		}
	}

	private void notifyAboutRemoval(final State state, final int position) {
		for (HistoryListener l : listeners) {
			l.stateLeavesHistory(state, position);
		}
	}

	public synchronized/* @ pure @ */int size() {
		return items.size();
	}

	public synchronized/* @ pure @ */boolean isEmpty() {
		return items.isEmpty();
	}

	/*
	 * @
	 * 
	 * @ requires pos > 0 & pos < size() & !isEmpty()
	 * 
	 * @
	 */
	public void gotoPos(final int pos) throws ProBException {
		final Animator animator = Animator.getAnimator();
		// Lock order must be Animator before History: commands executed by the
		// Animator (e.g. ExecuteOperationCommand) call add() while holding the
		// Animator lock. Taking the locks the other way round can deadlock.
		synchronized (animator) {
			synchronized (this) {
				// @StartAssert
				Assert.isTrue(!isEmpty(), "History is empty");
				Assert.isTrue(0 <= pos,
						"Position must be greater or equal 0, was " + pos);
				Assert.isTrue(pos < size(),
						"Position must be less than history size");
				// @EndAssert
				final int oldPosition = currentPosition;
				currentPosition = pos;
				try {
					SetStateCommand.setState(animator, getCurrent()
							.getStateId());
				} catch (ProBException e) {
					currentPosition = oldPosition;
					throw e;
				}

				animator.announceCurrentStateChanged(getCurrent().getState(),
						Operation.NULL_OPERATION);
			}
		}
	}

	public synchronized HistoryItem getCurrent() {
		return getHistoryItem(0);
	}

	/**
	 * Returns an item of the animator history
	 * 
	 * @param pos
	 *            the position of the item, 0 is the current state, positive
	 *            values refer to next states, negative values refer to previous
	 *            states
	 * @return the history item or <code>null</code> if there is no such element
	 */
	public synchronized HistoryItem getHistoryItem(int pos) {
		pos = currentPosition + pos;
		if (pos >= 0 && pos < items.size())
			return items.get(pos);
		else
			return null;
	}

	/**
	 * Like, {@link #getHistoryItem(int)}, but the state of the history item is
	 * returned.
	 * 
	 * @param pos
	 *            the position of the item, 0 is the current state, positive
	 *            values refer to next states, negative values refer to previous
	 *            states
	 * @return the state or <code>null</code> if there is no such element
	 */
	public synchronized State getState(final int pos) {
		final HistoryItem item = getHistoryItem(pos);
		return item == null ? null : item.getState();
	}

	public void gotoNext() {
		// see gotoPos for the lock order
		synchronized (Animator.getAnimator()) {
			synchronized (this) {
				if (currentPosition < items.size() - 1) {
					currentPosition++;
					announceCurrentState();
				}
			}
		}
	}

	/*
	 * @
	 * 
	 * @ requires currentPosition > 0
	 * 
	 * @
	 */
	public void gotoPrevious() {
		// see gotoPos for the lock order
		synchronized (Animator.getAnimator()) {
			synchronized (this) {
				if (currentPosition > 0) {
					currentPosition--;
					announceCurrentState();
				}
			}
		}
	}

	private void announceCurrentState() {
		HistoryItem item = getCurrent();
		Animator.getAnimator().announceCurrentStateChanged(item.getState(),
				Operation.NULL_OPERATION);
	}

	public synchronized void reset() {
		// notify listeners about removal of elements
		// from last to first state
		for (int i = items.size() - 1; i >= 0; i--) {
			notifyAboutRemoval(items.get(i).getState(), i);
		}
		items.clear();
		currentPosition = 0;
	}

	public synchronized/* @ pure @ */int getCurrentPosition() {
		return currentPosition;
	}

	@Override
	public synchronized void computedState(final State state) {
		if (isEmpty()) {
			add(state, null);
		}
	}

	public synchronized HistoryItem[] getAllItems() {
		return items.toArray(EMPTY_ITEM_ARRAY);
	}

	synchronized public void addListener(final HistoryListener listener) {
		listeners.add(listener);
	}

	synchronized public void removeListener(final HistoryListener listener) {
		listeners.remove(listener);
	}
}
