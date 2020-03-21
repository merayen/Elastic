package net.merayen.elastic.ui.objects.top.views.splashview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.MutableColor
import net.merayen.elastic.ui.objects.top.window.Window
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.util.Pacer
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

class SplashView : View() {
	private var loadingBubbles = Pacer()
	private var coloringPacer: Pacer? = null // TODO
	private var loadingBubblesPos = 0f
	private var loadingDescription = "Initializing"
	private val bgColor = MutableColor(0, 0, 0)
	private val fgColor = MutableColor(1f, 1f, 1f)
	override fun cloneView() = SplashView()

	override fun onInit() {
		super.onInit()

		val window = search.parentByType(Window::class.java)
		if (window == null) {
			println("WARNING: Window not found, can not apply properties to it")
		} else {
			window.layoutWidth = 1000f
			window.layoutHeight = 600f
			window.isDecorated = false
			window.center()
		}
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		// Update times
		loadingBubbles.update()
		loadingBubblesPos += loadingBubbles.getDiff()
		val initPos = 1 - max(0f, 1 - loadingBubblesPos / 4).pow(4)
		val initPosOrders = 1 - max(0f, 1 - loadingBubblesPos / 4).pow(3)

		bgColor.red = initPos
		bgColor.green = initPos
		bgColor.blue = initPos
		fgColor.red = 1 - initPos
		fgColor.green = 1 - initPos
		fgColor.blue = 1 - initPos

		draw.setColor(bgColor)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		// Top and bottom bar
		draw.setColor(fgColor)
		draw.fillRect(0f, 0f, layoutWidth, 100f * initPosOrders)
		draw.fillRect(0f, layoutHeight - 100f * initPosOrders, layoutWidth, 100f)

		draw.setColor(fgColor)
		draw.setFont("", 64f)

		draw.text("Loading", 50f + 120f * (1 - initPos), layoutHeight / 2 + 50f)

		// Funky moving bubbles
		for (i in 0 until 20) {
			val xNormalized = (((i / 20f + loadingBubblesPos / 5)) % 1).pow(2)
			val x = ((xNormalized * layoutWidth + 50f) % layoutWidth) + 100 * (1 - initPos)
			draw.fillOval(x, layoutHeight / 2 + 50f + sin(PI.toFloat() / 2) * (x / layoutWidth).pow(4) * 200f, 10f, 10f)
		}

		// Loading description
		draw.setColor(fgColor)
		draw.setFont("", 24f)
		draw.text(loadingDescription, 50f + 140f * (1 - initPos), layoutHeight / 2 + 90f)
	}

	override val easyMotionBranch = object : Branch(this) {}
}
