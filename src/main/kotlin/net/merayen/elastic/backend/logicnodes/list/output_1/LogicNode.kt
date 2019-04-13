package net.merayen.elastic.backend.logicnodes.list.output_1

import java.util.HashMap

import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice
import net.merayen.elastic.backend.logicnodes.Environment
import net.merayen.elastic.backend.mix.datatypes.Audio
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.system.intercom.NodeDataMessage

class LogicNode : BaseLogicNode() {
	private var output_device: String? = null

	override fun onCreate() {
		createPort(object : BaseLogicNode.PortDefinition() {
			init {
				name = "input"
			}
		})
	}

	override fun onInit() {
		val env = env as Environment
		for (ad in env.mixer.availableDevices)
			if (ad is AudioDevice)
				if (ad.isOutput)
					if (ad.id == "oracle_java:Default Audio Device" ||// Mac OS X 10.9
							ad.id == "oracle_java:PulseAudio Mixer" // Ubuntu 16.04
					)
						output_device = ad.id
	}

	override fun onConnect(port: String) {}

	override fun onDisconnect(port: String) {}

	override fun onParameterChange(key: String, value: Any) {
		set(key, value)
	}

	override fun onPrepareFrame(data: Map<String, Any>) {}

	override fun onFinishFrame(data: Map<String, Any>) {
		val fa = data["audio"] as Array<FloatArray>

		// Count max channels
		val channel_count = fa.size

		if (channel_count == 0)
			return  // Don't bother

		val sample_count = this.env.buffer_size

		val out = arrayOfNulls<FloatArray>(channel_count)/* channel no *//* sample no */

		var i = 0
		for (channel_no in 0 until channel_count) {
			val channel = fa[channel_no]

			if (channel != null)
				out[i] = channel
			else
				out[i] = FloatArray(sample_count)

			i++
		}

		val vuLeft = data["vuLeft"]
		if (vuLeft != null)
			sendDataToUI(object : HashMap<String, Any>() {
				init {
					put("vuLeft", vuLeft)
				}
			})

		val offset = data["offset"]
		if (offset != null)
			sendDataToUI(object : HashMap<String, Any>() {
				init {
					put("offset", offset)
				}
			})

		val mixer = (env as Environment).mixer

		mixer.send(output_device, Audio(out))

		val statistics = mixer.statistics[output_device]

		if (statistics != null) {
			val nodeData = HashMap<String, Any>()

			val stats = HashMap<String, Any>()
			stats["available_before_avg"] = statistics.available_before.avg
			stats["available_before_min"] = statistics.available_before.min
			stats["available_after_avg"] = statistics.available_after.avg
			stats["available_after_min"] = statistics.available_after.min
			nodeData["statistics"] = stats

			sendMessageToUI(NodeDataMessage(id, nodeData))
		}
	}

	override fun onRemove() {}

	override fun onData(data: Any) {}
}
