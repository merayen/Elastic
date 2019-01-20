package net.merayen.elastic.uinodes.list.midi_1.editor

import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.midiroll.MidiRoll
import net.merayen.elastic.ui.objects.nodeeditor.NodeEditor
import java.util.HashMap

/**
 * Editor for the MIDI-roll shown in a separate view.
 */
class Editor : NodeEditor {
	private val midiRoll: MidiRoll
	private val menuBar: MidiEditorMenuBar
	private val layout = AutoLayout(LayoutMethods.HorizontalLiquidBox())

	constructor(nodeId: String) : super(nodeId) {
		menuBar = MidiEditorMenuBar(object : MidiEditorMenuBar.Handler {
			override fun onChangeNoteStart(value: Float) {

			}

			override fun onChangeNoteLength(value: Float) {

			}

			override fun onTransposeNote(value: Int) {

			}
		})

		menuBar.layoutHeight = 40f
		add(menuBar)

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

		midiRoll.translation.scale_x = 0.5f
		midiRoll.translation.scale_y = 0.5f

		layout.translation.y = 40f

		add(layout)

		layout.add(midiRoll)

		layout.placement.applyConstraint(midiRoll, LayoutMethods.HorizontalLiquidBox.Constraint(1f))
	}

	override fun onUpdate() {
		super.onUpdate()

		layout.placement.layoutWidth = getWidth()
		layout.placement.layoutHeight = getHeight() - 40f

		menuBar.layoutWidth = getWidth()

		midiRoll.translation.scale_x = 0.5f
	}

	override fun onMessage(message: NodeMessage) {}
}