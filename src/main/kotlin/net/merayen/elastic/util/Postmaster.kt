package net.merayen.elastic.util

import net.merayen.elastic.system.intercom.BackendReadyMessage
import java.util.*

/**
 * Queues events from the backend to the UI.
 * Meant to make the bridge between backend and UI asynch.
 *
 * We support timeouts, which is meant to make sure that data doesn't jam
 * if the receiving parties hangs or similar.
 */
class Postmaster<T> {
	private val queue = ArrayDeque<T>()

	@Synchronized
	fun isEmpty() = queue.isEmpty()

	@Synchronized
	fun send(message: T) {
		queue.add(message)
		if (queue.isEmpty())
			throw RuntimeException("Should not happen")
	}

	@Synchronized
	fun send(messages: Collection<T>) {
		queue.addAll(messages)
	}

	@Synchronized
	fun send(messages: Array<T>) {
		queue.addAll(messages)
	}

	@Synchronized
	fun receive(): T? {
		val message = queue.poll()
		if (message is BackendReadyMessage)
			System.currentTimeMillis()
		return message
	}

	@Synchronized
	fun receiveAll(): Collection<T> {
		val result = ArrayList<T>()

		while (queue.isNotEmpty())
			result.add(queue.removeFirst())

		for (message in result)
			if (message is BackendReadyMessage)
				System.currentTimeMillis()

		return result
	}

	@Synchronized
	fun clear() {
		queue.clear()
	}

	@Synchronized
	fun size() = queue.size
}
