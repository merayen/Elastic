package net.merayen.elastic.backend.architectures.llvm.nodes

class Subtract(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = inExpressions.joinToString("-")
}