package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format

/**
 * Converts a signal to audio.
 *
 * Gives options on how the signal should be put into the audio tracks.
 */
class ToAudio(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteProcess(codeWriter: CodeWriter) {
			with(codeWriter) {
				if (getInletType("in") == Format.SIGNAL) { // TODO allow ports like in_0, in_1 etc, for spreading input onto audio channels
					writeForEachVoice(codeWriter) {
						writeForEachChannel(codeWriter) {
							writeForEachSample(codeWriter) {
								Statement("${writeOutlet("out")}.audio[sample_index + $frameSize * channel_index] = ${writeInlet("in")}.signal[sample_index]")
							}
						}
					}
				}
			}
		}
	}
}
