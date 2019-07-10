package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class PushTangentMessage(override val nodeId: String, val tangent: Short) : NodeDataMessage