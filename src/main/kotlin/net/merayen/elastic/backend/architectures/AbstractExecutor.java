package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.Temporary;
import net.merayen.elastic.system.intercom.ElasticMessage;

public abstract class AbstractExecutor {
	public interface Handler {
		/**
		 * Called when processor sends a message.
		 * Do not do any time consuming tasks in this callback, rather queue the message and notify someone to react on it.
		 */
		void onMessageFromProcessor(ElasticMessage message);
	}

	protected final int sample_rate, sample_buffer_size;

	public AbstractExecutor() {
		sample_rate = Temporary.sampleRate;
		sample_buffer_size = Temporary.bufferSize;
	}

	private Handler handler;

	protected abstract void onMessage(ElasticMessage message);

	/**
	 * Call this to stop the processing.
	 * It is not possible to resume processing atm. You need to compile again.
	 */
	public abstract void stop();

	/**
	 * Send message to backend
	 */
	protected void sendMessage(ElasticMessage message) {
		handler.onMessageFromProcessor(message);
	}

	void setHandler(Handler handler) {
		if(this.handler != null)
			throw new RuntimeException("Should not happen");

		this.handler = handler;
	}
}
