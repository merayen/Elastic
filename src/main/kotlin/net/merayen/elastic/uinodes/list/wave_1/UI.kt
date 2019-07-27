package net.merayen.elastic.uinodes.list.wave_1

import net.merayen.elastic.backend.logicnodes.list.wave_1.Data
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private lateinit var typeDropDown: DropDown

	class DropDownItem(val type: Data.Type, val label: String) : DropDown.Item(Label(label), TextContextMenuItem(label))

	init {
		typeDropDown = DropDown(object : DropDown.Handler {
			override fun onChange(selected: DropDown.Item) {
				selected as DropDownItem
				sendParameter(Data(type = selected.type.name))
			}
		})
		typeDropDown.addMenuItem(DropDownItem(Data.Type.SINE, "Sine"))
		typeDropDown.addMenuItem(DropDownItem(Data.Type.TRIANGLE, "Triangle"))
		typeDropDown.addMenuItem(DropDownItem(Data.Type.SQUARE, "Square"))
		typeDropDown.addMenuItem(DropDownItem(Data.Type.NOISE, "Noise"))
		typeDropDown.translation.x = 20f
		typeDropDown.translation.y = 20f
		typeDropDown.layoutWidth = 60f
		add(typeDropDown)
	}

	override fun onInit() {
		super.onInit()
		layoutWidth = 100f
		layoutHeight = 80f
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "frequency") {
			port.translation.x = 0f
			port.translation.y = 20f

		} else if (port.name == "audio") {
			port.translation.x = layoutWidth
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeData) {}
	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(instance: BaseNodeData) {
		instance as Data

		val type = instance.type
		if (type != null) {
			val newType = Data.Type.valueOf(type)
			for (x in typeDropDown.getItems()) {
				val item = x as DropDownItem
				if (item.type == newType) {
					typeDropDown.setViewItem(item)
					break
				}
			}
		}
	}
}