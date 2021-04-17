package net.merayen.elastic.backend.architectures.llvm.nodes

class Oscilloscope(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
	}
}
