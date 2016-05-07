package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.util.Postmaster;

public abstract class AbstractExecutor {
	//private final List<Postmaster.Message> ingoing = new ArrayList<>(); // From the processor
	//private final List<Postmaster.Message> outgoing = new ArrayList<>(); // To the processor

	private final Postmaster from_processing = new Postmaster(); // Messages sent from this executor

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
	 * Message to your processing backend.
	 */
	public abstract void handleMessage(Postmaster.Message message);

	/**
	 * Retrieves any messages sent from the processor.
	 * Needs to be polled often.
	 * Returns null if nothing.
	 */
	public final Postmaster.Message receiveMessage() {
		return from_processing.receive();
	}
}
