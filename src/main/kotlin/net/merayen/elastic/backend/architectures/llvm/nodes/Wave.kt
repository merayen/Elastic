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

	private val sampleRatio: Float
		get() = shared.sampleRate / waveSize.toFloat()

	private val minFactor: Float
		get() = sampleRatio / (shared.sampleRate / 4f)

	private val maxFactor: Float
		get() = sampleRatio / 4f


	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("char", "type")
				Member("double", "frequency")
				Member("double", "position[${shared.voiceCount}]") // In cycles
				Member("float", "wave[$waveSize]")
				Member("void*", "resampler[${shared.voiceCount}]")
				Member("int", "resampler_wave_position[${shared.voiceCount}]")
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

			with(codeWriter) {
				Statement("this->parameters.position[voice_index] = 0")

				// TODO if Type.CURVE is not set, don't allocate?
				Statement("this->parameters.resampler[voice_index] = resample_open(1, $minFactor, $maxFactor)")
			}
		}

		override fun onWriteDestroyVoice(codeWriter: CodeWriter) {
			super.onWriteDestroyVoice(codeWriter)
			with(codeWriter) {
				Call("resample_close", "this->parameters.resampler[voice_index]")
			}
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
							If("this->parameters.type == ${Properties.Type.SINE.ordinal}") {
								writeForEachSample(codeWriter) {
									Statement("${writeOutlet("out")}.signal[sample_index] = (float)sin(this->parameters.position[voice_index] * 2 * M_PI)")
									Statement("this->parameters.position[voice_index] += ${writeInlet("frequency")}.signal[sample_index] / 44100.0")
								}
							}
							ElseIf("this->parameters.type == ${Properties.Type.CURVE.ordinal}") {
								Statement("int inBufferUsed = 0")

								// TODO the factor should probably change more often in the frame, to have it more evenly change
								if (getInletType("frequency") == Format.SIGNAL)
									Statement("double factor = ${shared.sampleRate / waveSize} / ${writeInlet("frequency")}.signal[0]") // TODO Sample input? Average?
								else
									Statement("double factor = 1") // TODO implement frequency from setting

								// Clamp factor value
								If("factor < $minFactor") {
									Statement("factor = $minFactor")
								}
								ElseIf("factor > $maxFactor") {
									Statement("factor = $maxFactor")
								}

								Statement("int output_samples_processed = 0")

								Statement("int i = 0")
								For("", "output_samples_processed < ${shared.frameSize} && i < 10000", "i++") {
									Statement(
										"""
										int resample_result = resample_process( 
											this->parameters.resampler[voice_index],
											factor,
											this->parameters.wave + this->parameters.resampler_wave_position[voice_index],
											$waveSize - this->parameters.resampler_wave_position[voice_index],
											0,
											&inBufferUsed,
											${writeOutlet("out")}.signal + output_samples_processed,
											${shared.frameSize} - output_samples_processed
										)
									""".trimIndent()
									)

									If("resample_result < 0") {
										writePanic(codeWriter, "libresample returned %i", "resample_result")
									}

									Statement("output_samples_processed += resample_result")

									// Loop the input wave buffer
									Statement("this->parameters.resampler_wave_position[voice_index] += inBufferUsed")
									Statement("this->parameters.resampler_wave_position[voice_index] %= $waveSize")
								}
								If("i >= 10000") {
									writePanic(codeWriter, "Endless loop protection")
								}
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
						ElseIf("this->parameters.type == ${Properties.Type.CURVE.ordinal}") {
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
