package net.merayen.elastic.uinodes.list.wave_1

import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private lateinit var typeDropDown: DropDown

	class DropDownItem(val type: Properties.Type, val label: String) : DropDown.Item(Label(label), TextContextMenuItem(label))

	init {
		typeDropDown = DropDown(object : DropDown.Handler {
			override fun onChange(selected: DropDown.Item) {
				selected as DropDownItem
				sendProperties(Properties(type = selected.type.name))
			}
		})
		typeDropDown.addMenuItem(DropDownItem(Properties.Type.SINE, "Sine"))
		typeDropDown.addMenuItem(DropDownItem(Properties.Type.TRIANGLE, "Triangle"))
		typeDropDown.addMenuItem(DropDownItem(Properties.Type.SQUARE, "Square"))
		typeDropDown.addMenuItem(DropDownItem(Properties.Type.SAW, "Saw"))
		typeDropDown.addMenuItem(DropDownItem(Properties.Type.NOISE, "Noise"))
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

		} else if (port.name == "out") {
			port.translation.x = layoutWidth
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onData(message: NodeDataMessage) {}

	override fun onProperties(instance: BaseNodeProperties) {
		instance as Properties

		val type = instance.type
		if (type != null) {
			val newType = Properties.Type.valueOf(type)
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