package net.merayen.elastic.util.tap

import java.io.Closeable

/**
 * A tap that taps into a distributor.
 */
class Tap<T> internal constructor(private val listener: ObjectDistributor<T>, val func: (item: T) -> Unit) : Closeable {
	/**
	 * Ends this tap, meaning that it will not call the user function anymore.
	 */
	override fun close() {
		listener.remove(this)
	}
}
