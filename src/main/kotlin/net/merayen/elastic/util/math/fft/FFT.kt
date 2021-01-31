package net.merayen.elastic.util.math.fft

import kotlin.math.*

/**
 * FFT.
 *
 * Retrieved from the book Digital Signal Processing: A guide for engineers and scientists.
 *
 * Has a float and double precision version.
 */
class FFT {
	companion object {
		/**
		 * Forward FFT using floats. Precision seems to go down quite a lot on huge frames
		 */
		fun fft(rex: FloatArray, imx: FloatArray) {
			if (rex.size != imx.size)
				throw RuntimeException("rex and imx must have the same size")

			if (log2(rex.size.toFloat()) % 1 > 0)
				throw RuntimeException("Input must be 2**X")

			val nm1 = rex.size - 1
			val nd2 = rex.size / 2
			val m = log2(rex.size.toFloat()).roundToInt()

			var j = nd2

			// Bit reversal
			for (i in 1..rex.size - 2) {
				if (i < j) {
					val tr = rex[j]
					val ti = imx[j]
					rex[j] = rex[i]
					imx[j] = imx[i]
					rex[i] = tr
					imx[i] = ti
				}

				var k = nd2

				while (k <= j) {
					j -= k
					k /= 2
				}

				j += k
			}

			for (l in 1..m) { // Each stage
				val le = 2f.pow(l).roundToInt()
				val le2 = le / 2

				var ur = 1f
				var ui = 0f

				val sr = cos(PI/le2).toFloat()
				val si = -sin(PI/le2).toFloat()
				var tr = 0f
				var ti = 0f

				for (j in 1..le2) { // Each sub DFT
					val jm1 = j - 1

					for (i in jm1..nm1 step le) { // Each butterfly
						val ip = i + le2
						tr = rex[ip] * ur - imx[ip] * ui
						ti = rex[ip] * ui + imx[ip] * ur

						rex[ip] = rex[i] - tr
						imx[ip] = imx[i] - ti
						rex[i] += tr
						imx[i] += ti
					}
					tr = ur
					ur = tr * sr - ui * si
					ui = tr * si + ui * sr
				}
			}
		}

		/**
		 * Forward FFT. Higher precision than the floating point version of it.
		 */
		fun fft(rex: DoubleArray, imx: DoubleArray) {
			if (rex.size != imx.size)
				throw RuntimeException("rex and imx must have the same size")

			if (log2(rex.size.toFloat()) % 1 > 0)
				throw RuntimeException("Input must be 2**X")

			val nm1 = rex.size - 1
			val nd2 = rex.size / 2
			val m = log2(rex.size.toFloat()).roundToInt()

			var j = nd2

			// Bit reversal
			for (i in 1..rex.size - 2) {
				if (i < j) {
					val tr = rex[j]
					val ti = imx[j]
					rex[j] = rex[i]
					imx[j] = imx[i]
					rex[i] = tr
					imx[i] = ti
				}

				var k = nd2

				while (k <= j) {
					j -= k
					k /= 2
				}

				j += k
			}

			for (l in 1..m) { // Each stage
				val le = 2f.pow(l).roundToInt()
				val le2 = le / 2

				var ur = 1.0
				var ui = 0.0

				val sr = cos(PI/le2)
				val si = -sin(PI/le2)
				var tr = 0.0
				var ti = 0.0

				for (j in 1..le2) { // Each sub DFT
					val jm1 = j - 1

					for (i in jm1..nm1 step le) { // Each butterfly
						val ip = i + le2
						tr = rex[ip] * ur - imx[ip] * ui
						ti = rex[ip] * ui + imx[ip] * ur

						rex[ip] = rex[i] - tr
						imx[ip] = imx[i] - ti
						rex[i] += tr
						imx[i] += ti
					}
					tr = ur
					ur = tr * sr - ui * si
					ui = tr * si + ui * sr
				}
			}
		}

		fun ifft(rex: FloatArray, imx: FloatArray) {
			for (k in rex.indices)
				imx[k] = -imx[k]

			fft(rex, imx)

			for (i in rex.indices) {
				rex[i] = rex[i] / rex.size
				imx[i] = -imx[i] / rex.size
			}
		}

		fun ifft(rex: DoubleArray, imx: DoubleArray) {
			for (k in rex.indices)
				imx[k] = -imx[k]

			fft(rex, imx)

			for (i in rex.indices) {
				rex[i] = rex[i] / rex.size
				imx[i] = -imx[i] / rex.size
			}
		}
	}
}