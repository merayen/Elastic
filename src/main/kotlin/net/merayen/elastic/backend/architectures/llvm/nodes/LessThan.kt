package net.merayen.elastic.backend.architectures.llvm.nodes

class LessThan(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = "${inExpressions[0]} < ${inExpressions[1]}"
}