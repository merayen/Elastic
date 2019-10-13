package net.merayen.elastic.uinodes.list.poly_1

import net.merayen.elastic.backend.logicnodes.list.poly_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import kotlin.math.roundToInt

class UI : UINode() {
	private val unison: ParameterSlider

	init {
		layoutWidth = 100f
		layoutHeight = 100f

		val button = Button()
		button.label = "Open"
		button.translation.x = 10f
		button.translation.y = 20f
		button.handler = object : Button.IHandler {
			override fun onClick() {
				search.parentByType(NodeView::class.java)!!.swapView(nodeId)
			}
		}
		add(button)

		unison = ParameterSlider()
		unison.translation.x = 5f
		unison.translation.y = 40f
		unison.setHandler(object : ParameterSlider.IHandler {
			override fun onLabelUpdate(value: Double) = String.format("%d", Math.round(value * 31) + 1)

			override fun onChange(value: Double, programatic: Boolean) = sendParameter(Properties(unison = (value * 31).roundToInt() + 1))

			override fun onButton(offset: Int) {
				unison.value = unison.value + offset / 31.0
			}
		})
		add(unison)

		this.titlebar.title = "Poly"
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "input") {
			port.translation.y = 20f
		}

		if (port.name == "output") {
			port.translation.x = 100f
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onData(message: NodeDataMessage) {}

	override fun onMessage(instance: BaseNodeProperties) {
		if (instance is Properties) {
			val unisonData = instance.unison
			if (unisonData != null)
				unison.value = (unisonData - 1) / 31.0
		}
	}
}
