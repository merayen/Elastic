package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class RemoveEventZoneMessage(override val nodeId: String, val eventZoneId: String) : NodeDataMessage