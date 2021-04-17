package net.merayen.elastic.backend.mix

import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice
import net.merayen.elastic.backend.mix.datatypes.Audio
import net.merayen.elastic.backend.util.SoundTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class MixerTest {
	@Test
	@Disabled("It doesn't check anything!")
	fun testSendingAudio() {
		val mixer = Mixer()

		val output_device = getFirstOutputDevice(mixer)

		mixer.reconfigure(44100, 2, 16)

		for (i in 0..9) {
			if (i % 2 == 0) {
				mixer.send(output_device, Audio(arrayOf(SoundTest.makeSound(44100 * 2, 0.5f, floatArrayOf(1000f), 1f), SoundTest.makeSound(44100, 1f, floatArrayOf(2000f), 1f)//,
				)//SoundTest.makeSound(44100, 2f, new float[]{5008}, 1f),
					//SoundTest.makeSound(44100, 2f, new float[]{2010}, 1f)
				))
			} else {
				mixer.send(output_device, Audio(arrayOf(SoundTest.makeSound(44100, 1f, floatArrayOf(1000f), 1f))//SoundTest.makeSound(44100, 1f, new float[]{2000}, 1f)//,
					//SoundTest.makeSound(44100, 2f, new float[]{5008}, 1f),
					//SoundTest.makeSound(44100, 2f, new float[]{2010}, 1f)
				))
			}

			mixer.dispatch(44100)

			for (device in mixer.openDevices) {
				while (device.available() > 0) {
					synchronized(this) {
						try {
							Thread.sleep(1)
						} catch (ignored: InterruptedException) {
						}
					}
				}
			}
		}
		mixer.end()
	}

	@Test
	@Disabled("It doesn't check anything!")
	fun testSynchronization() {
		val mixer = Mixer()

		val output_device = getFirstOutputDevice(mixer)

		mixer.reconfigure(44100, 2, 16)

		val BUFFER_SIZE = 1024

		lateinit var sync: Synchronization

		sync = Synchronization(mixer, object : Synchronization.Handler {

			private var last = System.currentTimeMillis()
			private val start = System.currentTimeMillis()
			private val audio = SoundTest.makeSound(44100, 15f, floatArrayOf(1000f), 1f)
			private val output = FloatArray(BUFFER_SIZE)
			private var position: Int = 0

			internal var asked: Int = 0

			override fun needData() {
				asked++
				if (asked % 10 == 0)
				;//System.out.printf("Frame duration: %d ms, total duration: %d ms\n", System.currentTimeMillis() - last, System.currentTimeMillis() - start);
				last = System.currentTimeMillis()

				for (i in 0 until BUFFER_SIZE)
					output[i] = audio[position++]

				mixer.send(output_device, Audio(arrayOf(output, output)))

				mixer.dispatch(BUFFER_SIZE)

				sync.push(44100, BUFFER_SIZE)
				//if(asked % 5 != 0) sync.push();
			}

			override fun behind() {
				//System.out.println("Lagging behind");
				//sync.push();
			}
		})

		sync.start()

		val t = System.currentTimeMillis() + 5000
		while (t > System.currentTimeMillis()) {
			try {
				Thread.sleep(500)
			} catch (e: Exception) {
				e.printStackTrace()
			}

		}

		mixer.end()
		sync.end()
	}

	private fun getFirstOutputDevice(mixer: Mixer): String {
		for (ad in mixer.availableDevices) { // Test the first output device
			if (ad is AudioOutputDevice) {
				return ad.id
			}
		}

		throw RuntimeException("No output audio device found")
	}
}