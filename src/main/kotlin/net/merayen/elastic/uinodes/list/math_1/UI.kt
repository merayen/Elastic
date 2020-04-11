package net.merayen.elastic.uinodes.list.math_1

import net.merayen.elastic.backend.logicnodes.list.math_1.Mode
import net.merayen.elastic.backend.logicnodes.list.math_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.pow
import kotlin.math.roundToInt

class UI : UINode() {
	override var layoutWidth = 140f
	override var layoutHeight = 80f
	private var mode = Mode.ADD

	private val portParameters = ArrayList<PortParameter>()

	private val dropDown = DropDown(object : DropDown.Handler {
		override fun onChange(selected: DropDown.Item) {
			sendProperties(Properties(mode = (selected.dropdownItem as Label).text))
		}
	})

	override fun onInit() {
		super.onInit()

		for (mode in Mode.values())
			dropDown.addMenuItem(DropDown.Item(Label(mode.name), TextContextMenuItem(mode.name)))

		dropDown.translation.x = 20f
		dropDown.translation.y = 20f
		add(dropDown)
	}

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"a" -> port.translation.y = 50f
			"b" -> port.translation.y = 70f
			"out" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
		}

		if (port.name != "out") { // Assign PortParameter to the port if it is an input, so the user can choose value if it is not selected and this comment is too long I should have word wrapped it, but I did not do it
			val slider = ParameterSlider()
			slider.setHandler(object : ParameterSlider.Handler {
				override fun onChange(value: Double, programatic: Boolean) {
					if (port.name == "a") {
						sendMessage(
							NodePropertyMessage(
								nodeId,
								Properties(aValue = value.pow(2).toFloat() * 1000f)
							)
						)
					} else if (port.name == "b") {
						sendMessage(
							NodePropertyMessage(
								nodeId,
								Properties(bValue = value.pow(2).toFloat() * 1000f)
							)
						)
					}
				}

				override fun onLabelUpdate(value: Double) = "${(value.pow(2) * 10000f).roundToInt() / 10f}"
				override fun onButton(offset: Int) {}

			})
			slider.translation.x = 20f
			slider.translation.y = port.translation.y - 10f
			add(slider)
			val portParameter = PortParameter(this, port, slider, UIObject())
			add(portParameter)
			portParameters.add(portParameter)
		}
	}

	override fun onRemovePort(port: UIPort) {
		if (port.name != "out") {
			val portParameter = portParameters.firstOrNull { it.port === port }
			if (portParameter != null) {
				remove(portParameter.notConnected)
				remove(portParameter)
				portParameters.remove(portParameter)
			}
		}
	}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as Properties

		val aValue = properties.aValue
		val bValue = properties.bValue

		if (aValue != null)
			(portParameters[0].notConnected as ParameterSlider).value = (aValue / 1000f).pow(1/2f).toDouble()

		if (bValue != null)
			(portParameters[1].notConnected as ParameterSlider).value = (bValue / 1000f).pow(1/2f).toDouble()
	}

	override fun onData(message: NodeDataMessage) {}
}