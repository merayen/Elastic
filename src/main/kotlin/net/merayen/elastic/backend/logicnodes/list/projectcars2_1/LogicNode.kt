package net.merayen.elastic.backend.logicnodes.list.projectcars2_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.pow

class LogicNode : BaseLogicNode() {
	companion object {
		val paramNames = listOf("rpm", "engine_power", "engine_torque", "fuel_level", "oil_temp", "throttle", "breaks", "clutch")
	}

	private class UDPDataReceiver private constructor() : Thread() {
		val socket = DatagramSocket(5606)
		private val buffer = ByteArray(2048)
		private var outputBuffer = ByteArray(559)
		val packet = DatagramPacket(buffer, buffer.size)
		var running = true
		private val lock = ReentrantLock()
		private val condition = lock.newCondition()
		private var hasData = false

		override fun run() {
			while (running) {
				socket.receive(packet)

				if (packet.length != 559)
					continue // Not the message we wanted

				lock.withLock {
					for (i in outputBuffer.indices)
						outputBuffer[i] = buffer[i]

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
				return outputBuffer
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
		for (param in paramNames)
			createOutputPort(param, Format.SIGNAL)
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

			rawTelemetry.position(18)
			val oilTemp = readByte() + (readByte().let { if (it < 0) (it + 256) else (it) } shl 8).toFloat()

			rawTelemetry.position(28)
			val fuelCapacity = rawTelemetry.get().toFloat()

			rawTelemetry.position(32)
			val fuelLevelRaw = rawTelemetry.int

			rawTelemetry.position(364)
			val engineTorque = rawTelemetry.float

			// TODO check this
			val fuelLevelLiters = if (fuelLevelRaw < 0) (fuelLevelRaw + 2.0.pow(32)).toInt() else fuelLevelRaw
			val fuelLevel = fuelLevelLiters / fuelCapacity

			rawTelemetry.position(40)
			val rpm = (readByte() + (readByte() shl 8)).toFloat()

			return ProjectCars2UDPData(
				id,
				rpm = rpm,
				throttle = throttle,
				breaks = breaks,
				clutch = clutch,
				enginePower = 0f,
				engineTorque = engineTorque,
				engineOn = true,
				running = true,
				fuelLevel = fuelLevel,
				oilTemp = oilTemp,
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