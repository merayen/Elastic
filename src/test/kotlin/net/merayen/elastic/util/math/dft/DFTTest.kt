package net.merayen.elastic.util.math.dft


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.round
import kotlin.math.sin

internal class DFTTest {
	@Test
	@Disabled("Will probably not need this in Java space...? Or?")
	fun singleSineWaveWithNoWindow() {
		val dft = DFT(FloatArray(100) { 1f }) // DFT with flat window

		val pi = PI.toFloat()

		for (frequency in 1 until 50) {  // Test all frequencies below nyquist rate
			val result = dft.handle(FloatArray(100) { sin(2 * pi * frequency * it / 100) })

			for (i in result.indices) // Check all poles
				if (i == frequency)
					Assertions.assertEquals(100000f, round(result[i] * 100000)) // Should spike here
				else
					Assertions.assertEquals(0f, round(result[i] * 100000)) // Should be flat here
		}
	}
}