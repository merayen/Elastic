package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.out_1.OutNodeStatisticsMessage
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

/**
 * Takes audio or signal and sends it out of the current group.
 *
 * The parent node decides what happens with the data send into this node. If this node is under the topmost node,
 * it probably gets played onto your speakers.
 *
 * TODO We might want the parent node of this Out-node to actually read the data it receives, in the C code...? Not just forward it? The parent node probably wants to process the data... Maybe store the output data in a buffer instead? Or just let the parent node read the outlet connected to this node?
 */
class Out(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("float", "amplitude = 0")
				Member("float", "offset = 0")

				if (getInletType("in") == Format.SIGNAL) {
					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							Member("float", "sample = ${writeInlet("in")}.signal[sample_index]")
							If("fabsf(sample) > amplitude") {
								Statement("amplitude = fabsf(sample)")
							}
							Statement("offset += sample")
						}
					}

					Statement("offset /= $frameSize")
				}

				sendDataToBackend(codeWriter, "8") { result ->
					Statement("*(((float *)$result) + 0) = amplitude")
					Statement("*(((float *)$result) + 1) = offset")
				}
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		return listOf(OutNodeStatisticsMessage(nodeId, floatArrayOf(data.float), floatArrayOf(data.float)))
	}
}
