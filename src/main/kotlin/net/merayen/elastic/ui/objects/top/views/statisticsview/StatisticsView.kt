package net.merayen.elastic.ui.objects.top.views.statisticsview

import net.merayen.elastic.system.intercom.StatisticsReportMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.Rect
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Meter
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.top.views.View
import kotlin.math.roundToInt

class StatisticsView : View() {
	private val bar = StatisticsViewBar()

	private var notProcessingAvgTime = 0.0
	private var avgTime = 0.0
	private var maxTime = 0.0
	private var frameDuration = 0.0

	private val avgMeter = Meter()
	private val maxMeter = Meter()
	private val notProcessingMeter = Meter()

	class ScrollTest : UIObject() {
		var _absoluteOutline: Rect? = null

		override fun onDraw(draw: Draw) {
			//draw.disableOutline()
			draw.setColor(1f,1f,1f)
			draw.setStroke(1f)
			draw.rect(10f, 10f, getWidth() - 20, getHeight() - 20)
			_absoluteOutline = draw.absoluteOutline
		}
	}

	private val scrollContent = ScrollTest()

	private val scroll = Scroll(scrollContent)

	override fun cloneView() = StatisticsView()

	override fun onInit() {
		super.onInit()

		scroll.translation.x = 100f
		scroll.translation.y = 100f
		scroll.layoutWidth = 200f
		scroll.layoutHeight = 200f
		add(scroll)

		with(avgMeter.translation) {
			x = 220f
			y = 50f
		}
		scrollContent.add(avgMeter)

		maxMeter.translation.x = 20f
		maxMeter.translation.y = 80f
		scrollContent.add(maxMeter)

		notProcessingMeter.translation.x = 20f
		notProcessingMeter.translation.y = 110f
		scrollContent.add(notProcessingMeter)

		add(bar)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(1f, 0f, 1f)
		draw.setFont("", 20f)
		draw.text("Avg: ${"%.3f".format(avgTime / frameDuration)}ms", 150f, 100f)
		draw.text("Max: ${"%.3f".format(maxTime / frameDuration)}ms", 150f, 150f)
		draw.text("Avg not processing: ${"%.3f".format(notProcessingAvgTime / frameDuration)}ms", 150f, 200f)
		draw.text("Scroll content width=${"%.3f".format(scrollContent.getWidth())}, height=${"%.3f".format(scrollContent.getHeight())}", 10f, 400f)
		draw.text("Scroll content outline_abs=${scrollContent.absoluteOutline}", 10f, 450f)
		draw.text("Scroll content outline_abs=${scrollContent._absoluteOutline}", 10f, 500f)
	}

	fun handleStatisticsReportMessage(message: StatisticsReportMessage) {
		notProcessingAvgTime = message.notProcessingFrameTimeAvg
		avgTime = message.avgFrameTime
		maxTime = message.maxFrameTime
		frameDuration = message.frameDuration

		avgMeter.value = (avgTime / frameDuration).toFloat()
		avgMeter.content.text = "${(avgMeter.value * 100).roundToInt()}%"
		avgMeter.direction = Meter.Direction.DOWN
		avgMeter.layoutWidth = 100f
		avgMeter.layoutHeight = 100f

		maxMeter.value = (maxTime / frameDuration).toFloat()
		maxMeter.content.text = "${(maxMeter.value * 100).roundToInt()}%"

		notProcessingMeter.value = (notProcessingAvgTime / frameDuration).toFloat()
		notProcessingMeter.content.text = "${(notProcessingMeter.value * 100).roundToInt()}%"
	}
}
