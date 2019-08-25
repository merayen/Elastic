package net.merayen.elastic.backend.data.eventdata

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MidiDataTest {
	private var midiData: MidiData? = null

	@BeforeEach
	fun setUp() {
		val midiData = MidiData()
		midiData.add(MidiData.MidiChunk("0", 0.0, arrayOf(shortArrayOf(0))))
		midiData.add(MidiData.MidiChunk("2", 2.0, arrayOf(shortArrayOf(2))))
		midiData.add(MidiData.MidiChunk("1", 1.0, arrayOf(shortArrayOf(1))))

		this.midiData = midiData
	}


	@Test
	fun testOrder() {
		val midiData = midiData!!

		val midiChunks = midiData.getMidiChunks()
		assertEquals("0", midiChunks[0].id)
		assertEquals("1", midiChunks[1].id)
		assertEquals("2", midiChunks[2].id)
	}


	@Test
	fun testClone() {
		val midiData = midiData!!

		val clonedMidiData = midiData.clone()

		val midiChunks = midiData.getMidiChunks()

		midiChunks[0].midi[0] = shortArrayOf(1337)

		assertEquals(0, clonedMidiData.getMidiChunks()[0].midi[0][0])
	}
}