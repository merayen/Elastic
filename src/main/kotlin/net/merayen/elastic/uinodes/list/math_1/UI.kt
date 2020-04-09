package net.merayen.elastic.uinodes.list.math_1

import net.merayen.elastic.backend.logicnodes.list.math_1.Mode
import net.merayen.elastic.backend.logicnodes.list.math_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override var layoutWidth = 140f
	override var layoutHeight = 60f
	private var mode = Mode.ADD

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
			"a" -> port.translation.y = 40f
			"b" -> port.translation.y = 60f
			"out" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onProperties(properties: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
}