package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage

/**
 * Outputs signals, like a sine.
 */
class Wave(nodeId: String) : TranspilerNode(nodeId) {
	private enum class Operation {
		CHANGE_MODE,
		SET_FREQUENCY,
		SET_CURVE,
	}

	private val waveSize = 256

	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("char", "type")
				Member("double", "frequency")
				Member("double", "position[${shared.voiceCount}]") // In cycles
				Member("float", "wave[$waveSize]")
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteInit(codeWriter, allocComponent)

			with (codeWriter) {
				Statement("this->parameters.type = 0")
				Statement("this->parameters.frequency = 1000")
			}
		}

		override fun onWriteCreateVoice(codeWriter: CodeWriter) {
			super.onWriteCreateVoice(codeWriter)

			codeWriter.Statement("this->parameters.position[voice_index] = 0")
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length < 1") {
					writePanic(codeWriter, "Length is less than 1")
				}

				If("*((char *)data) == ${Operation.CHANGE_MODE.ordinal} && length == 2") {
					Statement("this->parameters.type = *((char *)(data + 1))")
				}
				ElseIf("*((char *)data) == ${Operation.SET_FREQUENCY.ordinal} && length == 5") {
					Statement("this->parameters.frequency = (double)*((float *)(data + 1))")
				}
				ElseIf("*((char *)data) == ${Operation.SET_CURVE.ordinal} && length > 1") {
					If("(length - 1) % (4*6) != 0") {
						writePanic(codeWriter, "Invalid curve length")
					}
					Call(
						"b2f_calc_curve",
						"(struct b2f_Dot *)(data + 1), (length - 1) / sizeof(struct b2f_Dot), this->parameters.wave, $waveSize, 2"
					)
				}
				Else {
					writePanic(codeWriter, "Invalid operation")
				}
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			if (!hasOutlet("out")) // Outlet is not connected, no reason to output anything
				return

			when (getInletType("frequency")) {
				Format.SIGNAL -> {
					with(codeWriter) {
						writeForEachVoice(codeWriter) {
							writeForEachSample(codeWriter) {
								Statement("${writeOutlet("out")}.signal[sample_index] = (float)sin(this->parameters.position[voice_index] * 2 * M_PI)")
								Statement("this->parameters.position[voice_index] += ${writeInlet("frequency")}.signal[sample_index] / 44100.0")
							}
						}
					}
				}
				null -> {
					with(codeWriter) {
						Statement("double frequency = this->parameters.frequency")
						Statement("double step = frequency / ${shared.sampleRate}")
						writeLog(codeWriter, "frequency %f", "frequency")
						If("this->parameters.type == ${Properties.Type.SINE.ordinal}") { // No frequency input for now
							writeForEachVoice(codeWriter) {
								Statement("double position = this->parameters.position[voice_index]")
								writeForEachSample(codeWriter) {
									Statement("${writeOutlet("out")}.signal[sample_index] = (float)sin(position * 2 * M_PI)")
									Statement("position += step")
								}
								Statement("this->parameters.position[voice_index] = position")
							}
						}
						Else {
							writePanic(codeWriter, "Unknown operation %i", "(int)this->parameters.type")
						}
					}
				}
				else -> {} // Whatever, we are silent
			}
		}
	}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties

		val type = instance.type
		val frequency = instance.frequency
		val curve = instance.curve

		if (type != null) {
			sendDataToDSP(1 + 1) {
				it.put(Operation.CHANGE_MODE.ordinal.toByte())
				it.put(Properties.Type.valueOf(type).ordinal.toByte())
			}
		}

		if (frequency != null) {
			sendDataToDSP(1 + 4) {
				it.put(Operation.SET_FREQUENCY.ordinal.toByte())
				it.putFloat(frequency)
			}
		}

		if (curve != null) {
			sendDataToDSP(1 + 4 * curve.size) {
				it.put(Operation.SET_CURVE.ordinal.toByte())
				for (x in curve)
					it.putFloat(x)
			}
		}
	}
}
