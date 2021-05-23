package net.merayen.elastic.backend.architectures.llvm.nodes

class Sine(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = "sinf(${inExpressions[0]})"
}