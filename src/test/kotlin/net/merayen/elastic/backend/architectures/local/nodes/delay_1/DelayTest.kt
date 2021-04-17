package net.merayen.elastic.backend.architectures.local.nodes.delay_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class DelayTest {
	@Test
	@Disabled("Java implementation of delay will be removed")
	fun testDelay() {
		val delay = Delay(10 + 2)
		delay.addTap(Delay.Tap(0, 1f, 0f))
		delay.addTap(Delay.Tap(2, 1f, 0f))

		val input = (0 until 10).map { it.toFloat() }.toFloatArray()

		assertEquals(0, delay.process(input, 0, 10))

		val fasit = listOf(
			0f,
			1f,
			(2 + 0).toFloat(),
			(3 + 1).toFloat(),
			(4 + 2).toFloat(),
			(5 + 3).toFloat(),
			(6 + 4).toFloat(),
			(7 + 5).toFloat(),
			(8 + 6).toFloat(),
			(9 + 7).toFloat(),
			(10 + 8).toFloat(),
			(11 + 9).toFloat(),
		)

		assertEquals(fasit, delay.buffer.toList())
	}
}