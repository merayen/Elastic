package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import java.util.ArrayList

import net.merayen.elastic.backend.interfacing.types.MidiPacket
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData
import net.merayen.elastic.util.UniqueID

class LogicNode : BaseLogicNode() {
	/**
	 * Midi buffer, with midi data made by the user, e.g when clicking the piano roll or doing something that should
	 * give immediate feedback.
	 */
	internal var buffer: ArrayList<ShortArray> = ArrayList()

	private val parameters = Parameters(this)

	/**
	 * This is the midi for the midi_1 node that gets played when user clicks "play"
	 * TODO store this
	 */
	private val midiData = MidiData()

	/**
	 * Set to true whenever we should pdate the processing backend and UI with new data.
	 * This happens on first process frame / load or if user has changed any midi.
	 */
	private var dirty = true

	override fun onCreate() {
		createPort(object : BaseLogicNode.PortDefinition() {
			init {
				name = "in"
			}
		})

		createPort(object : BaseLogicNode.PortDefinition() {
			init {
				name = "out"
				output = true
				format = Format.MIDI
			}
		})
	}

	override fun onInit() {}

	override fun onParameterChange(key: String, value: Any) {
		set(key, value)

		when (key) {
			"mute" -> System.out.println("Mute! Got it!")
			"trackName" -> System.out.println("Midi node received track-name '${value as String}'")
		}
	}

	override fun onConnect(port: String) {}

	override fun onDisconnect(port: String) {}

	override fun onRemove() {}

	override fun onPrepareFrame(): InputFrameData {
		if (dirty || buffer.size > 0) {
			return MidiNodeInputFrameData(
					id,

					if (dirty) {
						dirty = false
						MidiDataMessage(id, midiData.clone())
					} else {
						null
					},

					if (buffer.size > 0) {
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
			is PushTangentMessage -> {
				buffer.add(shortArrayOf(144.toShort(), data.tangent, 64))
			}
			is ReleaseTangentMessage -> {
				buffer.add(shortArrayOf(128.toShort(), data.tangent, 64))
			}
			is AddEventZoneMessage -> {
				println("Adding EventZone id=${data.eventZoneId}, start=${data.start}, length=${data.length}")
				val eventZones = parameters.getEventZones()
				val eventZone = Parameters.EventZone()
				eventZone.id = data.eventZoneId
				eventZone.start = data.start
				eventZone.length = data.length
				eventZones.add(eventZone)

				parameters.setEventZones(eventZones)
			}
			is ChangeEventZoneMessage -> {
				println("Changing EventZone id=${data.eventZoneId}, start=${data.start}, length=${data.length}")
				val eventZones = parameters.getEventZones()
				val eventZone = eventZones.find { it.id == data.eventZoneId }

				if (eventZone != null) {
					eventZone.start = data.start
					eventZone.length = data.length
					parameters.setEventZones(eventZones)
				}
			}
			is RemoveEventZoneMessage -> {
				println("Removing EventZone id=${data.eventZoneId}")
				val eventZones = parameters.getEventZones()
				val eventZone = eventZones.removeIf { it.id == data.eventZoneId }
				parameters.setEventZones(eventZones)
			}
		}
	}
}
