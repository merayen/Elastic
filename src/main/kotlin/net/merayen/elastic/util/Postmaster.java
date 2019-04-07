package net.merayen.elastic.util;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * Queues events from the backend to the UI.
 * Meant to make the bridge between backend and UI asynch.
 * 
 * We support timeouts, which is meant to make sure that data doesn't jam
 * if the receiving parties hangs or similar.
 */
public class Postmaster {
	public static abstract class Message {}

	private final ArrayDeque<Message> queue = new ArrayDeque<>();

	public void send(Message message) {
		synchronized (queue) {
			queue.add(message);
		}
	}

	public void send(Collection<Message> messages) {
		synchronized (queue) {
			queue.addAll(messages);
		}
	}

	public Message receive() {
		synchronized (queue) {
			return queue.poll();
		}
	}

	public Message[] receiveAll() {
		Message[] result;

		synchronized (queue) {
			result = queue.toArray(new Message[queue.size()]);
			queue.clear();
		}

		return result;
	}

	public boolean isEmpty() {
		synchronized (queue) {
			return queue.isEmpty();
		}
	}

	public void clear() {
		synchronized (queue) {
			queue.clear();
		}
	}

	public int size() {
		return queue.size();
	}
}
