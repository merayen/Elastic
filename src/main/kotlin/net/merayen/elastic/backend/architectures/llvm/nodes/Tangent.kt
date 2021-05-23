package net.merayen.elastic.backend.architectures.llvm.nodes

class Tangent(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = "tanf(${inExpressions[0]})"
}