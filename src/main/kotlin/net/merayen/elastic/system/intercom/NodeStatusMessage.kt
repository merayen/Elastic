package net.merayen.elastic.system.intercom

import net.merayen.elastic.util.Postmaster

class NodeStatusMessage : Postmaster.Message, NodeMessage {
	val _nodeId: String
	val load: Float
	val voices: Int
	val processCount: Int

	constructor(nodeId: String, load: Float, voices: Int, processCount: Int) {
		this._nodeId = nodeId
		this.load = load
		this.voices = voices
		this.processCount = processCount
	}

	override fun getNodeId() = _nodeId
}