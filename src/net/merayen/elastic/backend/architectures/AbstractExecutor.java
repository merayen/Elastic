package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.system.intercom.backend.InitBackendMessage;
import net.merayen.elastic.util.Postmaster;

public abstract class AbstractExecutor {
	public interface Handler {
		/**
		 * Called when processor sends a message.
		 * Do not do any time consuming tasks in this callback, rather queue the message and notify someone to react on it.
		 */
		public void onMessageFromProcessor(Postmaster.Message message);
	}

	protected final int sample_rate, sample_buffer_size;

	public AbstractExecutor(InitBackendMessage message) {
		sample_rate = message.sample_rate;
		sample_buffer_size = message.buffer_size;
	}

	private Handler handler;

	final Postmaster to_processing = new Postmaster(); // Messages queued to be read from the processing architecture

	protected abstract void onMessage(Postmaster.Message message);

	/**
	 * Call this to stop the processing.
	 * It is not possible to resume processing atm. You need to compile again.
	 */
	public abstract void stop();

	protected void sendFromProcessing(Postmaster.Message message) {
		handler.onMessageFromProcessor(message);
	}

	void setHandler(Handler handler) {
		if(this.handler != null)
			throw new RuntimeException("Should not happen");

		this.handler = handler;
	}
}
