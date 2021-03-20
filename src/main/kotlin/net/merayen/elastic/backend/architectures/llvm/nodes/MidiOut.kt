package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

/**
 * Sends midi out somewhere (e.g a hardware/software device).
 */
class MidiOut(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteProcess(codeWriter: CodeWriter) {
			with(codeWriter) {
				writeForEachVoice(codeWriter) {
					writePanic(codeWriter, "Works!")
				}
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		println("Data received from MidiOut: $data")

		return listOf()
	}
}