package net.merayen.elastic.backend.architectures.llvm.nodes

class Modulo(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>) = "fmod(${inExpressions[0]}, ${inExpressions[1]})"
}