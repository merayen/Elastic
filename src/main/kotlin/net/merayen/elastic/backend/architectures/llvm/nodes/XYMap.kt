package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.cmethods.clamp
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.xy_map_1.CurveData
import net.merayen.elastic.backend.logicnodes.list.xy_map_1.StateUpdateData
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

class XYMap(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("float*", "curve")
				Member("int", "curve_length") // In floats
				Member("float", "position") // Manual position when fac-port is not connected
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			val input = getInletType("in")
			val fac = getInletType("fac")
			val out = getOutletType("out") ?: return

			if (out == Format.AUDIO)
				TODO("add support for outputting audio")

			if (out != Format.SIGNAL)
				return // Can't output

			with(codeWriter) {
				If("this->parameters.curve == NULL || this->parameters.curve_length <= 0") {
					writePanic(codeWriter, "No curve set")
				}

				if (fac == Format.SIGNAL) {
					val facLet = "${writeInlet("fac")}.signal[sample_index]"

					when (input) {
						Format.SIGNAL -> {
							val outLet = "${writeOutlet("out")}.signal[sample_index]"
							val inLet = "${writeInlet("in")}.signal[sample_index]"
							writeForEachVoice(codeWriter) {
								writeForEachSample(codeWriter) {
									Statement("$outLet = $inLet * this->parameters.curve[(int)roundf(${clamp(facLet)} * (this->parameters.curve_length - 1))]")
								}
							}

						}
						Format.AUDIO -> TODO()
						null -> {
							val outLet = "${writeOutlet("out")}.signal[sample_index]"
							writeForEachVoice(codeWriter) {
								writeForEachSample(codeWriter) {
									Statement("$outLet = this->parameters.curve[(int)roundf(${clamp(facLet)} * (this->parameters.curve_length - 1))]")
								}
							}
						}
						else -> {}
					}
				} else {
					TODO()
				}
			}
		}

		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				// TODO only send positions if any nodes are actively showing them
				if (getInletType("fac") == Format.SIGNAL) {
					alloc.writeCalloc(codeWriter, "float*", "positions", shared.voiceCount.toString(), "4")

					writeForEachVoice(codeWriter) {
						Statement("positions[voice_index] = ${writeInlet("fac")}.signal[${frameSize - 1}]")
					}

					Call("send", "${shared.voiceCount * 4}, positions")

					alloc.writeFree(codeWriter, "positions")
				}
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("*(char *)data == 0") {
					Statement("this->parameters.curve_length = (length - 1) / 4")
					If("this->parameters.curve != NULL") {
						alloc.writeFree(codeWriter, "this->parameters.curve")
					}
					alloc.writeMalloc(codeWriter, "this->parameters.curve", "length - 1")

					Call("memcpy", "this->parameters.curve, data + 1, length - 1")
				}
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		return listOf(
			StateUpdateData(nodeId, (0 until shared.voiceCount).map { data.float }.toFloatArray() )
		)
	}

	override fun onMessage(message: NodeDataMessage) {
		if (message is CurveData) {
			sendDataToDSP(1 + message.curve.size * 4) {
				it.put(0)
				for (x in message.curve)
					it.putFloat(x)
			}
		}
	}
}