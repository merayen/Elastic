package net.merayen.elastic.uinodes.list.basemath

import net.merayen.elastic.backend.logicnodes.list.basemath.BaseMathProperties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.max

abstract class BaseMathUI : UINode() {
	abstract val symbol: String

	private val symbolLabel = Label()

	override fun onInit() {
		super.onInit()
		titlebar.title = symbol

		symbolLabel.fontSize = 24f
		symbolLabel.text = symbol
		symbolLabel.align = Label.Align.CENTER
		symbolLabel.translation.x = 120f / 2
		symbolLabel.translation.y = 20f
		add(symbolLabel)

		layoutWidth = 120f
		layoutHeight = 80f
	}

	final override fun onCreatePort(port: UIPort) {
		if (port.name == "out") {
			port.translation.x = 120f
			port.translation.y = 20f
		} else if (port.name.startsWith("in")) {
			val number = port.name.substring(2).toInt()
			port.translation.y = number * 20f + 40f
			layoutHeight = max(layoutHeight, port.translation.y + 40f)
		} else error("Unknown port ${port.name}")
	}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as BaseMathProperties
		properties.portValues?.apply {
			TODO("update port parameters here")
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onData(message: NodeDataMessage) {}
}