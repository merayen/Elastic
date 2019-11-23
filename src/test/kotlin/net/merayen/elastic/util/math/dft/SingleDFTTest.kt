package net.merayen.elastic.util.math.dft

import org.junit.jupiter.api.Test

internal class SingleDFTTest {
	@Test
	fun testSingle() {
		val singleDFT = SingleDFT(10, 1, 1f)
		singleDFT.handle(FloatArray(10) { sin() } )
	}
}