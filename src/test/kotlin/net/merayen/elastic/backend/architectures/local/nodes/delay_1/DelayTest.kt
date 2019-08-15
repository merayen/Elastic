package net.merayen.elastic.backend.architectures.local.nodes.delay_1

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DelayTest {
	@Test
	fun testDelay() {
		val delay = Delay(10 + 2)
		delay.addTap(Delay.Tap(0, 1f, 1f))
		delay.addTap(Delay.Tap(2, 1f, 1f))

		assertEquals(0, delay.process(floatArrayOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f), 0, 10))

		val fasit = floatArrayOf(0f, 1f, (2 + 0).toFloat(), (3 + 1).toFloat(), (4 + 2).toFloat(), (5 + 3).toFloat(), (6 + 4).toFloat(), (7 + 5).toFloat(), (8 + 6).toFloat(), (9 + 7).toFloat())

		assertTrue {
			var i = 0
			fasit.all {
				it == delay.buffer[i++]
			}
		}
	}
}