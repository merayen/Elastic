package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.lever_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage

class Lever(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("float", "value")
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length != 4") {
					writePanic(codeWriter, "Unexpected length")
				}

				Statement("this->parameters.value = *(float *)data")
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			with(codeWriter) {
				if (getOutletType("fac") == Format.SIGNAL) {
					Member("float", "value = this->parameters.value")
					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							Statement("${writeOutlet("fac")}.signal[sample_index] = value")
						}
					}
				}
			}
		}
	}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties
		val value = instance.value

		if (value != null) {
			sendDataToDSP(4) {
				it.putFloat(value)
			}
		}
	}
}