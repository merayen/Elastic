package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage
import kotlin.math.sin

/**
 * Outputs signals, like a sine.
 */
class Wave(nodeId: String) : TranspilerNode(nodeId) {
	private enum class Operation {
		CHANGE_MODE,
		SET_FREQUENCY,
	}

	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("char", "type")
				Member("double", "frequency")
				Member("double", "position[${shared.voiceCount}]") // In cycles
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
				Else {
					writePanic(codeWriter, "Invalid operation")
				}
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			if (!hasOutlet("out")) // Outlet is not connected, no reason to output anything
				return

			if (getInletType("frequency") == Format.SIGNAL)
				writePanic(codeWriter, "Not implemented frequency inlet")

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
	}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties

		val type = instance.type
		val frequency = instance.frequency

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
	}
}
