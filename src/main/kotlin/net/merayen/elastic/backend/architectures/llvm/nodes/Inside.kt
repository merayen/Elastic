package net.merayen.elastic.backend.architectures.llvm.nodes

class Inside(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = "${inExpressions[0]} <= ${inExpressions[1]} && ${inExpressions[1]} <= ${inExpressions[2]}"
}