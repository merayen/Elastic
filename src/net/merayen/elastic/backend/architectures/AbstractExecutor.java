package net.merayen.elastic.backend.architectures;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.architectures.Dispatch.Message;

public abstract class AbstractExecutor {
	private final List<Message> ingoing = new ArrayList<>(); // From the processor
	private final List<Message> outgoing = new ArrayList<>(); // To the processor

	/**
	 * Call this to stop the processing.
	 * It is not possible to resume processing atm. You need to compile again.
	 */
	public abstract void stop();

	/**
	 * Gets called very endlessly.
	 * You need to call Threda.sleep(...)
	 */
	public abstract void update();

	public synchronized void notifyNode(Message message) {
		outgoing.add(message);
	}

	/**
	 * Called by this application to retrieve any events sent from the processor.
	 * Needs to be polled often.
	 * Returns null if nothing.
	 */
	public synchronized List<Message> consumeIngoing() {
		if(ingoing.size() == 0)
			return null;

		List<Message> result = new ArrayList<>(ingoing);
		ingoing.clear();

		return result;
	}

	/**
	 * Called by the processor to consume events sent from this application.
	 */
	public synchronized List<Message> consumeOutgoing() {
		if(outgoing.size() == 0)
			return null;

		List<Message> result = new ArrayList<>(outgoing);
		outgoing.clear();

		return result;
	}
}
