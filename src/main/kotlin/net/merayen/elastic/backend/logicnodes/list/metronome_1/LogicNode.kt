package net.merayen.elastic.backend.logicnodes.list.metronome_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage

class LogicNode : BaseLogicNode() {
	override fun onInit() {
		createOutputPort("audio", Format.AUDIO)
		createOutputPort("midi", Format.MIDI)
	}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}

	override fun onData(data: NodeDataMessage?) {
		if (data !is Metronome1OutputFrameData) return

		val currentBeat = data.currentBeat
		val currentDivision = data.currentDivision

		if (currentBeat != null && currentDivision != null)
			sendToUI(MetronomeBeatMessage(id, current = currentBeat, division = currentDivision))
	}

	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
}