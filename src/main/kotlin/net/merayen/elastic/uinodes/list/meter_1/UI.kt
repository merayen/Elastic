package net.merayen.elastic.uinodes.list.meter_1

import net.merayen.elastic.backend.logicnodes.list.meter_1.MeterSignalData
import net.merayen.elastic.backend.logicnodes.list.meter_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.Checkbox
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class UI : UINode() {
	private var minValue = 0f
	private var maxValue = 1f
	private var value = 0f
	private var meter: MeterBase? = null

	/**
	 * Adjust the minValue and maxValue automatically to the maximum/minimum signal value seen
	 */
	private var auto = false

	private val typeSelect = DropDown(object : DropDown.Handler {
		override fun onChange(selected: DropDown.Item) {
			useMeter(Properties.MeterType.valueOf((selected.dropdownItem as Label).text).cls)
		}
	})

	private val autoCheckbox = Checkbox()
	private val resetButton = Button("Reset")

	override fun onInit() {
		super.onInit()
		layoutWidth = 200f
		layoutHeight = 80f

		for (meterType in Properties.MeterType.values()) {
			typeSelect.addMenuItem(object : DropDown.Item(Label(meterType.name), TextContextMenuItem(meterType.name)) {})
		}
		typeSelect.layoutWidth = 60f
		add(typeSelect)

		autoCheckbox.label.text = "Auto"
		add(autoCheckbox)

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

		autoCheckbox.whenChanged = {
			auto = autoCheckbox.checked
			send(
				Properties(
					auto = autoCheckbox.checked
				)
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
		properties.meterType?.apply {
			useMeter(Properties.MeterType.valueOf(this).cls)
		}
	}

	override fun onUpdate() {
		super.onUpdate()

		meter?.minValue = minValue
		meter?.maxValue = maxValue
		meter?.value = value

		typeSelect.translation.x = 10f
		typeSelect.translation.y = layoutHeight - 20
		autoCheckbox.translation.x = 80f
		autoCheckbox.translation.y = layoutHeight - 20
		resetButton.translation.x = 130f
		resetButton.translation.y = layoutHeight - 20
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

	private fun useMeter(cls: KClass<out MeterBase>) {
		val meter = meter

		if (meter != null)
			remove(meter)

		val newMeter = cls.primaryConstructor!!.call()

		newMeter.translation.x = 10f
		newMeter.translation.y = 20f
		add(newMeter)

		layoutWidth = max(200f, newMeter.layoutWidth + 20f)
		layoutHeight = newMeter.layoutHeight + 60f

		this.meter = newMeter
	}
}