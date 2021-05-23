package net.merayen.elastic.backend.architectures.llvm.nodes

class Divide(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = inExpressions.joinToString("/")
}