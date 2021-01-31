package net.merayen.elastic.backend.logicnodes.list.output_1

import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice
import net.merayen.elastic.backend.mix.datatypes.Audio
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	companion object {
		val knownOutputDevices = arrayOf(
			"oracle_java:Default Audio Device", // Mac OS X 10.9
			"oracle_java:PulseAudio Mixer", // Ubuntu 16.04
			"oracle_java:default [default]"
		)
	}
	private var output_device: String? = null

	override fun onInit() {
		createInputPort("input")

		val env = env as JavaBackend.Environment
		for (ad in env.mixer.availableDevices)
			if (ad is AudioDevice)
				if (ad.isOutput)
					if (ad.id in knownOutputDevices)
						output_device = ad.id
	}

	override fun onConnect(port: String) {}

	override fun onDisconnect(port: String) {}

	override fun onParameterChange(instance: BaseNodeProperties) {
		updateProperties(instance)
	}

	override fun onFinishFrame(data: OutputFrameData?) {
		if (data == null)  // FIXME Should this really be checked for? This seem to happen if node gets created while processing a frame in the DSP backend
			return

 		val output = data as Output1NodeOutputData

		// Count max channels
		val channelCount = output.audio.size

		if (channelCount == 0)
			return  // Don't bother

		val sampleCount = output.bufferSize

		val out = arrayOfNulls<FloatArray>(channelCount)/* channel no *//* sample no */

		for (channelNumber in 0 until channelCount) {
			val channel = data.audio[channelNumber]

			if (channel != null)
				out[channelNumber] = channel
			else
				out[channelNumber] = FloatArray(sampleCount)
		}

		sendDataToUI(
				OutputNodeStatisticsMessage(
						id,
						data.amplitudes,
						data.offsets
				)
		)

		val mixer = env.mixer

		mixer.send(output_device, Audio(out))

		val statistics = mixer.statistics[output_device]

		if (statistics != null) {
			if (loltid < System.currentTimeMillis()) {
				println(statistics.describe())
				loltid = System.currentTimeMillis() + 1000
			}
			sendMessage(
					OutputNodeStatisticsData(
							id,
							statistics.id,
							statistics.available_before.avg,
							statistics.available_before.min,
							statistics.available_after.avg,
							statistics.available_after.min
					)
			)
		}
	}
	private var loltid = System.currentTimeMillis()

	override fun onRemove() {}
	override fun onData(data: NodeDataMessage) {}
}
