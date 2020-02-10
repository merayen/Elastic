package net.merayen.elastic.ui.objects.top.views.statisticsview

import net.merayen.elastic.backend.logicnodes.list.output_1.OutputNodeStatisticsData
import net.merayen.elastic.system.intercom.StatisticsReportMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.Meter
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.views.View
import kotlin.math.roundToInt

class StatisticsView : View() {
	private val bar = StatisticsViewBar()

	private var notProcessingAvgTime = 0.0
	private var avgTime = 0.0
	private var maxTime = 0.0
	private var frameDuration = 0.0

	private var availableBeforeAvg = 0.0
	private var availableBeforeMin = 0.0
	private var availableAfterAvg = 0.0
	private var availableAfterMin = 0.0

	private val avgMeter = Meter()
	private val maxMeter = Meter()
	private val notProcessingMeter = Meter()
	override fun cloneView() = StatisticsView()

	override fun onInit() {
		super.onInit()

		avgMeter.translation.x = 200f
		avgMeter.translation.y = 40f
		add(avgMeter)

		maxMeter.translation.x = 200f
		maxMeter.translation.y = 60f
		add(maxMeter)

		notProcessingMeter.translation.x = 200f
		notProcessingMeter.translation.y = 80f
		add(notProcessingMeter)

		add(bar)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(1f, 1f, 1f)
		draw.setFont("", 12f)
		draw.text("Avg: ${"%.3f".format(avgTime / frameDuration)}ms", 10f, 50f)
		draw.text("Max: ${"%.3f".format(maxTime / frameDuration)}ms", 10f, 70f)
		draw.text("Avg not processing: ${"%.3f".format(notProcessingAvgTime / frameDuration)}ms", 10f, 90f)

		draw.text("Available before writing: avg=${availableBeforeAvg.toInt()}, min=${availableBeforeMin.toInt()}", 10f, 130f)
		draw.text("Available after writing: avg=${availableAfterAvg.toInt()}, min=${availableAfterMin.toInt()}", 10f, 150f)
	}

	fun handleStatisticsReportMessage(message: StatisticsReportMessage) {
		notProcessingAvgTime = message.notProcessingFrameTimeAvg
		avgTime = message.avgFrameTime
		maxTime = message.maxFrameTime
		frameDuration = message.frameDuration

		avgMeter.value = (avgTime / frameDuration).toFloat()
		avgMeter.content.text = "${(avgMeter.value * 100).roundToInt()}%"

		maxMeter.value = (maxTime / frameDuration).toFloat()
		maxMeter.content.text = "${(maxMeter.value * 100).roundToInt()}%"

		notProcessingMeter.value = (notProcessingAvgTime / frameDuration).toFloat()
		notProcessingMeter.content.text = "${(notProcessingMeter.value * 100).roundToInt()}%"
	}

	fun handleOutputNodeStatistics(data: OutputNodeStatisticsData) {
		// TODO needs to show for each device, now it just mixes them and flickers between them, probably
		availableBeforeAvg = data.availableBeforeAvg
		availableBeforeMin = data.availableBeforeMin
		availableAfterAvg = data.availableAfterAvg
		availableAfterMin = data.availableAfterMin
	}

	override val easyMotionBranch = object : Branch(this) {}
}
