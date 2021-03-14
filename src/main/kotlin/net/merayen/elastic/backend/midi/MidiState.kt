package net.merayen.elastic.backend.midi

import net.merayen.elastic.backend.data.eventdata.MidiData
import kotlin.experimental.and

/**
 * Feed me midi-packets, and I will pick up configuration and such.
 * Also handles MidiData.MidiChunk, that makes this class aware of time.
 */
abstract class MidiState {

	class Tangent(
		val id: String? = null,
		val start: Double,
		val tangent: Short,
		val velocity: Float
	)

	// States
	private var rpn_lsb: Short = 127
	private var rpn_msb: Short = 127

	private var data_entry_lsb: Short = 0
	private var data_entry_msb: Short = 0
	protected open fun onPitchBendSensitivityChange(semitones: Float) {}

	/**
	 * When sustain-pedal is pushed.
	 * value: 0-63 = Off, 64-127 = On
	 * value < 0.5 --> No sustain
	 * value >= 0.5 --> Sustain on
	 */
	protected open fun onSustain(value: Float) {}

	protected open fun onPitchChange(semitones: Float) {}
	protected open fun onVolumeChange(volume: Float) {}
	protected open fun onKeyDown(tangent: Short, velocity: Float) {}
	protected open fun onKeyUp(tangent: Short) {}

	/**
	 * All midi-packets are sent through this one after all the other events.
	 */
	protected open fun onMidi(midiPacket: ShortArray) {}

	/**
	 * When an error occurs. Like, when midi packets are sent in wrong order.
	 * When an error occurs, we reset our current read state.
	 */
	protected fun onError() {}

	// Public states that can be read
	/**
	 * Time in beats. Only used if handle() is used with MidiData.MidiChunk
	 */
	var time = 0.0
		private set

	/**
	 * ID of the current midi packet id, if any.
	 * Is null if there are no id.
	 */
	var currentMidiPacketId: String? = null
		private set

	/**
	 * In semitones. Range of pitch bending.
	 */
	var bendRange = 2f
		private set

	var currentMidiPacket: ShortArray = ShortArray(0)
		private set

	var volume = 1f
		private set

	var velocity = 1f
		private set

	var sustain = 1f
		private set

	var pitch = 0f
		private set

	val activeKeys = HashMap<Short, Tangent>()

	fun handle(midiPacket: ShortArray, id: String? = null) {
		currentMidiPacket = midiPacket
		currentMidiPacketId = id

		val status = midiPacket[0] and 0b11110000
		when {
			status == MidiStatuses.KEY_DOWN && midiPacket[2] > 0 -> {
				velocity = midiPacket[2] / 127f
				onKeyDown(midiPacket[1], midiPacket[2] / 127f)
				activeKeys[midiPacket[1]] = Tangent(id, time, midiPacket[1], midiPacket[2] / 127f)
			}

			status == MidiStatuses.KEY_UP || status == MidiStatuses.KEY_DOWN && midiPacket[2] == 0.toShort() -> {
				onKeyUp(midiPacket[1])
				activeKeys.remove(midiPacket[1])
			}

			status == MidiStatuses.PITCH_CHANGE -> {
				pitch = MidiUtils.midiPitchToFloat(midiPacket) * bendRange / 2
				onPitchChange(pitch)
			}

			midiPacket[0] == MidiStatuses.MOD_CHANGE -> when {
				midiPacket[1] == MidiControllers.DATA_ENTRY_MSB -> {
					data_entry_msb = midiPacket[2]
					dataEntryUpdate()
				}

				midiPacket[1] == MidiControllers.DATA_ENTRY_LSB -> {
					data_entry_lsb = midiPacket[2]
					dataEntryUpdate()
				}

				midiPacket[1] == MidiControllers.RPN_LSB -> rpn_lsb = midiPacket[2]
				midiPacket[1] == MidiControllers.RPN_MSB -> rpn_msb = midiPacket[2]
				midiPacket[1] == MidiControllers.SUSTAIN -> {
					sustain = midiPacket[2] / 127f
					onSustain(sustain)
				}

				midiPacket[1] == MidiControllers.CHANNEL_VOLUME_MSB -> {
					volume = midiPacket[2] / 127f
					onVolumeChange(volume)
				}
			}
		}

		onMidi(midiPacket)
	}

	fun handle(midiPacket: ShortArray) = handle(midiPacket, null)

	fun handle(midiPacket: Array<Short>) = handle(midiPacket.toShortArray(), null)

	fun handle(midiMessage: MidiData.MidiMessage) {
		time = midiMessage.start!!
		handle(midiMessage.midi!!.toShortArray(), midiMessage.id!!)
	}

	private fun dataEntryUpdate() {
		if (rpn_lsb.toInt() == 0 && rpn_msb.toInt() == 0) {
			bendRange = data_entry_msb + data_entry_lsb / 50f
			onPitchBendSensitivityChange(bendRange)
		}
	}
}