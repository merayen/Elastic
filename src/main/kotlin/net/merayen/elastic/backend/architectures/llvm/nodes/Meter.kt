package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.meter_1.MeterSignalData
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

class Meter(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			codeWriter.Member("float", "value")
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			with(codeWriter) {
				codeWriter.Statement("this->parameters.value = 0.0f")
				if (getInletType("in") == Format.SIGNAL) {
					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							Member("float", "value = fabsf(${writeInlet("in")}.signal[sample_index])")
							If("value > this->parameters.value") {
								Statement("this->parameters.value = value")
							}
						}
					}
				}
			}
		}

		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				Call("send", "4, &this->parameters.value")
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		return listOf(MeterSignalData(nodeId, value = data.float))
	}
}