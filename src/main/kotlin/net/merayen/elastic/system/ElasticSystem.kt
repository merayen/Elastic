package net.merayen.elastic.system

import net.merayen.elastic.backend.context.BackendContext
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.backend.EndBackendMessage
import net.merayen.elastic.system.intercom.backend.InitBackendMessage
import net.merayen.elastic.system.intercom.ui.EndUIMessage
import net.merayen.elastic.system.intercom.ui.InitUIMessage
import net.merayen.elastic.util.tap.ObjectDistributor
import net.merayen.elastic.util.tap.Tap

/**
 * This class binds together the backend and the UI.
 * This is the top class for everything.
 */
class ElasticSystem {
	@Volatile
	private var ui: UIBridge? = null

	@Volatile
	internal var backend: BackendContext? = null

	private val messagesFromUIDistributor = ObjectDistributor<Array<Any>>()
	private val messagesFromBackendDistributor = ObjectDistributor<Array<Any>>()

	/**
	 * Needs to be called often by main thread.
	 */
	fun update() { // TODO perhaps don't do this, but rather trigger on events
		val backend = backend
		val ui = ui
		if (backend != null) {
			backend.update()
			val messages = backend.message_handler.receiveMessagesFromBackend()
			if (ui != null) {
				backend.message_handler.sendToBackend(ui.retrieveMessagesFromUI())
				ui.sendMessagesToUI(messages)
			}
		}
	}

	fun end() {
		if (ui != null)
			ui!!.end()

		if (backend != null)
			backend!!.end()

		ui = null
		backend = null
	}

	/**
	 * Send message to UI.
	 */
	fun sendMessageToUI(messages: Collection<ElasticMessage>) {
		for (message in messages) {
			if (ui == null && message is InitUIMessage) {
				val uiBridge = UIBridge()
				uiBridge.handler = object : UIBridge.Handler {
					override fun onMessageToBackend(message: ElasticMessage) {
						backend!!.message_handler.sendToBackend(listOf(message))
						messagesFromUIDistributor.push(arrayOf(message))
					}
				}

				ui = uiBridge
			}

			if (ui != null && message is EndUIMessage) {
				ui!!.end()
				ui = null
			}
		}

		if (ui != null)
			ui!!.sendMessagesToUI(messages)
	}

	/**
	 * Only for debugging purposes.
	 * Only to be called outside the ElasticSystem.
	 */
	@Synchronized
	fun sendMessageToBackend(messages: Collection<ElasticMessage>) {
		for (message in messages) {
			if (message is InitBackendMessage) {
				if (backend == null) {
					this.backend = BackendContext(this, message)
				}
			} else if (backend == null) {
				println("Ignoring message as backend is not running: $message")
				return
			}

			if (message is EndBackendMessage) {
				backend?.end()
				backend = null
			}

			backend?.message_handler?.sendToBackend(listOf(message))
		}
	}

	/**
	 * Only for debugging purposes.
	 * Listen to all messages being sent to the UI.
	 */
	fun listenToMessagesFromUI(func: (item: Array<Any>) -> Unit): Tap<Array<Any>> {
		return messagesFromUIDistributor.createTap(func);
	}

	/**
	 * Only for debugging purposes.
	 * Listen to all messages being sent to the backend.
	 */
	fun listenToMessagesFromBackend(func: (item: Array<Any>) -> Unit): Tap<Array<Any>> {
		return messagesFromBackendDistributor.createTap(func)
	}

	fun runAction(action: Action) {
		action.start(this)
	}


}
