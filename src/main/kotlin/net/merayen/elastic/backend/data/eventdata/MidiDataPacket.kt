package net.merayen.elastic.backend.data.eventdata

data class MidiDataPacket(
	var id: String? = null,
	var start: Number? = null,
	var midi: Array<Number>? = null
) : Cloneable {
	public override fun clone() = MidiDataPacket(id, start, midi?.clone())
}