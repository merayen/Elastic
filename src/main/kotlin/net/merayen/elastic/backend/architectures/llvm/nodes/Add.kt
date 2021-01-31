package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import java.nio.ByteBuffer

class Add(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {}

		override fun onWritePrepare(codeWriter: CodeWriter) {}

		// TODO change code returned based on connected ports?
		override fun onWriteProcess(codeWriter: CodeWriter) {
			writeForEachVoice(codeWriter) {
				codeWriter.For("int i = 0", "i < ${shared.frameSize}", "i++") {
					codeWriter.Statement("${writeOutlet("out")}.signal[i] = ${writeInlet("in1")}.signal[i] + ${writeInlet("in2")}.signal[i]")
				}
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {}
		override fun onWriteCreateVoice(codeWriter: CodeWriter) {}
		override fun onWriteDestroyVoice(codeWriter: CodeWriter) {}
	}

	override fun onPrepareFrame() {}
	override fun onMessage(message: NodePropertyMessage) {}
	override fun onMessage(message: NodeDataMessage) {}
}