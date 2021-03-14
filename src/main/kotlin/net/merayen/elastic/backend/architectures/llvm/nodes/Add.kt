package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage

/**
 * Takes two inputs and adds them together.
 */
class Add(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {}

		override fun onWritePrepare(codeWriter: CodeWriter) {}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			writeForEachVoice(codeWriter) {
				with(codeWriter) {
					For("int i = 0", "i < ${shared.frameSize}", "i++") { // TODO change code returned based on connected ports?
						Statement("${writeOutlet("out")}.signal[i] = ${writeInlet("in1")}.signal[i] + ${writeInlet("in2")}.signal[i]")
					}
				}
			}
		}
	}
}