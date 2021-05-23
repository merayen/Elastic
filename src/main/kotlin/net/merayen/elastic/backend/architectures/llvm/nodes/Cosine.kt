package net.merayen.elastic.backend.architectures.llvm.nodes

class Cosine(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = "cosf(${inExpressions[0]})"
}