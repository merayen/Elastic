package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.list.value_1.Properties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage

/**
 * A node that adds two inputs.
 */
class Value(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			codeWriter.Member("float", "value")
		}

		override fun onWritePrepare(codeWriter: CodeWriter) {}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			writeForEachVoice(codeWriter) {
				codeWriter.For("int i = 0", "i < ${shared.frameSize}", "i++") { // TODO replace with writeForEachSamplw
					codeWriter.Statement("${writeOutlet("out")}.signal[i] = ${writeOuterParameterVariable("value")}")
				}
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length != 5") {
					writePanic(codeWriter, "Length must be 5")
				}

				If("*(unsigned char *)(data) != 0") {
					writePanic(codeWriter, "Type should always be 0")
				}

				Statement("${writeOuterParameterVariable("value")} = *(float *)(data + 1)")
				log?.write(codeWriter, "Node $nodeId received value %f", "*(float *)(data + 1)")
			}
		}

		override fun onWriteCreateVoice(codeWriter: CodeWriter) {}
		override fun onWriteDestroyVoice(codeWriter: CodeWriter) {}
	}

	override fun onPrepareFrame() {}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties
		val value = instance.value
		if (value != null) {
			sendDataToDSP(5) {
				it.put(0)
				it.putFloat(value)
			}
		}
	}

	override fun onMessage(message: NodeDataMessage) {}
}
