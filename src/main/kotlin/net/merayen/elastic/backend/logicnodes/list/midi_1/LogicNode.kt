package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData
import java.util.*

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

	override fun onInit() {
		createInputPort("in")
		createOutputPort("out", Format.MIDI)
	}

	override fun onParameterChange(instance: BaseNodeProperties) {
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
						val r = buffer.toTypedArray()
						buffer.clear()
						r
					} else {
						null
					}
			)
		}

		return MidiNodeInputFrameData(id)
	}

	override fun onData(data: NodeDataMessage) {
		when (data) {
			is AddMidiMessage -> {
				midiData.merge(data.midiData)
				dirty = true
				val properties = properties as Properties

				val eventZone = properties.eventZones?.first { it.id == data.eventZoneId }
				if (eventZone == null) {
					println("EventZone ${data.eventZoneId} not found")
					return
				}

				eventZone.midi!!.merge(data.midiData)

				// Mark eventZone as have being updated
				updateProperties(Properties(eventZones = properties.eventZones))
			}
			is PushTangentMessage -> {
				buffer.add(shortArrayOf(144.toShort(), data.tangent, 64))
			}
			is ReleaseTangentMessage -> {
				buffer.add(shortArrayOf(128.toShort(), data.tangent, 64))
			}
			is AddEventZoneMessage -> {
				val eventZones = (properties as Properties).eventZones ?: Properties.EventZones()

				eventZones.add(Properties.EventZone(
						id = data.eventZoneId,
						start = data.start,
						length = data.length,
						midi = MidiData()
				))

				updateProperties(Properties(eventZones = eventZones))
			}
			is ChangeEventZoneMessage -> {
				val eventZones = (properties as Properties).eventZones

				val eventZone = eventZones?.find { it.id == data.eventZoneId }

				if (eventZone != null) {
					eventZone.start = data.start
					eventZone.length = data.length
					updateProperties(Properties(eventZones = eventZones))
				} else {
					println("WARNING: EventZone could not be changed as id=${data.eventZoneId} was not found")
				}
			}
			is RemoveEventZoneMessage -> {
				println("Removing EventZone id=${data.eventZoneId}")

				val eventZones = (properties as Properties).eventZones
				if (eventZones != null) {
					if (eventZones.removeIf { it.id == data.eventZoneId }) {
						updateProperties(Properties(eventZones = eventZones))
					}
				} else {
					println("WARNING: EventZone could not be removed as id=${data.eventZoneId} was not found")
				}
			}
		}
	}
}
