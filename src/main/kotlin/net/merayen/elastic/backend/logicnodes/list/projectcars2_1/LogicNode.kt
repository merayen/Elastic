package net.merayen.elastic.backend.logicnodes.list.projectcars2_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LogicNode : BaseLogicNode() {
	private class UDPDataReceiver private constructor() : Thread() {
		val socket = DatagramSocket(5606)
		private val buffer = ByteArray(2048)
		private var outputBuffer = ByteArray(buffer.size)
		val packet = DatagramPacket(buffer, buffer.size)
		var running = true
		private val lock = ReentrantLock()
		private val condition = lock.newCondition()
		private var hasData = false

		override fun run() {
			while (running) {
				socket.receive(packet)
				lock.withLock {
					for ((i, x) in buffer.withIndex())
						outputBuffer[i] = x

					hasData = true

					condition.await()
				}
			}
		}

		fun fetch(): ByteArray? {
			lock.withLock {
				condition.signal()
				if (!hasData)
					return null

				hasData = false
				return buffer
			}
		}

		companion object {
			var instance = UDPDataReceiver()

			init {
				instance.start()
			}
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
	}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}

	override fun onPrepareFrame(): InputFrameData {
		val data = UDPDataReceiver.instance.fetch()
		if (data != null) {
			val rawTelemetry = ByteBuffer.wrap(data)
			fun readByte(): Int {
				val r = rawTelemetry.get()
				return if (r < 0) r + 256 else r.toInt()
			}

			rawTelemetry.position(13)
			val throttle = readByte() / 255f
			val breaks = readByte() / 255f

			rawTelemetry.position(16)
			val clutch = readByte() / 255f

			rawTelemetry.position(40)
			val rpm = (readByte() + readByte() shl 8).toFloat()

			return ProjectCars2UDPData(
				id,
				rpm = rpm,
				throttle = throttle,
				breaks = breaks,
				clutch = clutch,
				hp = 0f,
				nm = 0f,
				engineOn = true,
				running = true,
			)
		} else {
			return super.onPrepareFrame()
		}
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
}