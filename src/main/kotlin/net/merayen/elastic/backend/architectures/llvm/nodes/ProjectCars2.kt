package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.system.intercom.NodeDataMessage

class ProjectCars2(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {}

	override fun onMessage(message: NodeDataMessage) {
		TODO("Forward the Project Cars 2 data")
		sendDataToDSP(123) {
			TODO()
		}
	}
}