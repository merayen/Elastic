package net.merayen.elastic.system

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.util.Postmaster
import java.io.Closeable

abstract class ElasticModule : Thread(), Closeable {
	interface Handler {
		/**
		 * Called when this module sends outgoing messages.
		 * ElasticSystem's thread will wake up and retrieve the message.
		 */
		fun onWakeUp()
	}

	var handler: Handler? = null

	private val lock = Object()

	private var notified = true

	/**
	 * Messages sent to this module, that are to be read by this module.
	 */
	val ingoing = Postmaster<ElasticMessage>()

	/**
	 * Messages sent from this module, that are to be read by this module.
	 */
	val outgoing = Postmaster<ElasticMessage>()

	@Volatile
	var isRunning = false
		private set

	final override fun run() {
		isRunning = true

		onInit()

		while (isRunning) {
			synchronized(lock) {
				if (!notified)
					lock.wait()

				notified = false
			}

			onUpdate()
		}

		onEnd()
	}

	abstract fun onInit()

	/**
	 * Called every time someone schedules us, due to e.g incoming message.
	 */
	abstract fun onUpdate()

	/**
	 * Called when asked to stop the module.
	 * Clean up any resources/threads in this one.
	 */
	abstract fun onEnd()

	protected fun notifyElasticSystem() = handler!!.onWakeUp()

	/**
	 * Notifies this module that something is ready for it, usually a message.
	 */
	fun schedule() {
		synchronized(lock) {
			notified = true
			lock.notifyAll()
		}
	}

	override fun close() {
		isRunning = false
		join()
	}
}