package net.merayen.elastic.backend.data.eventdata

import org.json.simple.JSONObject

class AudioEventData : EventData() {
	class Zone {
		var start = 0.0f
		var length = 0.0f
		var audioClip = String()
	}

	override fun dump(): JSONObject {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun restore(json: JSONObject) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

}