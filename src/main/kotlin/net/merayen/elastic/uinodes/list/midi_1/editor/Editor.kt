package net.merayen.elastic.uinodes.list.midi_1.editor

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.list.midi_1.AddMidiMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.PushTangentMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.ReleaseTangentMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.RemoveMidiMessage
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.midiroll.MidiRoll
import net.merayen.elastic.ui.objects.nodeeditor.NodeEditor

/**
 * Editor for the MIDI-roll shown in a separate view.
 */
class Editor(nodeId: String) : NodeEditor(nodeId) {
	override fun onParameter(instance: BaseNodeData) {}

	private val midiRoll: MidiRoll
	private val menuBar = MidiEditorMenuBar(object : MidiEditorMenuBar.Handler {
		override fun onChangeNoteStart(value: Float) {

		}

		override fun onChangeNoteLength(value: Float) {

		}

		override fun onTransposeNote(value: Int) {

		}
	})
	private val layout = AutoLayout(LayoutMethods.HorizontalLiquidBox())

	init {
		menuBar.layoutHeight = 40f
		add(menuBar)

		midiRoll = MidiRoll(object : MidiRoll.Handler {
			override fun onAddMidi(eventZoneId: String, midiData: MidiData) {
				sendData(AddMidiMessage(nodeId, eventZoneId, midiData))
			}

			override fun onChangeNote(id: String, tangent: Int, start: Float, length: Float, weight: Float) {
				TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
			}

			override fun onRemoveMidi(id: String) {
				sendData(RemoveMidiMessage(nodeId, id))
			}

			override fun onUp(tangent_no: Int) {
				sendData(ReleaseTangentMessage(nodeId, tangent_no.toShort()))
			}

			override fun onDown(tangent_no: Int) {
				sendData(PushTangentMessage(nodeId, tangent_no.toShort()))
			}
		})
		midiRoll.translation.scaleX = 0.5f
		midiRoll.translation.scaleY = 0.5f

		layout.translation.y = 40f
		add(layout)

		layout.add(midiRoll)
		layout.placement.applyConstraint(midiRoll, LayoutMethods.HorizontalLiquidBox.Constraint(1f))
	}

	override fun onUpdate() {
		super.onUpdate()

		layout.placement.layoutWidth = layoutWidth
		layout.placement.layoutHeight = layoutHeight - 40f

		menuBar.layoutWidth = layoutWidth

		midiRoll.translation.scaleX = 0.5f
	}

	override fun onMessage(message: NodeMessage) {
		if (message is NodeDataMessage)
			midiRoll.handleMessage(message)
	}
}
