package net.merayen.elastic.backend.logicnodes.list.midi_1

import java.util.ArrayList

import net.merayen.elastic.backend.interfacing.types.MidiPacket
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode

class LogicNode : BaseLogicNode() {

	internal var buffer: MutableList<MidiPacket> = ArrayList()

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

	override fun onPrepareFrame(data: MutableMap<String, Any>) {
		val midi = arrayOfNulls<ShortArray>(buffer.size)

		var i = 0
		for (mp in buffer)
			midi[i++] = mp.midi

		buffer.clear()

		data["midi"] = midi
	}

	override fun onFinishFrame(data: Map<String, Any>) {}

	override fun onData(data: Map<String, Any>) {
		if (data.containsKey("tangent_down"))
			buffer.add(MidiPacket(shortArrayOf(144.toShort(), (data["tangent_down"] as Number).toShort(), 64), 0))
		if (data.containsKey("tangent_up"))
			buffer.add(MidiPacket(shortArrayOf(128.toShort(), (data["tangent_up"] as Number).toShort(), 64), 0))

		if (data.containsKey("moveEvent")) {
			//getParameter("eventZones") as
		}
	}

	fun getEventZones(): ArrayList<HashMap<String, Any>> {
		if (getParameter("eventZones") == null)
			set("eventZones", ArrayList<HashMap<String, Any>>())

		return getParameter("eventZones") as ArrayList<HashMap<String, Any>>
	}
}
