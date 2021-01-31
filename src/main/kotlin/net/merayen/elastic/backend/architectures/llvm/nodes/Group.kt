package net.merayen.elastic.backend.architectures.llvm.nodes

class Group(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex), GroupInterface {
	override val nodeClass = object : NodeClass() {}
}