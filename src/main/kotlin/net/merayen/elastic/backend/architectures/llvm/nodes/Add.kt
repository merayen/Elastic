package net.merayen.elastic.backend.architectures.llvm.nodes

/**
 * Takes two inputs and adds them together.
 */
class Add(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = inExpressions.joinToString("+")
}