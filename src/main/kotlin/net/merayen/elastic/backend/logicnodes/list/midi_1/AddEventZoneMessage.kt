package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class AddEventZoneMessage(override val nodeId: String, val eventZoneId: String, val start: Float, val length: Float) : NodeDataMessage