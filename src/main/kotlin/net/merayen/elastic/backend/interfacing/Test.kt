package net.merayen.elastic.backend.interfacing

import net.merayen.elastic.backend.interfacing.devicetypes.AudioInputDevice
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice
import net.merayen.elastic.backend.util.SoundTest

object Test {
	fun test() {
		//testOutput();
		testNoe()
		testForwarding()
	}

	private fun testOutput() {
		val ads = Platform.getPlatformScanner(object : AbstractDeviceScanner.Handler {

			override fun onDeviceAdded(device: AbstractDevice) {
				//System.out.println("Device " + device.id + " has been detected");
			}

			override fun onDeviceRemoved(device: AbstractDevice) {
				//System.out.println("Device " + device.id + " has been removed");
			}
		})

		for (ad in ads.devices) {
			if (ad is AudioOutputDevice) {
				println("OUTPUT: " + ad.id)

				//for(AbstractDevice.Configuration c : ad.getAvailableConfigurations())
				//	System.out.println(((AudioDevice.Configuration)c).getDescription());*/

				ad.configure(44100, 2, 16)
				ad.begin()

				ad.write(SoundTest.makeSound(44100, 1f, floatArrayOf(1000f, 1003f), 0.1f))

				ad.stop()

				return

			} else if (ad is AudioInputDevice) {
				println("INPUT: " + ad.id)

				//for(AbstractDevice.Configuration c : ad.getAvailableConfigurations())
				//	System.out.println(((AudioDevice.Configuration)c).getDescription());
			}
		}
	}

	private fun testForwarding() {
		val ads = Platform.getPlatformScanner(object : AbstractDeviceScanner.Handler {
			override fun onDeviceAdded(device: AbstractDevice) {}

			override fun onDeviceRemoved(device: AbstractDevice) {}
		})

		var aod: AudioOutputDevice? = null
		for (ad in ads.devices) {
			if (ad is AudioOutputDevice) {
				aod = ad
				break
			}
		}

		var m = 0
		var aid: AudioInputDevice? = null
		for (ad in ads.devices) {
			if (ad is AudioInputDevice) {
				aid = ad
				if (m++ == 1)
					break
			}
		}

		println("TEST: " + (1 and 0x7F))

		aod!!.configure(48000, 1, 16)
		aod.begin()

		println("OUTPUT data available now: " + aod.available())

		/*int division = 100;

		long t = System.currentTimeMillis();
		float[] silence = new float[48000 / (division * 2)];
		while(silence.length > 0) {
			for(int i = 0; i < division; i++) {
				aod.write(SoundTest.makeSound(48000, 1f / (division * 2), new float[]{1000}, 0.1f));
				aod.write(silence);
			}
			System.out.println((System.currentTimeMillis() - t) % 1000);
		}*/

		aid!!.configure(48000, 1, 16)
		aid.begin()

		println("INPUT data available now: " + aid.available())

		//byte[] silence = new byte[]{-50,50,50,50,50,-50,-50,-50,-50,-50,50,50,50,50,50,-50,-50,-50,-50,-50,50,50,50,50,50,-50,-50,-50,-50,-50,50,50};
		//byte[] silence = new byte[1024];
		val audio = FloatArray(512)
		val directAudio = ByteArray(512 * 2)
		val bal = 0
		var tx = 0
		var rx = 0
		var i: Long = 0
		var dc2 = 0.0
		while (i++ < 100) {
			aid.read(audio)
			rx += audio.size
			//((OracleAudioInputDevice)aid).directRead(directAudio);
			//rx += directAudio.length;
			var min = java.lang.Double.MAX_VALUE
			var max = -java.lang.Double.MAX_VALUE
			var dc = 0.0
			for (f in audio) {
				min = Math.min(f.toDouble(), min)
				max = Math.max(f.toDouble(), max)
				dc += f.toDouble()
				dc2 += f.toDouble()
			}

			vu(-min, max)
			System.out.printf("%.4f\t%.4f\tDC=%.4f, DC2=%.4f\n", min, max, dc, dc2)

			/*int leftover = aid.getBalance();
			if(leftover > 32) {
				System.out.println("Too much leftover data " + leftover);
			}*/

			/*if(aod.available() < 128) {
				//((OracleAudioOutputDevice)aod).directWrite(silence);
				System.out.println("Balancing " + ++bal + " (" + aod.available() + " / " + aid.available() + ")");
				//tx += silence.length;
			}*/

			aod.write(audio)
			tx += audio.size
			//((OracleAudioOutputDevice)aod).directWrite(directAudio);
			//tx += directAudio.length;

			/*try {
				Thread.sleep(1);
			} catch (Exception whatever) {
				break;
			}*/

			/*if(i % 100 == 0) {
				System.out.printf("Rx=%dk, Tx=%dk, balance=%d\n", rx / 1000, tx / 1000, tx - rx);
				rx = tx = 0;
			}*/
		}

		aid.kill()
		aod.kill()
	}

	private fun testNoe() {
		var o = 0
		for (m in 0..255) {
			for (n in 0..255) {
				val b = byteArrayOf(m.toByte(), n.toByte())
				var r: Long = 0
				for (i in b.indices)
					r += b[i].toLong() and 0xFF shl b.size * 8 - i * 8 - 8

				if (o++.toLong() != r)
					throw RuntimeException("nei")
			}
		}
	}

	private fun vu(min: Double, max: Double) {
		val width = 50f
		run {
			var i = 0
			while (i < width) {
				print(if (i / width < 1 - min) " " else "=")
				i++
			}
		}

		print(" | ")

		var i = 0
		while (i < width) {
			print(if (i / width <= max) "=" else " ")
			i++
		}

		print("|")
	}
}
