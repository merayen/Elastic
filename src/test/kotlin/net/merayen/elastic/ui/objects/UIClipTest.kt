package net.merayen.elastic.ui.objects

import net.merayen.elastic.ui.*
import net.merayen.elastic.ui.surface.Swing
import net.merayen.elastic.ui.util.DrawContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import kotlin.test.assertEquals

internal class UIClipTest {
	private class TestTop : UIObject()

	private var supervisor: Supervisor? = null
	private var top: UIObject? = null
	private var surfaceHandler: SurfaceHandler? = null

	@BeforeEach
	fun setUp() {
		val top = TestTop()
		val supervisor = Supervisor(top)
		val surfaceHandler = SurfaceHandler()

		surfaceHandler.createSurface("test", Swing::class)//DummySurface::class) // Not bothering to draw anything on screen

		surfaceHandler.handler = object : SurfaceHandler.Handler {
			override fun onDraw(drawContext: DrawContext) {
				supervisor.draw(drawContext)
			}
		}

		this.supervisor = supervisor
		this.top = top
		this.surfaceHandler = surfaceHandler
	}

	@AfterEach
	fun tearDown() {
		surfaceHandler?.end()
	}

	@Test
	@Timeout(10)
	fun testIsVisible() {
		val top = top!!

		val clip = object : UIClip() {
			override fun onDraw(draw: Draw) {
				super.onDraw(draw)
				draw.setColor(0f, 0f, 0f)
				draw.setStroke(2f)
				draw.rect(0f, 0f, layoutWidth, layoutHeight)
			}
		}
		clip.translation.x = 100f
		clip.translation.y = 100f
		clip.layoutWidth = 100f
		clip.layoutHeight = 100f
		top.add(clip)


		val intermediateObject = UIObject()
		intermediateObject.translation.x = 50f
		intermediateObject.translation.y = 50f
		clip.add(intermediateObject)


		val innerObject = object : UIObject(), FlexibleDimension {
			override var layoutWidth = 20f
			override var layoutHeight = 20f

			var visible = false

			@Volatile
			var drawCount = 0

			override fun onDraw(draw: Draw) {
				draw.setColor(0f, 0f, 1f)
				draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
				drawCount++
			}

			override fun onUpdate() {
				val uiClip = search.parentByType(UIClip::class.java)!!
				visible = uiClip.isVisible(this)
			}

			fun waitForDraw() {
				drawCount = 0
				while (drawCount < 2)
					Thread.sleep(10)
			}
		}
		innerObject.translation.x = 10f
		innerObject.translation.y = 10f

		intermediateObject.add(innerObject)

		intermediateObject.translation.x = 0f
		intermediateObject.translation.y = 0f
		innerObject.waitForDraw()
		assertEquals(true, innerObject.visible)

		intermediateObject.translation.x = -28f
		intermediateObject.translation.y = 0f
		innerObject.waitForDraw()
		assertEquals(true, innerObject.visible)

		intermediateObject.translation.x = -32f
		intermediateObject.translation.y = 0f
		innerObject.waitForDraw()
		assertEquals(false, innerObject.visible)

		intermediateObject.translation.x = 0f
		intermediateObject.translation.y = -28f
		innerObject.waitForDraw()
		assertEquals(true, innerObject.visible)

		intermediateObject.translation.x = 50f
		intermediateObject.translation.y = 88f
		innerObject.waitForDraw()
		assertEquals(true, innerObject.visible)

		intermediateObject.translation.x = 50f
		intermediateObject.translation.y = 92f
		innerObject.waitForDraw()
		assertEquals(false, innerObject.visible)

		intermediateObject.translation.x = 88f
		intermediateObject.translation.y = 50f
		innerObject.waitForDraw()
		assertEquals(true, innerObject.visible)

		intermediateObject.translation.x = 100f
		intermediateObject.translation.y = 100f
		innerObject.waitForDraw()
		assertEquals(false, innerObject.visible)

		intermediateObject.translation.x = -100f
		intermediateObject.translation.y = -100f
		innerObject.waitForDraw()
		assertEquals(false, innerObject.visible)
	}
}