package net.merayen.elastic.backend.data.eventdata

import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MidiDataIteratorTest {
	private var midiData: MidiData? = null

	@BeforeEach
	fun setUp() {
		val midiData = MidiData()

		// Add notes getting closer at the end
		for (i in 0 until 100) {
			midiData.add(
				MidiData.MidiChunk(
					i.toString(),
					1 - (1 / (i+1).toDouble()),
					shortArrayOf(i.toShort())
				)
			)
		}

		// Add notes at the same exact place, order should be kept (100 before 101)
		midiData.add(MidiData.MidiChunk("100", 1.0, shortArrayOf(100)))
		midiData.add(MidiData.MidiChunk("101", 1.0, shortArrayOf(101)))

		this.midiData = midiData
	}


	@Test
	fun testIteratorCount() {
		val midiData = midiData!!

		var count = 0
		for (midiChunk in midiData.iterator())
			count++

		Assertions.assertEquals(102, midiData.getMidiChunks().size)
		Assertions.assertEquals(102, count)
	}


	@Test
	fun testIteratorOrder() {
		val midiData = midiData!!

		var count = 0
		for (midiChunk in midiData)
				Assertions.assertEquals(count++, midiChunk.id!!.toInt())
	}


	@Test
	fun testIteratorWithOffset() {
		val midiData = midiData!!

		for (i in 0 until 100) {
			var count = i
			for (midiChunk in midiData.iterator(1 - 1 / (i + 1.0)))
				Assertions.assertEquals(count++, midiChunk.id!!.toInt())
		}
	}


	@Test
	fun testIteratorWhileRemovingAfter() {
		val midiData = midiData!!

		for (i in 0 until 102/2) {
			var count = 0
			for (midiChunk in midiData) {
				assertEquals(count, midiChunk.id!!.toInt())
				midiData.remove((count + 1).toString())
				count += 2
			}
		}
	}


	@Test
	fun testIteratorWhileRemovingBefore() {
		val midiData = midiData!!

		val iterator = midiData.iterator()
		iterator.next()
		iterator.next()
		midiData.remove("0")

		assertEquals("2", iterator.next().id)
	}
}