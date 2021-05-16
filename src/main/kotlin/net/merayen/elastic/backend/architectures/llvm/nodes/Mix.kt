package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.cmethods.clamp
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.mix_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage

class Mix(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("float", "mix")
				Member("float", "a_value")
				Member("float", "b_value")
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length != 5") {
					writePanic(codeWriter)
				}

				If("*(char *)data == 0") {
					Statement("this->parameters.mix = *(float *)(data + 1)")
				}
				ElseIf("*(char *)data == 1") {
					Statement("this->parameters.a_value = *(float *)(data + 1)")
				}
				ElseIf("*(char *)data == 2") {
					Statement("this->parameters.b_value = *(float *)(data + 1)")
				}
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			val a = getInletType("a")
			val b = getInletType("b")
			val fac = getInletType("fac")
			val out = getOutletType("out")

			with(codeWriter) {
				if (out == null)
					return // Nothing to do

				if (out != Format.SIGNAL)
					TODO("llvm mix node only supports Format.SIGNAL as output for now. Fix!")

				val outLet = "${writeOutlet("out")}.signal[sample_index]"

				// TODO if there is 1 audio on one of the ports, output audio and convert other input to audio if signal

				// Reset out port
				writeForEachVoice(codeWriter) {
					writeForEachSample(codeWriter) {
						Statement("$outLet = 0")
					}
				}

				if (a == null && b == null) {
					Member(
						"float",
						"value = " +
							"this->parameters.a_value * (1 - this->parameters.mix) + " +
							"this->parameters.b_value * this->parameters.mix"
					)
					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							Statement("$outLet = value")
						}
					}

				} else if (a == Format.SIGNAL && b == Format.SIGNAL) {
					val aLet = "${writeInlet("a")}.signal[sample_index]"
					val bLet = "${writeInlet("b")}.signal[sample_index]"

					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							if (fac == Format.SIGNAL) {
								val facLet = "${writeInlet("fac")}.signal[sample_index]"
								Member("float", "fac = $facLet")
								Statement("fac = ${clamp("fac")}")
								Statement("$outLet += $aLet * (1 - fac) + $bLet * fac")
							} else {
								Statement("$outLet += $aLet * (1 - this->parameters.mix) + $bLet * this->parameters.mix")
							}
						}
					}

				} else if (a == Format.SIGNAL && b == null) {
					val aLet = "${writeInlet("a")}.signal[sample_index]"

					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							if (fac == Format.SIGNAL) {
								val facLet = "${writeInlet("fac")}.signal[sample_index]"
								Member("float", "fac = $facLet")
								Statement("fac = ${clamp("fac")}")
								Statement("$outLet += $aLet * (1 - fac) + this->parameters.b_value * fac")
							} else {
								Statement("$outLet += $aLet * (1 - this->parameters.mix) + this->parameters.b_value * this->parameters.mix")
							}
						}
					}

				} else if (a == null && b == Format.SIGNAL) {
					val bLet = "${writeInlet("b")}.signal[sample_index]"

					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							if (fac == Format.SIGNAL) {
								val facLet = "${writeInlet("fac")}.signal[sample_index]"
								Member("float", "fac = $facLet")
								Statement("fac = ${clamp("fac")}")
								Statement("$outLet += this->parameters.a_value * (1 - fac) + $bLet * fac")
							} else {
								Statement("$outLet += this->parameters.a_value * (1 - this->parameters.mix) + $bLet * this->parameters.mix")
							}
						}
					}
				}
			}
		}
	}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties

		instance.mix?.let {
			sendDataToDSP(1 + 4) { buffer ->
				buffer.put(0)
				buffer.putFloat(it)
			}
		}
		instance.aValue?.let {
			sendDataToDSP(1 + 4) { buffer ->
				buffer.put(1)
				buffer.putFloat(it)
			}
		}
		instance.bValue?.let {
			sendDataToDSP(1 + 4) { buffer ->
				buffer.put(2)
				buffer.putFloat(it)
			}
		}
	}
}
