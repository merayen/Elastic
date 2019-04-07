package net.merayen.elastic.uinodes.list.midi_in_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.menu.MenuListItem

class UI : UINode() {
	private var midi_device: Label? = null
	private lateinit var which: DropDown

	override fun onInit() {
		super.onInit()

		layoutWidth = 150f
		layoutHeight = 100f

		titlebar.title = "MIDI Input"

		midi_device = Label()
		midi_device!!.translation.x = 10f
		midi_device!!.translation.y = 20f
		add(midi_device!!)

		which = DropDown(object : DropDown.Handler {
			override fun onChange(selected: DropDown.Item) {}
		})
		which.translation.x = 10f
		which.translation.y = 50f
		add(which)

		which.addMenuItem(DropDown.Item(Label("Hei"), TextContextMenuItem("Hei!")))

		which.addMenuItem(DropDown.Item(Label("Du"), TextContextMenuItem("Du!")))
	}

	override fun onDraw(draw: Draw) {
		midi_device!!.text = "Device: "
		super.onDraw(draw)
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "output") {
			port.translation.x = 150f
			port.translation.y = 20f
			port.color = UIPort.MIDI_PORT
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: NodeParameterMessage) {}

	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(key: String, value: Any) {}
}
