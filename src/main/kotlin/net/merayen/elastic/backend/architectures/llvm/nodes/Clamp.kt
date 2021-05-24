package net.merayen.elastic.backend.architectures.llvm.nodes

class Clamp(nodeId: String) : BaseMath(nodeId) {
	override fun onWriteProcessSample(inExpressions: List<String>): String {
		return "${inExpressions[1]} < ${inExpressions[0]} ? ${inExpressions[0]} : ${inExpressions[1]} > ${inExpressions[2]} ? ${inExpressions[2]} : ${inExpressions[1]}}}}}"
	}
}