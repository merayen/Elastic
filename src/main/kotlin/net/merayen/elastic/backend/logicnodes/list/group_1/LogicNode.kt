package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.Temporary
import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeAudioOut
import net.merayen.elastic.backend.logicnodes.list.output_1.OutputNodeStatisticsData
import net.merayen.elastic.backend.logicnodes.list.output_1.OutputNodeStatisticsMessage
import net.merayen.elastic.backend.mix.datatypes.Audio
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.backend.nodes.GroupLogicNode
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

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
					sendMessage(
						PlaybackStatusMessage(
							nodeId = id,
							currentPlayheadPosition = message.currentPlayheadPosition,
							currentBPM = message.currentBPM,
							isPlaying = isPlaying
						)
					)
				}
				// Count max channels
				val channelCount = message.outAudio.size

				if (channelCount == 0) {
					env.mixer.send(output_device, Audio(arrayOf(FloatArray(bufferSize)))) // Send some silence TODO do we need to do this?
					return  // Don't bother
				}

				val sampleCount = bufferSize // TODO should be a field sent from group-node? as it decides the frame size...?

				val out = arrayOfNulls<FloatArray>(channelCount)/* channel no *//* sample no */

				TODO("forward each out-node to correct output device...?")

				//for (channelNumber in 0 until channelCount) {
				//	val channel = message.outAudio[channelNumber]

				//	if (channel != null)
				//		out[channelNumber] = channel
				//	else
				//		out[channelNumber] = FloatArray(sampleCount)
				//}

				//sendDataToUI(
				//	OutputNodeStatisticsMessage(
				//		id,
				//		message.amplitudes,
				//		message.offsets
				//	)
				//)

				val mixer = env.mixer

				mixer.send(output_device, Audio(out))

				//val statistics = mixer.statistics[output_device]

				//if (statistics != null) {
				//	if (loltid < System.currentTimeMillis()) {
				//		println(statistics.describe())
				//		loltid = System.currentTimeMillis() + 1000
				//	}
				//	sendMessage(
				//		OutputNodeStatisticsData(
				//			id,
				//			statistics.id,
				//			statistics.available_before.avg,
				//			statistics.available_before.min,
				//			statistics.available_after.avg,
				//			statistics.available_after.min
				//		)
				//	)
				//}
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
