package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData
import kotlin.reflect.KClass

/**
 * Doesn't do anything, other than having children.
 */
class LogicNode : BaseLogicNode() {
	override fun getParameterRegistry() = mapOf<String,Class<Any>>(
			//"bpm" to java.lang.Number.
	)

	override fun onCreate() {}
	override fun onInit() {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData) {}
	override fun onData(data: NodeDataMessage) {
		if (data is SetBPMMessage) {
			set("bpm", data.bpm)
		}
	}
	override fun onParameterChange(key: String, value: Any) = set(key, value)
}
