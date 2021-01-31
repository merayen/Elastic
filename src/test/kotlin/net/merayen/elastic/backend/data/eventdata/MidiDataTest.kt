package net.merayen.elastic.backend.data.eventdata

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.util.JSONObjectMapper
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MidiDataTest {
	private var midiData: MidiData? = null

	@BeforeEach
	fun setUp() {
		val midiData = MidiData()
		midiData.add(MidiData.MidiChunk("0", 0.0, arrayListOf(0)))
		midiData.add(MidiData.MidiChunk("2", 2.0, arrayListOf(2)))
		midiData.add(MidiData.MidiChunk("1", 1.0, arrayListOf(1)))

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

		midiChunks[0].midi!!.add(1337)

		assertEquals(0, clonedMidiData.getMidiChunks()[0].midi!![0])
	}

	@Test
	fun testMerge() {
		val midiData = midiData!!

		val newMidiData = MidiData()

		newMidiData.add(MidiData.MidiChunk("10", 1.0, arrayListOf(10.toShort())))
		newMidiData.add(MidiData.MidiChunk("11", 1.5, arrayListOf(10)))
		newMidiData.add(MidiData.MidiChunk("12", 2.1, arrayListOf(10)))

		midiData.merge(newMidiData)

		assertEquals("10", midiData.getMidiChunks()[2].id)
		assertEquals("11", midiData.getMidiChunks()[3].id)
		assertEquals("12", midiData.getMidiChunks()[5].id)
	}

	@Test
	fun testSerialization() {
		data class Data(
			var midiData: MidiData? = null
		) : BaseNodeProperties()

		val midiData = midiData!!

		val data = Data(midiData)

		val mapper = JSONObjectMapper()
		mapper.registerClass(Data::class)
		mapper.registerClass(MidiData::class)
		mapper.registerClass(MidiData.MidiChunk::class)

		val result = mapper.toObject(JSONParser().parse(JSONObject.toJSONString(mapper.toMap(data))) as Map<*, *>) as Data

		val resultMidiData = result.midiData!!

		assertNotNull(resultMidiData.midi)
		assertNotNull(resultMidiData.midi!![1])
		assertNotNull(resultMidiData.midi!![1].midi)

		for ((index, midiChunk) in resultMidiData.withIndex()) {
			assertEquals(index.toString(), midiChunk.id)
			assertEquals(index, midiChunk.midi!![0].toInt())
		}
	}
}