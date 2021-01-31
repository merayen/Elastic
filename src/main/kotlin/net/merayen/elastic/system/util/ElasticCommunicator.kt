package net.merayen.elastic.system.util

import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.ElasticSystem
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster
import java.io.Closeable

/**
 * Utility class to communicate with Elastic.
 * Also takes care of running its update()-method to have it process.
 */
class ElasticCommunicator(private val system: ElasticSystem) : Closeable {
	private val messagesFromBackend = Postmaster<ElasticMessage>()
	private val messagesFromUI = Postmaster<ElasticMessage>()

	val netlist = NetList()

	private val messagesFromBackendTap = system.listenToMessagesFromBackend {
		NetListMessages.apply(netlist, it)
		messagesFromBackend.send(it)
	}

	private val messagesFromUITap = system.listenToMessagesFromUI {
		messagesFromBackend.send(it)
	}

	fun send(message: ElasticMessage) = system.send(message)

	/**
	 * Only for testing.
	 */
	fun waitForBackendMessage(func: (message: ElasticMessage) -> Boolean) {
		while (true) {
			system.update(0)
			Thread.sleep(1)
			val message = messagesFromBackend.receive()
			if (message != null)
				if (func(message))
					return
		}
	}

	/**
	 * Only for testing.
	 */
	fun waitForUIMessage(func: (message: ElasticMessage) -> Boolean) {
		while (true) {
			system.update(0)
			Thread.sleep(1)
			val message = messagesFromBackend.receive()
			if (message != null)
				if (func(message))
					return
		}
	}

	/**
	 * Only for testing.
	 */
	fun waitFor(func: () -> Boolean) {
		while (!func()) {
			system.update(0)
			Thread.sleep(1)
		}
	}

	override fun close() {
		messagesFromBackendTap.close()
		messagesFromUITap.close()
	}
}