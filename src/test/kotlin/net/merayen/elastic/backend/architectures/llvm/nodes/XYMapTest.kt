package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.LLVMDSPModule
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.xy_map_1.CurveData
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

internal class XYMapTest : LLVMNodeTest() {
	@Test
	fun `fac input`() {
		val supervisor = init()
		supervisor.ingoing.send(
			listOf(
				NodeConnectMessage("signal1", "out", "xy_map", "fac"),
				NodePropertyMessage("signal1", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = 0.26f)),
				CurveData("xy_map", (0 until 11).map { it.toFloat() }.toFloatArray()),
				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertTrue(response.outSignal["out"]!!.all { abs(it - 3f) < 0.00001f })
		}

		supervisor.ingoing.send(
			listOf(
				NodePropertyMessage("signal1", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = .90001f)),
				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertTrue(response.outSignal["out"]!!.all { abs(it - 9f) < 0.000001f })
		}
	}

	@Test
	fun `with input and fac`() {
		val supervisor = init()
		supervisor.ingoing.send(
			listOf(
				NodeConnectMessage("signal0", "out", "xy_map", "in"),
				NodeConnectMessage("signal1", "out", "xy_map", "fac"),

				NodePropertyMessage("signal0", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = 100f)),
				NodePropertyMessage("signal1", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = 0.25f)),
				CurveData("xy_map", (0 until 11).map { it.toFloat() }.toFloatArray()),

				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
		assertTrue(response.outSignal["out"]!!.all { abs(it - 300f) < 0.00001f })
	}

	private fun init(): LLVMDSPModule {
		val supervisor = createSupervisor()

		supervisor.ingoing.send(
			listOf(
				CreateNodeMessage("signal0", "value", "top"),
				CreateNodePortMessage("signal0", "out", Format.SIGNAL),

				CreateNodeMessage("signal1", "value", "top"),
				CreateNodePortMessage("signal1", "out", Format.SIGNAL),

				CreateNodeMessage("xy_map", "xy_map", "top"),
				CreateNodePortMessage("xy_map", "in"),
				CreateNodePortMessage("xy_map", "fac"),
				CreateNodePortMessage("xy_map", "out", Format.SIGNAL),

				CreateNodeMessage("out", "out", "top"),
				CreateNodePortMessage("out", "in"),

				NodeConnectMessage("xy_map", "out", "out", "in"),
			)
		)

		return supervisor
	}
}