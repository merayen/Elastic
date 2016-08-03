package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.util.Postmaster;

public abstract class AbstractExecutor {
	final Postmaster from_processing = new Postmaster(); // Messages sent from this executor
	final Postmaster to_processing = new Postmaster(); // Messages queued to be read from the processing architecture

	protected abstract void onMessage(Postmaster.Message message);

	/**
	 * Call this to stop the processing.
	 * It is not possible to resume processing atm. You need to compile again.
	 */
	public abstract void stop();

	/**
	 * Gets called very endlessly.
	 * You need to call Thread.sleep(...)
	 */
	public abstract void update();

	/**
	 * Retrieves any messages sent from the processor.
	 * Needs to be polled often.
	 * Returns null if nothing.
	 */
	Postmaster.Message receiveFromProcessor() {
		return from_processing.receive();
	}
}
