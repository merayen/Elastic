package net.merayen.elastic.backend.interfacing.platforms.oracle_java

import net.merayen.elastic.backend.interfacing.AbstractDevice
import net.merayen.elastic.backend.interfacing.AbstractDeviceScanner
import net.merayen.elastic.backend.logicnodes.list.output_1.LogicNode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

class OracleAudioOutputDeviceTest {
	private val devices = ArrayList<AbstractDevice>()
	private var outputDevice: OracleAudioOutputDevice? = null

	@BeforeEach
	fun setUp() {
		devices.clear()
		val deviceScanner = DeviceScanner(object : AbstractDeviceScanner.Handler {
			override fun onDeviceAdded(device: AbstractDevice) {
				devices.add(device)
			}

			override fun onDeviceRemoved(device: AbstractDevice) {
				throw RuntimeException("Test does not support removing of audio devices while testing")
			}
		})

		outputDevice = assertDoesNotThrow {
			devices.first { it.id in LogicNode.knownOutputDevices }
		} as OracleAudioOutputDevice
	}

	@Test
	fun testOutputFlow() {
		val outputDevice = outputDevice!!

		outputDevice.configure(44100, 2, 16)

		outputDevice.begin()

		var pos = 0
		for (chunk in 4 until 5) {
			val audio = FloatArray(2.0.pow(8 + 4 - chunk).toInt())
			println("Audio should sound good with no hick ups, buffer: ${audio.size}")

			val t = System.currentTimeMillis() + 2000000
			var l = System.currentTimeMillis()
			var samplesSent = 0
			while (t > System.currentTimeMillis()) {
				for (i in audio.indices)
					audio[i] = sin(pos++ / 44100.0 * 1000 * 2 * PI).toFloat() * 0.05f

				if (Math.random() > 0.1)
					Thread.sleep(1)

				outputDevice.write(audio)
				//if (outputDevice.available() == 0)
				//	println("Noes")

				samplesSent += audio.size

				if (l + 1000 < System.currentTimeMillis()) {
					println("Samples sent: $samplesSent/s, ${outputDevice.statistics.describe()}")
					samplesSent = 0
					l = System.currentTimeMillis()
				}
			}
		}

		outputDevice.stop()
	}
}
