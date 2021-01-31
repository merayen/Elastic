package net.merayen.elastic.ui.objects

import net.merayen.elastic.ui.*
import net.merayen.elastic.ui.surface.Swing
import net.merayen.elastic.ui.util.DrawContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

internal class UIClipTest {
	private class TestTop : UIObject()

	private var supervisor: Supervisor? = null
	private var top: UIObject? = null
	private var surfaceHandler: SurfaceHandler? = null
	private var clip: UIClip? = null
	private var intermediateObject: UIObject? = null
	private var innerObject: InnerObject? = null

	class InnerObject : UIObject(), FlexibleDimension {
		override var layoutWidth = 20f
		override var layoutHeight = 20f

		private var visible = false

		@Volatile
		var drawCount = 0

		override fun onDraw(draw: Draw) {
			draw.setColor(0f, 0f, 1f)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
			drawCount++
		}

		override fun onUpdate() {

		}

		private fun waitForDraw() {
			drawCount = 0
			while (drawCount < 2)
				Thread.sleep(10)
		}

		fun isVisible(): Boolean {
			waitForDraw()
			return search.parentByType(UIClip::class.java)!!.isVisible(this)
		}
	}

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


		val innerObject = InnerObject()
		intermediateObject.add(innerObject)


		this.supervisor = supervisor
		this.top = top
		this.surfaceHandler = surfaceHandler
		this.clip = clip
		this.intermediateObject = intermediateObject
		this.innerObject = innerObject
	}

	@AfterEach
	fun tearDown() {
		surfaceHandler?.end()
	}

	@Test
	@Timeout(10)
	fun testIsVisible() {
		val intermediateObject = intermediateObject!!
		val innerObject = innerObject!!

		innerObject.translation.x = 10f
		innerObject.translation.y = 10f

		intermediateObject.translation.x = 0f
		intermediateObject.translation.y = 0f
		assertEquals(true, innerObject.isVisible())

		intermediateObject.translation.x = -28f
		intermediateObject.translation.y = 0f
		assertEquals(true, innerObject.isVisible())

		intermediateObject.translation.x = -32f
		intermediateObject.translation.y = 0f
		assertEquals(false, innerObject.isVisible())

		intermediateObject.translation.x = 0f
		intermediateObject.translation.y = -28f
		assertEquals(true, innerObject.isVisible())

		intermediateObject.translation.x = 50f
		intermediateObject.translation.y = 88f
		assertEquals(true, innerObject.isVisible())

		intermediateObject.translation.x = 50f
		intermediateObject.translation.y = 92f
		assertEquals(false, innerObject.isVisible())

		intermediateObject.translation.x = 88f
		intermediateObject.translation.y = 50f
		assertEquals(true, innerObject.isVisible())

		intermediateObject.translation.x = 100f
		intermediateObject.translation.y = 100f
		assertEquals(false, innerObject.isVisible())

		intermediateObject.translation.x = -100f
		intermediateObject.translation.y = -100f
		assertEquals(false, innerObject.isVisible())
	}

	@Test
	@Timeout(10)
	fun testIsVisibleWithScaling() {
		val intermediateObject = intermediateObject!!
		val innerObject = innerObject!!

		intermediateObject.translation.scaleX = .1f
		intermediateObject.translation.scaleY = .1f

		innerObject.translation.x = 4f
		innerObject.translation.y = 2f
		assertTrue(innerObject.isVisible())

		innerObject.translation.x = 2f
		innerObject.translation.y = 4f
		assertTrue(innerObject.isVisible())

		innerObject.translation.x = 5f
		innerObject.translation.y = 5f
		assertFalse(innerObject.isVisible())

		innerObject.translation.x = -10f
		innerObject.translation.y = -10f
		assertTrue(innerObject.isVisible())

		innerObject.translation.x = -19f
		innerObject.translation.y = -19f
		Thread.sleep(10000)
		assertTrue(innerObject.isVisible())
	}
}
