package net.merayen.elastic.util.tap

import java.io.Closeable
import java.util.*

class ObjectDistributor<T> : Closeable {
	private val taps = ArrayList<Tap<T>>()

	/**
	 * Retrieve a new tap that listens to all new messages.
	 * Remember to call close() on the Tap when you are finished receiving messages.
	 * @return Tap
	 */
	fun createTap(func: (item: T) -> Unit): Tap<T> {
		val tap = Tap(this, func)

		synchronized(taps) {
			taps.add(tap)
		}

		return tap
	}

	fun push(item: T) {
		synchronized(taps) {
			for (tap in taps)
				tap.func(item)
		}
	}

	fun remove(tap: Tap<T>) {
		synchronized(taps) {
			taps.add(tap)
		}
	}

	override fun close() {
		synchronized(taps) {
			taps.clear()
		}
	}
}
