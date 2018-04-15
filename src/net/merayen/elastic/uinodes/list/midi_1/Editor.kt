package net.merayen.elastic.uinodes.list.midi_1

import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.midiroll.MidiRoll
import net.merayen.elastic.ui.objects.nodeeditor.NodeEditor
import java.util.HashMap

class Editor : NodeEditor {
	private val midiRoll: MidiRoll
	private val layout = AutoLayout(LayoutMethods.HorizontalLiquidBox())

	constructor(nodeId: String) : super(nodeId) {
		midiRoll = MidiRoll(object : MidiRoll.Handler {
			override fun onUp(tangent_no: Int) {
				sendData(object : HashMap<String, Any>() {
					init {
						put("tangent_up", tangent_no)
					}
				})
			}

			override fun onDown(tangent_no: Int) {
				sendData(object : HashMap<String, Any>() {
					init {
						put("tangent_down", tangent_no)
					}
				})
			}
		})

		add(layout)

		layout.add(midiRoll)

		layout.placement.applyConstraint(midiRoll, LayoutMethods.HorizontalLiquidBox.Constraint(0.5f))
	}

	override fun onUpdate() {
		super.onUpdate()

		layout.placement.layoutWidth = getWidth()
		layout.placement.layoutHeight = getHeight()
	}

	override fun onMessage(message: NodeMessage) {}
}