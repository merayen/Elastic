package net.merayen.elastic.backend.logicnodes.list.frequency_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class FrequencyUpdateMessage(override val nodeId: String, var poles: FloatArray? = null) : NodeDataMessage