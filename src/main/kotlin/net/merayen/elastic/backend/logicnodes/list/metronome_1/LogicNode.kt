package net.merayen.elastic.backend.logicnodes.list.metronome_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onCreate() {
		createOutputPort("audio", Format.AUDIO)
		createOutputPort("midi", Format.MIDI)
	}

	override fun onInit() {}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}

	override fun onFinishFrame(data: OutputFrameData?) {
		if (data !is Metronome1OutputFrameData) return

		val currentBeat = data.currentBeat
		val currentDivision = data.currentDivision

		if (currentBeat != null && currentDivision != null)
			sendMessageToUI(MetronomeBeatMessage(id, current = currentBeat, division = currentDivision))
	}
}