package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class RemoveMidiMessage(
		override val nodeId: String,
		eventZoneId: String,
		id: String
) : NodeDataMessage