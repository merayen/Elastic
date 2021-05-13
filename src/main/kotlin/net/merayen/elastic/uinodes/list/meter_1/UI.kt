package net.merayen.elastic.uinodes.list.meter_1

import net.merayen.elastic.backend.logicnodes.list.meter_1.MeterSignalData
import net.merayen.elastic.backend.logicnodes.list.meter_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.Checkbox
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.max
import kotlin.math.min

class UI : UINode() {
	private var minValue = 0f
	private var maxValue = 1f
	private var value = 0f

	/**
	 * Adjust the minValue and maxValue automatically to the maximum/minimum signal value seen
	 */
	private var auto = false

	private val autoCheckbox = Checkbox()
	private val resetButton = Button("Reset")
	private val minValueLabel = Label()
	private val maxValueLabel = Label()

	override fun onInit() {
		super.onInit()
		layoutWidth = 200f
		layoutHeight = 80f

		autoCheckbox.translation.x = 10f
		autoCheckbox.translation.y = layoutHeight - 20
		autoCheckbox.label.text = "Auto"
		add(autoCheckbox)

		resetButton.translation.x = 60f
		resetButton.translation.y = layoutHeight - 20
		resetButton.handler = object : Button.IHandler {
			override fun onClick() {
				auto = true
				autoCheckbox.checked = true
				minValue = 0f
				maxValue = 0f
				send(
					Properties(
						auto = true,
						minValue = 0f,
						maxValue = 0f,
					)
				)
			}
		}
		add(resetButton)

		minValueLabel.translation.x = 12f
		minValueLabel.translation.y = 20f
		minValueLabel.shadow = false
		add(minValueLabel)

		maxValueLabel.translation.x = layoutWidth - 12
		maxValueLabel.translation.y = 20f
		maxValueLabel.align = Label.Align.RIGHT
		maxValueLabel.shadow = false
		add(maxValueLabel)

		autoCheckbox.whenChanged = {
			auto = autoCheckbox.checked
			send(
				Properties(
					auto = autoCheckbox.checked
				)
			)
		}
	}

	override fun onUpdate() {
		super.onUpdate()
		minValueLabel.text = "%.3f".format(minValue)
		maxValueLabel.text = "%.3f".format(maxValue)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(0, 0, 0)
		draw.fillRect(10f, 20f, layoutWidth - 20, layoutHeight - 50)

		draw.setColor(0f, 1f, 0f)
		if (maxValue > minValue) {
			val value = max(minValue, min(maxValue, value))
			draw.fillRect(
				12f,
				22f,
				(layoutWidth - 20 - 4) * ((value - minValue) / (maxValue - minValue)),
				layoutHeight - 54
			)
		}
	}

	override fun onCreatePort(port: UIPort) {
		port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as Properties
		properties.minValue?.apply { minValue = this }
		properties.maxValue?.apply { maxValue = this }
		properties.auto?.apply { auto = this }
	}

	override fun onData(message: NodeDataMessage) {
		if (message is MeterSignalData) {
			if (auto) {
				value = message.value

				if (value < minValue)
					minValue = value

				if (value > maxValue)
					maxValue = value

			} else {
				value = max(minValue, min(maxValue, message.value))
			}
		}
	}
}