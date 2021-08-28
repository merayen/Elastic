package net.merayen.elastic.uinodes.list.lever_1

import net.merayen.elastic.backend.logicnodes.list.lever_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val slider = ParameterSlider()

	init {
		slider.setHandler(object : ParameterSlider.Handler {
			override fun onChange(value: Double, programatic: Boolean) {
				send(Properties(value=value.toFloat()))
			}

			override fun onButton(offset: Int) {
				slider.value += 0.01f
			}

			override fun onLabelUpdate(value: Double) = String.format("%.2f", value)
		})
	}

	override fun onInit() {
		super.onInit()

		layoutWidth = 100f
		layoutHeight = 50f

		slider.translation.x = 10f
		slider.translation.y = 20f
		slider.layoutWidth = layoutWidth - 20f
		add(slider)

		titlebar.title = "Lever"
	}

	override fun onCreatePort(port: UIPort) {
		port.translation.x = layoutWidth
		port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as Properties
		val value = properties.value
		if (value != null)
			slider.value = value.toDouble()
	}

	override fun onData(message: NodeDataMessage) {}
}