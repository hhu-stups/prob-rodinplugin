/**
 * 
 */
package de.prob.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This specialized Logger class stores a limited number of log entries in
 * memory. None of its information is written to a file nor console.
 * 
 * Listeners can be registered to be notified when a new log entry comes in. A
 * separate notification thread ensures that not too many messages will be sent
 * by waiting a little bit and collection information.
 * 
 * For each log entry the current time is added to allow performance
 * measurements.
 * 
 * @author plagge
 */
public class LimitedLogger {
	private final static LogEntry[] EMPTY_ARRAY = new LogEntry[0];
	private final static LimitedLogger LOGGER = new LimitedLogger();
	private final static long DELAY = 300;

	public interface LogListener {
		void newLoggingInfo();
	}

	public final static class LogEntry {
		private final long id;
		private final long time;
		private final String category;
		private final Object shortDescriptionObj;
		private final Object longDescriptionObj;
		private String shortDescription;
		private String longDescription;

		public LogEntry(final long id, final long time, final String category,
				final String shortDescription, final String longDescription) {
			this.id = id;
			this.time = time;
			this.category = category;
			this.shortDescription = shortDescription;
			this.longDescription = longDescription;
			this.shortDescriptionObj = null;
			this.longDescriptionObj = null;
		}

		public LogEntry(final long id, final long time, final String category,
				final Object shortDescription, final Object longDescription) {
			this.id = id;
			this.time = time;
			this.category = category;
			this.shortDescriptionObj = shortDescription;
			this.longDescriptionObj = longDescription;
		}

		public long getId() {
			return id;
		}

		public long getTime() {
			return time;
		}

		public String getCategory() {
			return category;
		}

		public String getShortDescription() {
			if (shortDescription == null && shortDescriptionObj != null) {
				shortDescription = shortDescriptionObj.toString();
			}
			return shortDescription;
		}

		public String getLongDescription() {
			if (longDescription == null && longDescriptionObj != null) {
				longDescription = longDescriptionObj.toString();
			}
			return longDescription;
		}
	}

	private final Object newEventsNotification = new Object();

	private int limit = 200;
	private long currentId = 0;
	private final LinkedList<LogEntry> entries = new LinkedList<LogEntry>();
	private final Collection<LogListener> listener = new HashSet<LogListener>();
	private NotificationThread notificationThread = null;
	private Long firstTime;

	public synchronized void log(final String category,
			final Object shortDescription, final Object longDescription) {
		final long now = System.currentTimeMillis();
		final LogEntry entry = new LogEntry(currentId, now, category,
				shortDescription, longDescription);
		log(now, entry);
	}

	public synchronized void log(final String category,
			final String shortDescription, final String longDescription) {
		final long now = System.currentTimeMillis();
		final LogEntry entry = new LogEntry(currentId, now, category,
				shortDescription, longDescription);
		log(now, entry);
	}

	private void log(final long now, final LogEntry entry) {
		if (entries.size() >= limit) {
			entries.removeFirst();
		}
		if (firstTime == null) {
			firstTime = now;
		}
		entries.addLast(entry);
		currentId++;
		synchronized (newEventsNotification) {
			newEventsNotification.notifyAll();
		}
	}

	public synchronized void setLimit(final int limit) {
		this.limit = limit;
		while (entries.size() > limit) {
			entries.removeFirst();
		}
	}

	public synchronized LogEntry[] getEntries() {
		return entries.toArray(EMPTY_ARRAY);
	}

	public static LimitedLogger getLogger() {
		return LOGGER;
	}

	public void registerListener(final LogListener listener) {
		synchronized (listener) {
			this.listener.add(listener);
			if (notificationThread == null) {
				notificationThread = new NotificationThread(this);
				notificationThread.setDaemon(true);
				notificationThread.start();
			}
		}
	}

	public void unregisterListener(final LogListener listener) {
		synchronized (listener) {
			this.listener.remove(listener);
			if (this.listener.isEmpty()) {
				notificationThread.stopNotification();
				notificationThread = null;
			}
		}
	}

	/**
	 * Get the time of the first log entry
	 * 
	 * @return the time in milliseconds since epoch, <code>null</code> if there
	 *         is no entry yet.
	 */
	public synchronized Long getFirstLoggingTime() {
		return firstTime;
	}

	/**
	 * Clear all entries
	 */
	public synchronized void clear() {
		this.entries.clear();
	}

	private static class NotificationThread extends Thread {
		private final LimitedLogger logger;
		private final static long lastNotification = 0;
		private boolean stopped = false;

		public NotificationThread(final LimitedLogger logger) {
			super("LimitedLoggerNotification");
			this.logger = logger;
		}

		public void stopNotification() {
			this.stopped = true;
			this.interrupt();
		}

		@Override
		public void run() {
			while (!stopped) {
				try {
					// wait for the next new event
					synchronized (logger.newEventsNotification) {
						if (!stopped) {
							logger.newEventsNotification.wait();
						}
					}
					// then wait a little bit to prevent too many notifications
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// ignore
				}
				notifyIfNewData();
			}
		}

		private void notifyIfNewData() {
			final long current;
			synchronized (logger) {
				current = logger.currentId;
			}
			if (lastNotification < current) {
				synchronized (logger.listener) {
					if (!stopped) {
						for (LogListener l : logger.listener) {
							l.newLoggingInfo();
						}
					}
				}
			}
		}
	}
}
