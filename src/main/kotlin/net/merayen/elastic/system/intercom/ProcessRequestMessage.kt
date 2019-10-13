package net.merayen.elastic.system.intercom

import java.util.*

class ProcessRequestMessage : ElasticMessage {
	/**
	 * Data to be sent into the processing backend
	 */
	val input: MutableMap<String, InputFrameData> = HashMap()
}