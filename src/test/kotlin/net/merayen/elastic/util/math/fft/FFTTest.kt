package net.merayen.elastic.util.math.fft

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.math.roundToInt

internal class FFTTest {
	@Test
	fun `test forward and inverse`() {
		for (i in 1..16) {
			val rexOriginal = DoubleArray(2f.pow(i).roundToInt())
			val imxOriginal = DoubleArray(rexOriginal.size)

			for (i in rexOriginal.indices) {
				rexOriginal[i] = (Math.random() * 1000000).roundToInt() / 1000.0
				imxOriginal[i] = (Math.random() * 1000000).roundToInt() / 1000.0
			}

			val rex = rexOriginal.clone()
			val imx = imxOriginal.clone()

			FFT.fft(rex, imx)

			FFT.ifft(rex, imx)

			// Round away floating point errors
			for (i in rexOriginal.indices) {
				rex[i] = (rex[i] * 1000f).roundToInt() / 1000.0
				imx[i] = (imx[i] * 1000f).roundToInt() / 1000.0
			}

			assertArrayEquals(rexOriginal, rex)
			assertArrayEquals(imxOriginal, imx)
		}
	}

	@Test
	fun `benchmark double precision`() {
		val rex = DoubleArray(2f.pow(16).roundToInt())
		val imx = DoubleArray(rex.size)

		for (i in rex.indices) {
			rex[i] = Math.random()
			imx[i] = Math.random()
		}

		val times = LongArray(1000)
		for (i in times.indices) {
			times[i] -= System.nanoTime()

			FFT.fft(rex, imx)
			FFT.ifft(rex, imx)

			times[i] += System.nanoTime()
		}

		times.sort()

		println("FFT double precision benchmark best time: ${(times[0] / 1000) / 1000.0} ms")
	}

	@Test
	fun `benchmark float precision`() {
		val rex = FloatArray(2f.pow(16).roundToInt())
		val imx = FloatArray(rex.size)

		for (i in rex.indices) {
			rex[i] = Math.random().toFloat()
			imx[i] = Math.random().toFloat()
		}

		val times = LongArray(1000)
		for (i in times.indices) {
			times[i] -= System.nanoTime()

			FFT.fft(rex, imx)
			FFT.ifft(rex, imx)

			times[i] += System.nanoTime()
		}

		times.sort()

		println("FFT float precision benchmark best time: ${(times[0] / 1000) / 1000.0} ms")
	}

	@Test
	fun `benchmark float to double precision and fft`() {
		val rex = FloatArray(2f.pow(16).roundToInt())
		val imx = FloatArray(rex.size)

		for (i in rex.indices) {
			rex[i] = Math.random().toFloat()
			imx[i] = Math.random().toFloat()
		}

		val rexDouble = DoubleArray(rex.size)
		val imxDouble = DoubleArray(rex.size)

		val times = LongArray(1000)
		for (i in times.indices) {
			times[i] -= System.nanoTime()

			for (i in rex.indices) {
				rexDouble[i] = rex[i].toDouble()
				imxDouble[i] = imx[i].toDouble()
			}

			FFT.fft(rexDouble, imxDouble)
			FFT.ifft(rexDouble, imxDouble)

			times[i] += System.nanoTime()
		}

		println("FFT float->double-FFT->double->float benchmark best time: ${(times[0] / 1000) / 1000.0} ms")
	}
}