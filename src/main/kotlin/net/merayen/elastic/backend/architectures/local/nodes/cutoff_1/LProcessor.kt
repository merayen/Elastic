package net.merayen.elastic.backend.architectures.local.nodes.cutoff_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor

class LProcessor : LocalProcessor() {
	override fun onInit() {}
	override fun onPrepare() {}

	override fun onProcess() {
		val audioIn = getInlet("in")
		val audioOut = getOutlet("out")

		if (audioOut != null) {
			TODO()
		} else if (audioIn != null) {
			audioIn.read = audioIn.outlet.written
		}
	}

	override fun onDestroy() {}
}