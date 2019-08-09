package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import java.util.ArrayList

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	/**
	 * Midi buffer, with midi data made by the user, e.g when clicking the piano roll or doing something that should
	 * give immediate feedback.
	 */
	internal var buffer: ArrayList<ShortArray> = ArrayList()

	/**
	 * This is the midi for the midi_1 node that gets played when user clicks "play"
	 * TODO store this
	 */
	private val midiData = MidiData()

	/**
	 * Set to true whenever we should update the processing backend and UI with new data.
	 * This happens on first process frame / load or if user has changed any midi.
	 */
	private var dirty = true

	override fun onCreate() {
		createInputPort("in")
		createOutputPort("out", Format.MIDI)
	}

	override fun onInit() {}

	override fun onParameterChange(instance: BaseNodeData) {
		updateProperties(instance)
	}

	override fun onConnect(port: String) {}

	override fun onDisconnect(port: String) {}

	override fun onRemove() {}

	override fun onPrepareFrame(): InputFrameData {
		if (dirty || buffer.size > 0) {
			return MidiNodeInputFrameData(
					nodeId = id,

					midiDataMessage = if (dirty) {
						dirty = false
						MidiDataMessage(id, midiData.clone())
					} else {
						null
					},

					temporaryMidi = if (buffer.size > 0) {
						// TODO remove cancellations?
						val r = buffer.toTypedArray();
						buffer.clear();
						r
					} else {
						null
					}
			)
		}

		return MidiNodeInputFrameData(id)
	}

	override fun onFinishFrame(data: OutputFrameData) {}

	override fun onData(data: NodeDataMessage) {
		when (data) {
			is AddMidiMessage -> {
				println("Adding midi message $data")
				midiData.merge(data.midiData)
				dirty = true
			}
			is PushTangentMessage -> {
				buffer.add(shortArrayOf(144.toShort(), data.tangent, 64))
			}
			is ReleaseTangentMessage -> {
				buffer.add(shortArrayOf(128.toShort(), data.tangent, 64))
			}
			is AddEventZoneMessage -> {
				println("Adding EventZone id=${data.eventZoneId}, start=${data.start}, length=${data.length}")

				val eventZones = (properties as Data).eventZones ?: ArrayList()

				eventZones.add(Data.EventZone(
						id = data.eventZoneId,
						start = data.start,
						length = data.length,
						midi = MidiData()
				))

				updateProperties(Data(eventZones = eventZones))
			}
			is ChangeEventZoneMessage -> {
				println("Changing EventZone id=${data.eventZoneId}, start=${data.start}, length=${data.length}")

				val eventZones = (properties as Data).eventZones

				val eventZone = eventZones?.find { it.id == data.eventZoneId }

				if (eventZone != null) {
					eventZone.start = data.start
					eventZone.length = data.length
					updateProperties(Data(eventZones = eventZones))
				} else {
					println("WARNING: EventZone could not be changed as id=${data.eventZoneId} was not found")
				}
			}
			is RemoveEventZoneMessage -> {
				println("Removing EventZone id=${data.eventZoneId}")

				val eventZones = (properties as Data).eventZones
				if (eventZones != null) {
					if (eventZones.removeIf { it.id == data.eventZoneId }) {
						updateProperties(Data(eventZones = eventZones))
					}
				} else {
					println("WARNING: EventZone could not be removed as id=${data.eventZoneId} was not found")
				}
			}
		}
	}
}
