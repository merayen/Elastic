package net.merayen.elastic.util

import java.util.ArrayDeque

/**
 * Queues events from the backend to the UI.
 * Meant to make the bridge between backend and UI asynch.
 *
 * We support timeouts, which is meant to make sure that data doesn't jam
 * if the receiving parties hangs or similar.
 */
class Postmaster {

	private val queue = ArrayDeque<Any>()

	val isEmpty: Boolean
		get() = synchronized(queue) {
			return queue.isEmpty()
		}

	fun send(message: Any) {
		synchronized(queue) {
			queue.add(message)
		}
	}

	fun send(messages: Collection<Any>) {
		synchronized(queue) {
			queue.addAll(messages)
		}
	}

	fun receive(): Any? {
		synchronized(queue) {
			return queue.poll()
		}
	}

	fun receiveAll(): Array<Any> {
		val result: Array<Any>

		synchronized(queue) {
			result = queue.toTypedArray()
			queue.clear()
		}

		return result
	}

	fun clear() {
		synchronized(queue) {
			queue.clear()
		}
	}

	fun size(): Int {
		return queue.size
	}
}
