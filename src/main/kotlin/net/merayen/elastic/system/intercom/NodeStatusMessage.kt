package net.merayen.elastic.system.intercom

data class NodeStatusMessage(override val nodeId: String, val load: Float, val voices: Int, val processCount: Int) : NodeMessage