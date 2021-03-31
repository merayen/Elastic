package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

/**
 * Sends midi out somewhere (e.g a hardware/software device).
 *
 * TODO delete? Just use the out-node...?
 */
class MidiOut(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteProcess(codeWriter: CodeWriter) {
			if (getInletType("in") == Format.MIDI) {
				with(codeWriter) {
					writeForEachVoice(codeWriter) {
						//writePanic(codeWriter, "Works!")
						writeLog(codeWriter, "MidiOut supposed to process a frame, voice %i", "voice_index")
					}
				}
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		println("Data received from MidiOut: $data")

		return listOf()
	}
}