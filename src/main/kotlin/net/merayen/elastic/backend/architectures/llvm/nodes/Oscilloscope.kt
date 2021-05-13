package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.oscilloscope_1.OscilloscopeSignalDataMessage
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

class Oscilloscope(nodeId: String) : TranspilerNode(nodeId) {
	private val outputSize = 200

	override val nodeClass = object : NodeClass() {
		override fun onWriteMembers(codeWriter: CodeWriter) {
			with(codeWriter) {
				// Properties
				Member("float", "amplitude")
				Member("float", "offset")
				Member("float", "time")
				Member("float", "trigger")

				// Internal values
				Member("float", "samples[$outputSize]") // Output value
				Member("int", "samples_position")
				Member("int", "input_position")
				Member("bool", "been_below_trigger")
				Member("long", "next_forced_trigging")
				Member("bool", "trigging")
				Member("bool", "frame_finished")
				Member("float", "min_value")
				Member("float", "max_value")
				Member("bool", "samples_available")
				Member("long", "time_in_samples") // Time in sample count
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteInit(codeWriter, allocComponent)
			with(codeWriter) {
				Statement("this->samples_position = -1")
				Statement("this->time = 0.001")
			}
		}

		override fun onWritePrepare(codeWriter: CodeWriter) {
			with(codeWriter) {
				Statement("$instanceVariable->frame_finished = false")
				Statement("$instanceVariable->samples_available = false")
				Statement("$instanceVariable->min_value = 1E+37")
				Statement("$instanceVariable->max_value = 1E-37")

			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("$instanceVariable->frame_finished") {
					//Return() // TODO fix supervisor so that we don't need to do this?
					writePanic(codeWriter, "Seems like we are run multiple times by the supervisor. That should perhaps not happen?")
				}

				// Figure out how often we should sample the signal
				Member("int", "sample_every = ${shared.sampleRate} * $instanceVariable->time / $outputSize")

				If("sample_every < 1") {
					Statement("sample_every = 1")
				}

				// The buffer we will sample from. It is made of several channels and voices
				Member("float", "buffer[$frameSize]")
				Call("memset", "buffer, 0, $frameSize * sizeof(float)")

				if (getInletType("in") == Format.SIGNAL) {
					// Find the min and max values, and sample all the voices
					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {

							Statement("buffer[sample_index] += ${writeInlet("in")}.signal[sample_index]")

							Member("float", "sample = buffer[sample_index]")

							If("sample < $instanceVariable->min_value") {
								Statement("$instanceVariable->min_value = sample")
							}
							If("sample > $instanceVariable->max_value") {
								Statement("$instanceVariable->max_value = sample")
							}
						}
					}

				} else if (getInletType("in") == Format.AUDIO) {
					TODO("Implement audio support in oscilloscope")
				}

				// The main sampling logic that creates the oscilloscope window
				writeForEachSample(codeWriter) {
					Member("float", "sample = buffer[sample_index]")
					If("!$instanceVariable->trigging") {
						If("$instanceVariable->next_forced_trigging < $instanceVariable->time_in_samples + sample_index") {
							Statement("$instanceVariable->trigging = true")
							Statement("$instanceVariable->input_position = 0")
							Statement("$instanceVariable->samples_position = 0")
							Statement("$instanceVariable->been_below_trigger = false")
						}
						ElseIf("sample * $instanceVariable->amplitude + 0.01f < $instanceVariable->trigger") {
							Statement("$instanceVariable->been_below_trigger = true")
						}
						ElseIf("$instanceVariable->been_below_trigger && sample >= $instanceVariable->trigger") {
							Statement("$instanceVariable->trigging = true")
							Statement("$instanceVariable->input_position = 0")
							Statement("$instanceVariable->samples_position = 0")
							Statement("$instanceVariable->been_below_trigger = false")
						}

						If("$instanceVariable->trigging") {
							If("$instanceVariable->input_position++ % sample_every == 0") {
								Statement("$instanceVariable->samples[$instanceVariable->samples_position++] = (sample * $instanceVariable->offset) * $instanceVariable->amplitude")
								If("$instanceVariable->samples_position == $outputSize") {
									Statement("$instanceVariable->samples_position = -1")
									Statement("$instanceVariable->been_below_trigger = false")
									Statement("$instanceVariable->next_forced_trigging = $instanceVariable->time_in_samples + ${shared.sampleRate / 10}") // We never trigger less often than 100ms
									Statement("$instanceVariable->trigging = false")
									Statement("$instanceVariable->samples_available = true")
									writePanic(codeWriter, "Det virker") // TODO make us get here
									Break()
								}
							}
						}
					}
				}

				Statement("$instanceVariable->time_in_samples += $frameSize")
				Statement("$instanceVariable->frame_finished = true")
			}
		}

		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				val length = outputSize * 4 + 4 + 4
				alloc.writeMalloc(codeWriter, "void*", "result", "$length")

				//If("$instanceVariable->max_value != 1.0f") {
				//	writePanic(codeWriter, "Noes %f", "$instanceVariable->max_value")
				//}
				Call("memcpy", "result, $instanceVariable->samples, ${outputSize * 4}")
				Statement("*(float *)(result + ${outputSize * 4}) = $instanceVariable->min_value")
				Statement("*(float *)(result + ${outputSize * 4 + 4}) = $instanceVariable->max_value")

				//If("*(float *)(result + ${outputSize * 4 + 4}) != 1.0f") {
				//	writePanic(codeWriter, "What")
				//}

				Call("send", "$length, result")

				alloc.writeFree(codeWriter, "result")
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		val expectedSize = outputSize * 4 + 4 + 4
		if (data.limit() != expectedSize)
			error("Expected $expectedSize bytes from oscilloscope node, got ${data.remaining()}")

		val samples = FloatArray(outputSize)

		for (i in 0 until outputSize)
			samples[i] = data.float

		val minValue = data.float
		val maxValue = data.float

		return listOf(
			OscilloscopeSignalDataMessage(
				nodeId = nodeId,
				samples = samples.toTypedArray(),
				minValue = minValue,
				maxValue = maxValue,
			)
		)
	}
}
