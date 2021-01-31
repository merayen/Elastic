package net.merayen.elastic.system

import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.system.intercom.ElasticMessage

/**
 * An action is a group of messages sent into the backend, like initializing a new project, open an existing one etc.
 */
abstract class Action {
	interface Handler {
		/**
		 * Called whenever the Action wants to send messages to the backend.
		 */
		fun onMessage(message: ElasticMessage)

		/**
		 * Called when ElasticSystem can process messages.
		 */
		fun onUpdateSystem()
	}

	var handler: Handler? = null

	abstract fun run()

	/**
	 * Called whenever there are messages from backend
	 */
	abstract fun onMessageFromBackend(message: ElasticMessage)

	protected fun waitFor(func: () -> Boolean) {
		while (!func()) {
			handler?.onUpdateSystem()
			synchronized(this) {
				Thread.sleep(1)
			}
		}
	}

	/**
	 * Send messages to backend.
	 */
	protected fun send(message: ElasticMessage) = handler?.onMessage(message)
}
