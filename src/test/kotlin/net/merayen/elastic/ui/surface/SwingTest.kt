package net.merayen.elastic.ui.surface

import org.junit.jupiter.api.*
import java.awt.Color
import java.awt.Graphics2D
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class SwingTest {
	private lateinit var swing: Swing
	private var drawer: ((draw: Graphics2D) -> Unit)? = null

	@BeforeEach
	fun setUp() {
		swing = Swing("test", object : Surface.Handler {
			override fun onDraw(graphics2d: Graphics2D) {
				val drawer = drawer
				if (drawer != null)
					drawer(graphics2d)
			}
		})
	}

	@AfterEach
	fun tearDown() {
		swing.end()
		drawer = null
	}

	@Test
	@Timeout(1)
	@Order(1)
	fun isReady() {
		while (!swing.isReady())
			Thread.sleep(10)
	}

	@Test
	fun drawALot() {
		var u = 0L
		var frames = 0
		drawer = {
			for (i in 0 until 1000) {
				val b = i + u.toInt()
				it.color = Color(b % 256, (b*b) % 256, (b) % 256)
				val x = (b+i) % 1000
				val y = (b*b+i) % 1000
				it.fillRect(x, y, 10, 10)
			}

			u += 123

			frames++
		}

		Thread.sleep(1000)

		assertTrue { frames > 10 }
	}
}