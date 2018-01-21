package net.merayen.elastic.uinodes.list.poly_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.components.Button
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView

class UI : UINode() {
	private val unison: ParameterSlider

	init {
		width = 100f
		height = 100f

		val button = Button()
		button.label = "Open"
		button.translation.x = 10f
		button.translation.y = 20f
		button.setHandler { search.parentByType(NodeView::class.java)!!.swapView(node_id) }
		add(button)

		unison = ParameterSlider()
		unison.translation.x = 5f
		unison.translation.y = 40f
		unison.setHandler(object : ParameterSlider.IHandler {
			override fun onLabelUpdate(value: Double) = String.format("%d", Math.round(value * 31) + 1)

			override fun onChange(value: Double, programatic: Boolean) = sendParameter("unison", Math.round(value * 31) + 1)

			override fun onButton(offset: Int) {
				unison.value = unison.value + offset / 31.0
			}
		})
		add(unison)
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

	override fun onMessage(message: NodeParameterMessage) {
		if (message is NodeParameterMessage)
			if (message.key == "unison")
				unison.value = ((message.value as Number).toInt() - 1) / 31.0
	}

	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(key: String, value: Any) {}
}
