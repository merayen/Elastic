package net.merayen.elastic.util

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

	val isEmpty: Boolean
		get() = synchronized(queue) {
			return queue.isEmpty()
		}

	fun send(message: T) {
		synchronized(queue) {
			queue.add(message)
		}
	}

	fun send(messages: Collection<T>) {
		synchronized(queue) {
			queue.addAll(messages)
		}
	}

	fun send(messages: Array<T>) {
		synchronized(queue) {
			queue.addAll(messages)
		}
	}

	fun receive(): T? {
		synchronized(queue) {
			return queue.poll()
		}
	}

	fun receiveAll(): Collection<T> {
		val result = ArrayList<T>()

		synchronized(queue) {
			while (queue.isNotEmpty())
				result.add(queue.removeFirst())
		}

		return result
	}

	fun clear() {
		synchronized(queue) {
			queue.clear()
		}
	}

	fun size(): Int {
		synchronized(queue) {
			return queue.size
		}
	}
}
