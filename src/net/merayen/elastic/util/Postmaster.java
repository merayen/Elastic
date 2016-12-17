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
	public static abstract class Message {
		public final long timeout;

		public Message(long timeout) {
			this.timeout = System.currentTimeMillis() + timeout;
		}

		public Message() {
			this.timeout = Long.MAX_VALUE;
		}
	}

	private final ArrayDeque<Message> queue = new ArrayDeque<>();

	public void send(Message message) {
		if(System.currentTimeMillis() % 10 == 0)
			clean();

		if(queue.size() >= 1000)
			System.out.println("Postmaster is scarily flooded");

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
		clean(); // Bad, if many messages in queue?

		synchronized (queue) {
			return queue.poll();
		}
	}

	public Message[] receiveAll() {
		clean();

		Message[] result;

		synchronized (queue) {
			result = queue.toArray(new Message[0]);
			queue.clear();
		}

		return result;
	}

	public void clear() {
		synchronized (queue) {
			queue.clear();
		}
	}

	private void clean() {
		return;
		/*synchronized (queue) {
			long t = System.currentTimeMillis();
			for(Message o : queue)
				if(o.timeout < t)
					queue.remove(o);
		}*/
	}
}
