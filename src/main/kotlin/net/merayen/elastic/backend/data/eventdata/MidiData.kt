package net.merayen.elastic.backend.data.eventdata

class MidiData(val midi: MutableList<MidiPacket> = ArrayList()) : Cloneable {

	/**
	 * A midi packet.
	 * @param id The unique ID of the midi event
	 * @param start Offset (in beats) this MidiPacket applies
	 * @param midi
	 */
	class MidiPacket(
			val id: String,
			val start: Float,
			val midi: ShortArray
	) : Cloneable {
		public override fun clone(): MidiPacket {
			return MidiPacket(id, start, midi.copyOf())
		}
	}

	public override fun clone(): MidiData {
		return MidiData(midi.map { it.clone() } as MutableList<MidiPacket>)
	}

	fun slice(from: Float = 0f, to: Float = Float.MAX_VALUE): MidiData {
		TODO()
	}
}