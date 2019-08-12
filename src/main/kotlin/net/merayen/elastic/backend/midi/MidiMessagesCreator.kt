package net.merayen.elastic.backend.midi

import kotlin.math.max
import kotlin.math.min

object MidiMessagesCreator {
	fun keyDown(tangent: Int, weight: Float): ShortArray {
		val t = min(12*6, max(0, tangent)).toShort()
		val w = min(127f, max(0f, weight * 127)).toShort()
		return shortArrayOf(MidiStatuses.KEY_DOWN, t, w)
	}

	fun keyUp(tangent: Int, weight: Float = 0f): ShortArray {
		val t = min(12*6, max(0, tangent)).toShort()
		val w = (min(1f, max(0f, weight)) * 127).toShort()
		return shortArrayOf(MidiStatuses.KEY_UP, t, w)
	}

	fun changePitchBendRange(semitones: Float): Array<ShortArray> {
		return arrayOf(
				shortArrayOf(MidiStatuses.MOD_CHANGE, MidiControllers.RPN_LSB, 0),
				shortArrayOf(MidiStatuses.MOD_CHANGE, MidiControllers.RPN_MSB, 0),
				shortArrayOf(MidiStatuses.MOD_CHANGE, MidiControllers.DATA_ENTRY_MSB, semitones.toShort()),
				shortArrayOf(MidiStatuses.MOD_CHANGE, MidiControllers.DATA_ENTRY_LSB, 0) // Not supporting cents. FIXME
		)
	}

	fun changePitch(value: Float): ShortArray {
		val v = min(1f, max(-1f, value))
		return if (v < 0)
			shortArrayOf(MidiStatuses.PITCH_CHANGE, 0, (127 + v * 63).toShort())
		else
			shortArrayOf(MidiStatuses.PITCH_CHANGE, 0, (v * 63).toShort())
	}
}
