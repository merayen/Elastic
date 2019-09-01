package net.merayen.elastic.ui.objects

import net.merayen.elastic.ui.Supervisor
import net.merayen.elastic.ui.SurfaceHandler
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.surface.DummySurface
import net.merayen.elastic.ui.util.DrawContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UIClipTest {
	private class TestTop : UIObject()

	private var supervisor: Supervisor? = null

	@BeforeEach
	fun setUp() {
		val top = TestTop()
		val supervisor = Supervisor(top)
		val surfaceHandler = SurfaceHandler()

		surfaceHandler.createSurface("test", DummySurface::class)

		surfaceHandler.handler = object : SurfaceHandler.Handler {
			override fun onDraw(drawContext: DrawContext) {
				supervisor.draw(drawContext)
			}
		}

		this.supervisor = supervisor
	}

	@AfterEach
	fun tearDown() {
	}

	@Test
	fun testNoDrawingOutsideClip() {
		Thread.sleep(10000)
	}
}