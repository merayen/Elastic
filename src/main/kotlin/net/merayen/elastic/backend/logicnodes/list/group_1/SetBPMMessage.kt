package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class SetBPMMessage(override val nodeId: String, val bpm: Int) : NodeDataMessage