package net.merayen.elastic.backend.interfacing.platforms.oracle_java

import net.merayen.elastic.backend.interfacing.AbstractDevice
import net.merayen.elastic.backend.interfacing.AbstractDeviceScanner
import net.merayen.elastic.backend.logicnodes.list.output_1.LogicNode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.math.PI
import kotlin.math.pow
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

		outputDevice.configure(44100, 1, 16)

		outputDevice.begin()

		var pos = 0
		//while(true) {
			for (chunk in 1 until 5) {
				val audio = FloatArray(2.0.pow(8 + 4 - chunk).toInt())
				println("Audio should sound good with no hick ups, buffer: ${audio.size}")

				//for (x in 0 until 50*chunk) {
				val t = System.currentTimeMillis() + 2000
				while (t > System.currentTimeMillis()) {
					for (i in 0 until audio.size)
						audio[i] = sin(pos++ / 44100.0 * 1000 * 2 * PI).toFloat() * 0.1f

					outputDevice.write(audio)
				}
			}
		//}

		outputDevice.stop()
	}
}