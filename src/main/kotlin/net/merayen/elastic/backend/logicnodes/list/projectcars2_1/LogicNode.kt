package net.merayen.elastic.backend.logicnodes.list.projectcars2_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.net.DatagramPacket
import java.net.DatagramSocket

class LogicNode : BaseLogicNode() {
	class UDPDataReceiver private constructor() {
		val socket = DatagramSocket(5606)
		private val buffer = ByteArray(1024)

		fun fetch(): DatagramPacket {
			val packet = DatagramPacket(buffer, buffer.size)
			socket.receive(packet)
			return packet
		}

		companion object {
			var instance = UDPDataReceiver()
		}
	}

	override fun onInit() {
		createOutputPort("rpm", Format.SIGNAL)
		createOutputPort("nm", Format.SIGNAL)
		createOutputPort("hp", Format.SIGNAL)
		createOutputPort("running", Format.SIGNAL) // 1.0f if game is running, no pause screen
		createOutputPort("engine_on", Format.SIGNAL) // 1.0f if engine is on
		createOutputPort("throttle", Format.SIGNAL)
		createOutputPort("break", Format.SIGNAL)
		createOutputPort("clutch", Format.SIGNAL)

		println(UDPDataReceiver.instance)
	}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}

	override fun onPrepareFrame(): InputFrameData {
		UDPDataReceiver.instance.fetch()
		return super.onPrepareFrame()
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
}