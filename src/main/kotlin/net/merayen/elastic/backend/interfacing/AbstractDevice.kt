package net.merayen.elastic.backend.interfacing

import net.merayen.elastic.util.AverageStat
import kotlin.math.roundToInt

/**
 * Represents a hardware device, like a audio interface, midi keyboard etc.
 */
abstract class AbstractDevice(val id: String // an unique ID for a device. Should be namespaced by the DeviceScanner. This ID can be used by the nodes to identify a device between multiple sessions
							  , val description: String, protected var vendor: String) {

	val statistics = Statistics(id)

	/**
	 * Returns true if the device is connected and working. Returns false if it has been disconnected.
	 * A DeviceDescriptor can not be reused.
	 */
	var isDead: Boolean = false
		private set
	var isRunning: Boolean = false
		private set
	protected var configuration: Configuration? = null

	/**
	 * Returns all the available configurations this device supports
	 */
	abstract val availableConfigurations: List<Configuration>

	abstract val isOutput: Boolean

	interface Configuration

	/**
	 * Holy shit. Need more information on what happens under the hood.
	 */
	class Statistics(val id: String) {
		/**
		 * Time taken to write/read from the buffer.
		 */
		val buffer_time = AverageStat<Float>(100)

		/**
		 * Time outside writing to the buffer.
		 */
		val outside_buffer_time = AverageStat<Float>(100)

		/**
		 * Samples available before reading/writing.
		 */
		val available_before = AverageStat<Int>(100)

		/**
		 * Samples available after reading/writing.
		 */
		val available_after = AverageStat<Int>(100)

		/**
		 * Samples processed on read/write.
		 */
		val samples_processed = AverageStat<Int>(100)

		/**
		 * Every time the buffer is empty upon writing or reading.
		 */
		var hunger: Int = 0

		fun describe(): String {
			val formatter = AverageStat.Formatter { "${(it * 1000).roundToInt()}ms" }

			return String.format("Statistics:\n\tbuffer_time=%s\n\toutside_buffer_time=%s\n\tavailable_before=%s\n\tavailable_after=%s\n\tsamples_processed=%s\n\thunger=%d",
					buffer_time.info(formatter),
					outside_buffer_time.info(formatter),
					available_before.info(),
					available_after.info(),
					samples_processed.info(),
					hunger
			)
		}
	}

	/**
	 * Called when a configuration has been changed.
	 * Device should be re-inited to confirm to the new configuration.
	 */
	abstract fun onReconfigure()

	protected abstract fun onStop()

	protected abstract fun onKill()

	/**
	 * Either reads or writes silence to device. This one is usually called to assert that no lines are starving/overflowing
	 */
	abstract fun spool(samples: Int)

	/**
	 * For inputs:
	 * Return how many samples we do have available for processing. 0 to a positive number
	 *
	 * For outputs:
	 * Return how many samples we have that are waiting to be sent. If it is 0 or a negative number, we might have stuttering
	 */
	abstract fun available(): Int

	/**
	 * Called by the device scanner to indicate that this devices has been disconnected.
	 * A new DeviceDescriptor must be created if the device gets connected/available again!
	 * Should most likely not be called manually, unless testing.
	 */
	fun kill() {
		if (isRunning)
			stop()

		onKill()

		isDead = true
	}

	/**
	 * Makes the device start processing. This should initialize and open whatever needs to be opened.
	 */
	fun begin() {
		if (configuration == null)
			throw RuntimeException("Device must be configured before it can be started")

		if (isDead)
			throw RuntimeException("Device has been closed. Future processing is not possible")

		isRunning = true
	}

	fun stop() {
		onStop()
		isRunning = false
	}
}
