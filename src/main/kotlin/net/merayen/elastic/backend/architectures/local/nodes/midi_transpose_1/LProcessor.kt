package net.merayen.elastic.backend.architectures.local.nodes.midi_transpose_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.Inlet
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet
import net.merayen.elastic.backend.midi.MidiStatuses
import net.merayen.elastic.util.Postmaster
import java.util.*

class LProcessor : LocalProcessor() {
	private val keysDown = ArrayDeque<Short>()
	private var transpose = 0

	override fun onInit() {
		transpose = (localNode as LNode).transpose
	}

	override fun onPrepare() {}
	override fun onProcess() {
		val input: Inlet? = getInlet("in")
		val output = getOutlet("out") as? MidiOutlet

		if (input is MidiInlet) {
			val available = available()
			if (available > 0) {
				var midiFrame: MidiOutlet.MidiFrame? = null

				while (true) {
					midiFrame = input.getNextMidiFrame(available)
					if (midiFrame == null)
						break

					if (output != null) {
						for (midiPacket in midiFrame) {
							when {
								midiPacket[0] == MidiStatuses.KEY_DOWN -> {
									val alteredMidiPacket = midiPacket.copyOf()

									alteredMidiPacket[1] = transposeKey(midiPacket[1])

									keysDown.add(midiPacket[1])

									output.putMidi(midiFrame.framePosition, alteredMidiPacket)
								}
								midiPacket[0] == MidiStatuses.KEY_UP -> {
									keysDown.remove(midiPacket[1])
									output.putMidi(midiFrame.framePosition, shortArrayOf(MidiStatuses.KEY_UP, transposeKey(midiPacket[1])))
								}
								else -> output.putMidi(midiFrame.framePosition, midiPacket)
							}
						}
					}
				}

				input.read += available

				if (input.read == buffer_size && output != null) {
					val localNode = localNode as LNode

					if (localNode.transpose != transpose) {
						for (key in keysDown)
							output.putMidi(buffer_size - 1, shortArrayOf(MidiStatuses.KEY_UP, transposeKey(key), 0))

						keysDown.clear()

						transpose = localNode.transpose
					}
				}

				output?.written = input.read

				output?.push()
			}/* else {
				input.read += available
				output?.written = buffer_size

				output?.push()
			}*/
		} else {
			input?.read = buffer_size
			output?.written = buffer_size

			output?.push()
		}
	}

	private fun transposeKey(key: Short) = (key + transpose).toShort()

	override fun onMessage(message: Postmaster.Message?) {}
	override fun onDestroy() {}
}