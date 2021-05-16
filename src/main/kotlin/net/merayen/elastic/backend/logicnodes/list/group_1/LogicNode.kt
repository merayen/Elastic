package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.Temporary
import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice
import net.merayen.elastic.backend.mix.datatypes.Audio
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.backend.nodes.GroupLogicNode
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage

/**
 * Doesn't do anything, other than having children.
 */
class LogicNode : BaseLogicNode(), GroupLogicNode {
	private var startPlaying = false
	private var stopPlaying = false
	private var playheadPosition: Float? = null
	private var rangeSelectionStart: Float? = null
	private var rangeSelectionStop: Float? = null
	private var bpm = 120.0
	private var isPlaying = false

	private var sampleRate: Int = Temporary.sampleRate
	private var bufferSize: Int = Temporary.bufferSize
	private var depth: Int = Temporary.depth

	private var nextReportToUI = System.currentTimeMillis()

	companion object {
		val knownOutputDevices = arrayOf(
			"oracle_java:Default Audio Device", // Mac OS X 10.9
			"oracle_java:PulseAudio Mixer", // Ubuntu 16.04
			"oracle_java:default [default]"
		)
	}

	private var output_device: String? = null

	override fun onInit() {
		createInputPort("in")

		val env = env as JavaBackend.Environment
		for (ad in env.mixer.availableDevices)
			if (ad is AudioDevice)
				if (ad.isOutput)
					if (ad.id in knownOutputDevices)
						output_device = ad.id
	}

	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}


	private var loltid = System.currentTimeMillis()
	override fun onData(message: NodeDataMessage) {
		when (message) {
			is SetBPMMessage -> {
				bpm = message.bpm.toDouble()
				updateProperties(Properties(bpm = bpm.toInt()))
			}
			is TransportStartPlaybackMessage -> {
				startPlaying = true
				isPlaying = true
			}
			is TransportStopPlaybackMessage -> {
				stopPlaying = true
				isPlaying = false
			}
			is Group1OutputFrameData -> {
				if (nextReportToUI < System.currentTimeMillis()) {
					nextReportToUI = System.currentTimeMillis() + 50
					sendToUI(
						PlaybackStatusMessage(
							nodeId = id,
							currentPlayheadPosition = message.currentPlayheadPosition,
							currentBPM = message.currentBPM,
							isPlaying = isPlaying
						)
					)
				}

				// TODO handle outgoing midi
				// TODO handle audio out

				if (message.outAudio.isEmpty() && message.outSignal.isEmpty()) {
					env.mixer.send(output_device, Audio(arrayOf(FloatArray(bufferSize)))) // Send silence TODO necessary?
					return  // Don't bother
				}

				// TODO should bufferSize be a field sent from group-node? as it decides the frame size...?

				// Stereo output. TODO make dynamic and individual for each out-node
				val result = listOf(FloatArray(bufferSize), FloatArray(bufferSize)) // Stereo, whatever

				// Signal
				for ((_, samples) in message.outSignal) {
					// TODO handle device mapping on out-node basis (send audio to mapped devices)
					for ((i, sample) in samples.withIndex()) {
						result[0][i] += sample
						result[1][i] += sample
					}
				}

				val mixer = env.mixer

				mixer.send(output_device, Audio(result.toTypedArray()))
			}
		}
	}

	override fun onParameterChange(instance: BaseNodeProperties) {
		instance as Properties

		val bpm = instance.bpm
		val playheadPosition = instance.playheadPosition
		val rangeSelectionStart = instance.rangeSelectionStart
		val rangeSelectionStop = instance.rangeSelectionStop

		if (bpm != null)
			this.bpm = bpm.toDouble()

		if (playheadPosition != null)
			this.playheadPosition = playheadPosition

		if (rangeSelectionStart != null && rangeSelectionStop != null) {
			this.rangeSelectionStart = rangeSelectionStart
			this.rangeSelectionStop = rangeSelectionStop
		}

		updateProperties(instance)
	}

	override fun onPrepareFrame(): InputFrameData {
		val data = Group1InputFrameData(
			id,
			startPlaying = startPlaying,
			stopPlaying = stopPlaying,
			playheadPosition = playheadPosition,
			bpm = bpm, // TODO remove this and use a curve instead
			sampleRate = sampleRate,
			bufferSize = bufferSize,
			depth = depth
		)

		startPlaying = false
		stopPlaying = false
		playheadPosition = null

		return data
	}
}
