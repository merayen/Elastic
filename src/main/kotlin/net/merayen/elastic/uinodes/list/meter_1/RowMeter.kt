package net.merayen.elastic.uinodes.list.meter_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.Label
import kotlin.math.max
import kotlin.math.min

internal class RowMeter : MeterBase() {
	private val minValueLabel = Label()
	private val maxValueLabel = Label()

	init {
		layoutWidth = 160f
		layoutHeight = 40f
	}

	override fun onInit() {
		minValueLabel.translation.x = 2f
		minValueLabel.translation.y = 2f
		minValueLabel.shadow = false
		add(minValueLabel)

		maxValueLabel.translation.x = layoutWidth - 2
		maxValueLabel.translation.y = 2f
		maxValueLabel.align = Label.Align.RIGHT
		maxValueLabel.shadow = false
		add(maxValueLabel)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(0, 0, 0)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0f, 1f, 0f)
		if (maxValue > minValue) {
			val value = max(minValue, min(maxValue, value))
			draw.fillRect(
				2f,
				15f,
				(layoutWidth - 4) * ((value - minValue) / (maxValue - minValue)),
				layoutHeight - 17
			)
		}
	}

	override fun onUpdate() {
		super.onUpdate()
		minValueLabel.text = "%.3f".format(minValue)
		maxValueLabel.text = "%.3f".format(maxValue)
	}
}